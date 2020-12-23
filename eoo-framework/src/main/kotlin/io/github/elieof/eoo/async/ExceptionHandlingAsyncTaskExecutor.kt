package io.github.elieof.eoo.async

import mu.KotlinLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.task.AsyncTaskExecutor
import java.util.concurrent.Callable
import java.util.concurrent.Future

private val logger = KotlinLogging.logger {}

/**
 * Define a specific message for all exception happening while running an async task
 */
public open class ExceptionHandlingAsyncTaskExecutor(
    private val executor: AsyncTaskExecutor
) : AsyncTaskExecutor, InitializingBean, DisposableBean {
    public companion object {
        public const val EXCEPTION_MESSAGE: String = "Caught async exception"
    }

    override fun execute(task: Runnable) {
        executor.execute(createWrappedRunnable(task))
    }

    override fun execute(task: Runnable, startTimeout: Long) {
        executor.execute(createWrappedRunnable(task), startTimeout)
    }

    private fun <T> createCallable(task: Callable<T>): Callable<T> {
        return Callable {
            try {
                task.call()
            } catch (e: Exception) {
                handle(e)
                throw e
            }
        }
    }

    private fun createWrappedRunnable(task: Runnable): Runnable {
        return Runnable {
            try {
                task.run()
            } catch (e: Exception) {
                handle(e)
            }
        }
    }

    /**
     *
     * handle.
     *
     * @param e a [Exception] object.
     */
    protected open fun handle(e: Exception) {
        logger.error(EXCEPTION_MESSAGE, e)
    }

    override fun submit(task: Runnable): Future<*> {
        return executor.submit(createWrappedRunnable(task))
    }

    override fun <T> submit(task: Callable<T>): Future<T> {
        return executor.submit(createCallable(task))
    }

    @Throws(Exception::class)
    override fun destroy() {
        if (executor is DisposableBean) {
            executor.destroy()
        }
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        if (executor is InitializingBean) {
            executor.afterPropertiesSet()
        }
    }
}
