package engine.core.util

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMembers

/**
 * This calls the given method with the given arguments
 * @return if called
 */
internal fun Any.call(methodName: String, value: Any): Boolean {
    val cls = this::class
    for (method in cls.declaredFunctions) {
        if (method.name == methodName) {
            method.parameters.forEach {
                println(it.type.classifier)
            }
            method.call( this, value)
            return true
        }
    }
    return false
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