package gargoyle.xenox.util.log

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.io.Writer
import java.nio.charset.Charset
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

abstract class StreamLogger(private val writer: PrintWriter) :
    ILogger {

    constructor(writer: Writer) : this((PrintWriter(writer)))

    constructor(stream: OutputStream = System.err, charset: Charset = Charset.defaultCharset()) :
            this(OutputStreamWriter(stream, charset))

    constructor(charset: Charset = Charset.defaultCharset()) : this(System.err, charset)

    override fun log(
        caller: StackTraceElement,
        throwable: Throwable?,
        level: ILogger.Level,
        dateTime: Long,
        message: String
    ) {
        writer.println(
            "[${
                DateTimeFormatter.BASIC_ISO_DATE.format(
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(dateTime),
                        ZoneId.systemDefault()
                    )
                )
            }] [$level] ${caller.className}.${caller.methodName}(${caller.fileName}:${caller.lineNumber}${"): " + message + (throwable?.stackTraceToString() ?: "")}"
        )
        writer.flush()
    }

    override fun isLoggable(level: ILogger.Level): Boolean =
        level.isLoggable(ILogger.Level.DEBUG)

}
