package engine.core.plugin.extensions

import kotlin.reflect.KClass

/**
 * Represents either an interface which should only have 1 attribute with the name 'interface'
 * The [attributes] contain key to any type of value
 */
data class Extension(val extensionId: String, val type: ExtensionType, val attributes: Map<String, String>) {


    fun <T : Any> get(type: KClass<T>, name: String): T? {
        val result = when (type) {
            Boolean::class -> boolean(name)
            Int::class -> int(name)
            Long::class -> long(name)
            Double::class -> double(name)
            Float::class -> float(name)
            Short::class -> short(name)
            Byte::class -> byte(name)
            else -> string(name) //string otherwise
        } ?: return null
        if (type.isInstance(result)) return result as T
        return null
    }

    fun has(name: String): Boolean = attributes.containsKey(name)

    inline fun <reified T : Any> get(name: String): T? = get(T::class, name)

    fun <T : Any> ifPresent(type: KClass<T>, name: String, unit: (T) -> Unit) = get(type, name)?.let(unit)

    inline fun <reified T : Any> ifPresent(name: String, noinline unit: (T) -> Unit) = ifPresent(T::class, name, unit)

    fun string(name: String): String? = attributes[name]

    fun boolean(name: String): Boolean = string(name)?.toBoolean() ?: false

    fun int(name: String): Int? = string(name)?.toIntOrNull()

    fun long(name: String): Long? = string(name)?.toLongOrNull()

    fun double(name: String): Double? = string(name)?.toDoubleOrNull()

    fun float(name: String): Float? = string(name)?.toFloatOrNull()

    fun short(name: String): Short? = string(name)?.toShortOrNull()

    fun byte(name: String): Byte? = string(name)?.toByteOrNull()

}
