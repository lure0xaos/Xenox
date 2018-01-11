package gargoyle.xenox.util.log

import gargoyle.xenox.util.log.jul.JULLoggerFactory.getCaller

interface ILogger {

    fun fatal(message: String): Unit =
        log(Level.FATAL, message)

    fun fatal(throwable: Throwable, message: String? = null): Unit =
        log(throwable, Level.FATAL, message)

    fun error(message: String): Unit =
        log(Level.ERROR, message)

    fun error(throwable: Throwable, message: String? = null): Unit =
        log(throwable, Level.ERROR, message)

    fun info(message: String): Unit =
        log(Level.INFO, message)

    fun info(throwable: Throwable, message: String? = null): Unit =
        log(throwable, Level.INFO, message)

    fun warn(message: String): Unit =
        log(Level.WARN, message)

    fun warn(throwable: Throwable, message: String? = null): Unit =
        log(throwable, Level.WARN, message)

    fun debug(message: String): Unit =
        log(Level.DEBUG, message)

    fun debug(throwable: Throwable, message: String? = null): Unit =
        log(throwable, Level.DEBUG, message)

    fun log(level: Level = Level.DEBUG, message: String): Unit =
        log(getCaller(), null, level, System.currentTimeMillis(), message)

    fun log(throwable: Throwable?, level: Level = Level.DEBUG, message: String? = null): Unit =
        log(
            getCaller(), throwable, level, System.currentTimeMillis(), message ?: if (throwable == null) ""
            else "${throwable::class.qualifiedName}: ${throwable.localizedMessage ?: ""}\n${throwable.stackTraceToString()}"
        )

    fun log(
        caller: StackTraceElement,
        throwable: Throwable?,
        level: Level,
        dateTime: Long,
        message: String
    )

    fun isLoggable(level: Level): Boolean

    enum class Level(private val level: Int) {
        FATAL(0), ERROR(1), INFO(2), WARN(3), DEBUG(4);

        fun isLoggable(level: Level): Boolean =
            this.level >= level.level

        override fun toString(): String =
            name
    }
}
