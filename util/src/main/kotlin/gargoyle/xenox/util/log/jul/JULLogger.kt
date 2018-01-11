package gargoyle.xenox.util.log.jul

import gargoyle.xenox.util.log.ILogger
import java.time.Instant
import java.util.logging.Level
import java.util.logging.LogRecord

class JULLogger(val name: String, val level: ILogger.Level) : ILogger {
    private val logger: java.util.logging.Logger = java.util.logging.Logger.getLogger(name)


    override fun isLoggable(level: ILogger.Level): Boolean =
        level.isLoggable(this.level)

    override fun log(
        caller: StackTraceElement,
        throwable: Throwable?,
        level: ILogger.Level,
        dateTime: Long,
        message: String
    ) {
        logger.log(LogRecord(
            when (level) {
                ILogger.Level.FATAL -> Level.SEVERE
                ILogger.Level.ERROR -> Level.SEVERE
                ILogger.Level.WARN -> Level.WARNING
                ILogger.Level.INFO -> Level.INFO
                ILogger.Level.DEBUG -> Level.FINE
            }, message
        ).apply {
            loggerName = caller.className
            instant = Instant.ofEpochMilli(dateTime)
            sourceClassName = caller.className
            sourceMethodName = caller.methodName
            thrown = throwable
        })
    }
}
