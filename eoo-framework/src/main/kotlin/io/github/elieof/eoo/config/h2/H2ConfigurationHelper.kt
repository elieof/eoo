@file:Suppress("TooGenericExceptionCaught")

package io.github.elieof.eoo.config.h2

import java.lang.reflect.InvocationTargetException
import java.sql.SQLException
import javax.servlet.Servlet
import javax.servlet.ServletContext

/**
 * Utility class to configure H2 in development.
 *
 * We don't want to include H2 when we are packaging for the `"prod"` profile and won't
 * actually need it, so we have to load / invoke things at runtime through reflection.
 */
object H2ConfigurationHelper {

    @Throws(SQLException::class)
    fun createServer(): Any {
        return createServer("9092")
    }

    @Throws(SQLException::class)
    fun createServer(port: String): Any {
        return try {
            val loader = Thread.currentThread().contextClassLoader
            val serverClass = Class.forName("org.h2.tools.Server", true, loader)
            val createServer = serverClass.getMethod("createTcpServer", Array<String>::class.java)
            createServer.invoke(null, arrayOf("-tcp", "-tcpAllowOthers", "-tcpPort", port))
        } catch (e: Throwable) {
            val message = when (e) {
                is ClassNotFoundException,
                is LinkageError -> "Failed to load and initialize org.h2.tools.Server"
                is SecurityException,
                is NoSuchMethodException -> "Failed to get method org.h2.tools.Server.createTcpServer()"
                is IllegalAccessException,
                is IllegalArgumentException -> "Failed to invoke org.h2.tools.Server.createTcpServer()"
                is InvocationTargetException -> {
                    val t = e.targetException
                    if (t is SQLException) {
                        throw t
                    }
                    "Unchecked exception in org.h2.tools.Server.createTcpServer()"
                }
                else -> "Unchecked exception in org.h2.tools.Server.createTcpServer()"
            }
            throw IllegalStateException(message, e)
        }
    }

    fun initH2Console(servletContext: ServletContext) {
        try {
            // We don't want to include H2 when we are packaging for the "prod" profile and won't
            // actually need it, so we have to load / invoke things at runtime through reflection.
            val loader = Thread.currentThread().contextClassLoader
            val servletClass = Class.forName("org.h2.server.web.WebServlet", true, loader)
            val servlet = servletClass.getDeclaredConstructor().newInstance() as Servlet
            val h2ConsoleServlet = servletContext.addServlet("H2Console", servlet)
            h2ConsoleServlet.addMapping("/h2-console/*")
            h2ConsoleServlet.setInitParameter("-properties", "src/main/resources/")
            h2ConsoleServlet.setLoadOnStartup(1)
        } catch (e: Throwable) {
            val message = when (e) {
                is ClassNotFoundException,
                is LinkageError,
                is NoSuchMethodException,
                is InvocationTargetException -> "Failed to load and initialize org.h2.server.web.WebServlet"
                else -> "Failed to instantiate org.h2.server.web.WebServlet"
            }
            throw IllegalStateException(message, e)
        }
    }
}
