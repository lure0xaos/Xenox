package gargoyle.xenox.res

import java.io.InputStream
import java.net.URL
import kotlin.reflect.KClass

object Res {
    fun url(name: String): URL =
        Res::class.java.getResource(name) ?: error("NOT FOUND $name (should be at ${name(Res::class, name)})")

    fun stream(name: String): InputStream =
        Res::class.java.getResourceAsStream(name) ?: error("NOT FOUND $name (should be at ${name(Res::class, name)})")

    fun name(clazz: KClass<*>, name: String): String =
        clazz.qualifiedName!!.substringBeforeLast(".").replace('.', '/') + '/' + name
}
