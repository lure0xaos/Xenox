package gargoyle.xenox.util.log

import gargoyle.xenox.util.log.jul.JULLoggerFactory
import gargoyle.xenox.util.log.jul.JULLoggerFactory.getCallerName

@Suppress("unused")
object Log {
    fun fatal(message: String): Unit = log(ILogger.Level.FATAL, message)

    fun fatal(throwable: Throwable, message: String? = null): Unit = log(throwable, ILogger.Level.FATAL, message)

    fun error(message: String): Unit = log(ILogger.Level.ERROR, message)

    fun error(throwable: Throwable, message: String? = null): Unit = log(throwable, ILogger.Level.ERROR, message)

    fun info(message: String): Unit = log(ILogger.Level.INFO, message)

    fun info(throwable: Throwable, message: String? = null): Unit = log(throwable, ILogger.Level.INFO, message)

    fun warn(message: String): Unit = log(ILogger.Level.WARN, message)

    fun warn(throwable: Throwable, message: String? = null): Unit = log(throwable, ILogger.Level.WARN, message)

    fun debug(message: String): Unit = log(ILogger.Level.DEBUG, message)

    fun debug(throwable: Throwable, message: String? = null): Unit = log(throwable, ILogger.Level.DEBUG, message)

    fun log(throwable: Throwable, level: ILogger.Level = ILogger.Level.DEBUG, message: String? = null): Unit =
        JULLoggerFactory.getLogger(getCallerName(), level).log(throwable, level, message)

    fun log(level: ILogger.Level = ILogger.Level.DEBUG, message: String): Unit =
        JULLoggerFactory.getLogger(getCallerName(), level).log(level, message)

}
