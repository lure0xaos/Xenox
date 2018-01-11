package gargoyle.xenox.i18n

import java.util.*
import kotlin.reflect.KClass

object I18N {
    fun getResourceBundle(name: String, locale: Locale): ResourceBundle =
        ResourceBundle.getBundle(name(I18N::class, name), locale, I18N::class.java.classLoader)

    fun name(clazz: KClass<*>, name: String): String =
        clazz.qualifiedName!!.substringBeforeLast(".").replace('.', '/') + '/' + name
}
