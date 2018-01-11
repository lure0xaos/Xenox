package gargoyle.xenox.util.i18n

import gargoyle.xenox.util.log.Log
import java.util.*

operator fun ResourceBundle.get(key: String): String =
    if (containsKey(key)) getString(key)
    else key.also { Log.error("resource $key not found") }
