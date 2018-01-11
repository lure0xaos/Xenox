package gargoyle.xenox.util.log

interface ILoggerFactory {

    fun getLogger(name: String = getCallerName(), level: ILogger.Level = ILogger.Level.DEBUG): ILogger


    fun getCaller(): StackTraceElement =
        with(Log::class.java.getPackage().name) {
            Thread.currentThread().stackTrace.let { elements ->
                elements.drop(1).firstOrNull { !it.className.startsWith(this) } ?: elements.first()
            }
        }

    fun getCallerName(): String = getCaller().className
}
