package engine.core.util

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMembers

/**
 * This calls the given method with the given arguments
 * @return if called
 */
internal fun Any.call(methodName: String, value: Any): Boolean {
    val cls = this::class.java
    return try {
        val method = cls.getDeclaredMethod(methodName, value::class.java)
        method.invoke(this, value)
        true
    } catch (ex: Exception) {
        ex.printStackTrace()
        print("failed to call method $methodName, msg - ${ex}")
        false
    }
}

/**
 * Sets field value of the given fieldName to the [value]
 * @return true if set
 */
internal fun Any.set(fieldName: String, value: Any): Boolean {
    val cls = this::class
    for (field in cls.declaredMembers) {
        if (field.name == fieldName && field is KMutableProperty) {
            field.setter.call(this, value)
            return true
        }
    }
    return false
}

/**
 * This will do ors of the given values.
 */
fun Int.orEquals(vararg ints: Int): Int {
    var out = this
    for (element in ints)
        out = out or element
    return out
}