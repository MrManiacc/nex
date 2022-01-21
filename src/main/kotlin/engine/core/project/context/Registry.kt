@file:Suppress("UNCHECKED_CAST")

package engine.core.project

import mu.KotlinLogging
import kotlin.reflect.KClass

/**
 * A register contains/stores arbitrary object instances allowing for looks and injections
 */
class Registry {
    private val log = KotlinLogging.logger { }
    private val instances: MutableMap<KClass<*>, Any> = HashMap()
    private val namedInstances: MutableMap<KClass<*>, MutableMap<String, Any>> = HashMap()

    /**
     * Adds the new instance if not already present, return true if put successfully
     */
    fun add(instance: Any): Boolean {
        if (instances.containsKey(instance::class)) {
            log.warn { "Attempted to insert instance of class '${instance::class.simpleName} twice." }
            return false
        }
        instances[instance::class] = instance
        return true
    }

    /**
     * Adds a new named instance if not already present
     */
    fun add(instance: Any, name: String): Boolean {
        val instances = namedInstances.computeIfAbsent(instance::class) { HashMap() }
        if (instances.containsKey(name)) {
            log.warn { "Attempted to instance named '$name' of class type '${instance::class.simpleName}' twice." }
            return false
        }
        instances[name] = instance
        return true
    }

    /**
     * This gets the given instance with the provided [type] and casts it to the type
     */
    @Suppress("UNCHECKED_CAST") fun <T : Any> get(type: KClass<T>): T? {
        if (!instances.containsKey(type)) return null
        return instances[type]!! as T
    }

    /**
     * This gets the given named instance with the provided [type] and casts it to the type
     */
    @Suppress("UNCHECKED_CAST") fun <T : Any> get(type: KClass<T>, name: String): T? {
        val map = namedInstances[type] ?: return null
        if (!map.containsKey(name)) return null
        return map[name]!! as T
    }

    /**
     * Gets all the named instances for the given type as well as the singleton instances
     */
    fun <T : Any> collect(type: KClass<T>): List<T> {
        val list = ArrayList<T>()
        get(type)?.let { list.add(it) }
        if (namedInstances.containsKey(type)) {
            for (instance in namedInstances[type]!!.values)
                list.add(instance as T)
        }
        return list
    }

    /**
     * Gets all the named instances for the given type as well as the singleton instances
     */
    inline fun <reified T : Any> collect(): List<T> = collect(T::class)

    /**
     * This gets the given named instance with the provided [T] and casts it to the type
     */
    inline fun <reified T : Any> get(name: String): T? = get(T::class, name)


    /**
     * This gets the given instance with the provided [T] and casts it to the type
     */
    inline fun <reified T : Any> get(): T? = get(T::class)

    /**
     * true if we have the given instance
     */
    fun <T : Any> has(type: KClass<T>): Boolean = get(type) != null

    /**
     * true if we have the given instance
     */
    inline fun <reified T : Any> has(): Boolean = has(T::class)

    /**
     * true if we have the given named instance
     */
    fun <T : Any> has(type: KClass<T>, name: String): Boolean = get(type, name) != null

    /**
     * true if we have the given named instance
     */
    inline fun <reified T : Any> has(name: String): Boolean = has(T::class, name)

    /**
     * returns true if the item was removed successfully
     */
    @Suppress("UNCHECKED_CAST") fun <T : Any> remove(type: KClass<T>): T? {
        val removed = instances.remove(type) ?: return null
        return removed as T
    }

    /**
     * returns true if the item was removed successfully
     */
    inline fun <reified T : Any> remove(): T? = remove(T::class)

    /**
     * returns true if the item was removed successfully
     */
    @Suppress("UNCHECKED_CAST") fun <T : Any> remove(type: KClass<T>, name: String): T? {
        if (!has(type, name)) return null
        return namedInstances[type]!!.remove(name)!! as T
    }

    /**
     * returns true if the item was removed successfully
     */
    inline fun <reified T : Any> remove(name: String): T? = remove(T::class, name)
}