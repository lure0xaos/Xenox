package gargoyle.xenox.util.log.jul

import gargoyle.xenox.util.log.ILogger
import gargoyle.xenox.util.log.ILoggerFactory
import gargoyle.xenox.util.log.SystemLogger
import java.security.AccessController
import java.security.PrivilegedAction
import java.util.logging.LogManager
import kotlin.reflect.KClass

object JULLoggerFactory : ILoggerFactory {

    private const val CONFIG = "/logging.properties"

    fun configure(name: String, clazz: KClass<JULLoggerFactory> = JULLoggerFactory::class) {
        AccessController.doPrivileged(PrivilegedAction {
            clazz.java.classLoader.getResource(
                if (this.CONFIG.startsWith('/')) this.CONFIG.trimStart('/')
                else "${clazz.qualifiedName!!.substringBeforeLast('.').replace('.', '/')}/${name}"
            )?.runCatching { openStream().use { it.apply { LogManager.getLogManager().readConfiguration(it) } } }
                ?.onFailure { SystemLogger.error("logger configuration error: ${it.localizedMessage}") }
                ?.onSuccess { SystemLogger.info("logger configured") }
                ?: SystemLogger.error("no logger config ${this.CONFIG}")
        })
    }

    init {
        configure(CONFIG)
    }

    override fun getLogger(name: String, level: ILogger.Level): ILogger =
        JULLogger(name, level)

}
