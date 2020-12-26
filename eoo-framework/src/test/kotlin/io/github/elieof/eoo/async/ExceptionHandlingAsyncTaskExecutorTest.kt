package io.github.elieof.eoo.async

import io.github.elieof.eoo.test.LogbackRecorder
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.SimpleAsyncTaskExecutor
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException

class ExceptionHandlingAsyncTaskExecutorTest {
    companion object {
        private val exception = RuntimeException("Eek")
        private const val testResult = 42
    }

    private var done = false
    private var handled: Exception? = null
    private lateinit var task: MockAsyncTaskExecutor
    private lateinit var executor: ExceptionHandlingAsyncTaskExecutor
    private lateinit var recorder: LogbackRecorder

    @BeforeEach
    fun setup() {
        done = false
        handled = null
        task = spyk(MockAsyncTaskExecutor())
        executor = TestExceptionHandlingAsyncTaskExecutor(task)
        recorder = LogbackRecorder.forClass(ExceptionHandlingAsyncTaskExecutor::class.java).reset().capture("ALL")
    }

    @AfterEach
    fun teardown() {
        recorder.release()
    }

    @Test
    fun testExecuteWithoutException() {
        val runnable = spyk(MockRunnableWithoutException())
        var caught: Throwable? = null
        try {
            synchronized(executor) {
                executor.execute(runnable)
                (executor as java.lang.Object).wait(100)
            }
        } catch (x: Exception) {
            caught = x
        }
        assertThat(done).isEqualTo(true)
        verify { runnable.run() }
        assertThat(caught).isNull()
        assertThat(handled).isNull()
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testExecuteWithException() {
        val runnable = spyk(MockRunnableWithException())
        var caught: Throwable? = null
        try {
            synchronized(executor) {
                executor.execute(runnable, AsyncTaskExecutor.TIMEOUT_INDEFINITE)
                (executor as java.lang.Object).wait(100)
            }
        } catch (x: InterruptedException) {
            // This should never happen
            throw Error(x)
        } catch (x: java.lang.Exception) {
            caught = x
        }
        assertThat(done).isEqualTo(true)
        verify { runnable.run() }
        assertThat(caught).isNull()
        assertThat(handled).isEqualTo(exception)
        val events = recorder.play()
        assertThat(events).hasSize(1)
        val event = events[0]
        assertThat(event.level).isEqualTo("ERROR")
        assertThat(event.message).isEqualTo(ExceptionHandlingAsyncTaskExecutor.EXCEPTION_MESSAGE)
        assertThat(event.thrown).isEqualTo(exception.toString())
    }

    @Test
    fun testSubmitRunnableWithoutException() {
        val runnable = spyk(MockRunnableWithoutException())
        val future = executor.submit(runnable)
        val caught = Assertions.catchThrowable { future.get() }
        assertThat(done).isEqualTo(true)
        verify { runnable.run() }
        assertThat(caught).isNull()
        assertThat(handled).isNull()
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testSubmitRunnableWithException() {
        val runnable: Runnable = spyk(MockRunnableWithException())
        val future = executor.submit(runnable)
        val caught = Assertions.catchThrowable { future.get() }
        assertThat(done).isEqualTo(true)
        verify { runnable.run() }
        assertThat(caught).isNull()
        assertThat(handled).isEqualTo(exception)
        val events = recorder.play()
        assertThat(events).hasSize(1)
        val event = events[0]
        assertThat(event.level).isEqualTo("ERROR")
        assertThat(event.message).isEqualTo(ExceptionHandlingAsyncTaskExecutor.EXCEPTION_MESSAGE)
        assertThat(event.thrown).isEqualTo(exception.toString())
    }

    @Test
    fun testSubmitCallableWithoutException() {
        val callable: Callable<Int> = spyk(MockCallableWithoutException())
        val future = executor.submit(callable)
        val caught = Assertions.catchThrowable {
            assertThat(future.get()).isEqualTo(42)
        }
        assertThat(done).isEqualTo(true)
        assertThat(caught).isNull()
        assertThat(handled).isNull()
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testSubmitCallableWithException() {
        val callable: Callable<Int> = spyk(MockCallableWithException())
        val future = executor.submit(callable)
        val caught = Assertions.catchThrowable { future.get() }
        assertThat(done).isEqualTo(true)
        assertThat(caught).isInstanceOf(ExecutionException::class.java)
        assertThat(caught.cause).isEqualTo(handled)
        assertThat(handled).isEqualTo(exception)
        val events = recorder.play()
        assertThat(events).hasSize(1)
        val event = events[0]
        assertThat(event.level).isEqualTo("ERROR")
        assertThat(event.message).isEqualTo(ExceptionHandlingAsyncTaskExecutor.EXCEPTION_MESSAGE)
        assertThat(event.thrown).isEqualTo(exception.toString())
    }

    @Test
    fun testInitializingExecutor() {
        task = spyk(MockAsyncInitializingTaskExecutor())
        executor = TestExceptionHandlingAsyncTaskExecutor(task)
        val caught = Assertions.catchThrowable {
            executor.afterPropertiesSet()
            verify { task.afterPropertiesSet() }
        }
        assertThat(caught).isNull()
    }

    @Test
    fun testNonInitializingExecutor() {
        val caught = Assertions.catchThrowable {
            executor.afterPropertiesSet()
            verify(exactly = 0) { task.afterPropertiesSet() }
        }
        assertThat(caught).isNull()
    }

    @Test
    fun testDisposableExecutor() {
        task = spyk(MockAsyncDisposableTaskExecutor())
        executor = TestExceptionHandlingAsyncTaskExecutor(task)
        val caught = Assertions.catchThrowable {
            executor.destroy()
            verify { task.destroy() }
        }
        assertThat(caught).isNull()
    }

    @Test
    fun testNonDisposableExecutor() {
        val caught = Assertions.catchThrowable {
            executor.destroy()
            verify(exactly = 0) { task.destroy() }
        }
        assertThat(caught).isNull()
    }

    inner class TestExceptionHandlingAsyncTaskExecutor internal constructor(executor: AsyncTaskExecutor) :
        ExceptionHandlingAsyncTaskExecutor(executor) {
        override fun handle(e: Exception) {
            synchronized(executor) {
                handled = e
                super.handle(e)
                (executor as java.lang.Object).notifyAll()
            }
        }
    }

    inner class MockRunnableWithoutException : Runnable {
        override fun run() {
            synchronized(executor) {
                done = true
                (executor as java.lang.Object).notifyAll()
            }
        }
    }

    inner class MockRunnableWithException : Runnable {
        override fun run() {
            synchronized(executor) {
                done = true
                throw exception
            }
        }
    }

    inner class MockCallableWithoutException : Callable<Int> {
        override fun call(): Int {
            done = true
            return testResult
        }
    }

    inner class MockCallableWithException : Callable<Int> {
        override fun call(): Int {
            done = true
            throw exception
        }
    }

    private open class MockAsyncTaskExecutor : SimpleAsyncTaskExecutor() {
        fun afterPropertiesSet() {}
        fun destroy() {}
    }

    private class MockAsyncInitializingTaskExecutor : MockAsyncTaskExecutor(), InitializingBean

    private class MockAsyncDisposableTaskExecutor : MockAsyncTaskExecutor(), DisposableBean
}
