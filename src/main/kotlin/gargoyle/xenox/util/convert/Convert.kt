package gargoyle.xenox.util.convert


object Convert {
    private val converters: MutableMap<Class<*>, (String)-> Any> = HashMap()

    init {
        converters[String::class.java] =  { it }
        converters[Int::class.java] = { it.toInt() }
        converters[Boolean::class.java] = { it.toBoolean() }
        converters[Double::class.java] = { it.toDouble() }
        converters[Long::class.java] = { it.toLong() }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> convert(p: String?, type: Class<T>, defValue: T): T? =
        when {
            p == null -> null
            type in converters ->
            converters[type]!!(p) as T
            else -> defValue
        }
}
