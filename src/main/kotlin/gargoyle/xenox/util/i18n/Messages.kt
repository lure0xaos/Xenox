package gargoyle.xenox.util.i18n

import gargoyle.xenox.util.log.Log
import java.io.Serializable
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle

class Messages(name: String, locale: Locale) : Serializable {
    private lateinit var bundle: ResourceBundle

    init {
        try {
            bundle = ResourceBundle.getBundle(name, locale)
        } catch (e: MissingResourceException) {
            Log.error(String.format("resource %s not found", name))
        }
    }

    operator fun get(key: String): String? {
        if (!::bundle.isInitialized) {
            Log.error(String.format("resource %s.%s not found", "?", key))
            return key
        }
        return if (bundle.containsKey(key)) bundle.getString(key) else {
               Log.error(String.format("resource %s not found", key))
               key
           }

    }
}
