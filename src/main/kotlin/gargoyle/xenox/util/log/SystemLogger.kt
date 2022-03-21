package gargoyle.xenox.util.log

internal object SystemLogger : StreamLogger() {
    val systemLogger: Logger = SystemLogger
}
