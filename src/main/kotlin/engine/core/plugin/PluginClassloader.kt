package engine.core.plugin

import mu.KotlinLogging
import java.net.URL
import kotlin.collections.HashMap
import kotlin.reflect.KClass

class PluginClassloader(private val pLoader: PluginClassloader? = null) : ClassLoader() {
    private val log = KotlinLogging.logger { }
    private val registeredClasses: MutableMap<String, URL> = HashMap()
    private val registeredResources: MutableMap<String, URL> = HashMap()

    /**
     * This method will register all the modules and libraries to internal hashmaps, allowing them
     * to be quickly looked up in the [findClass] method.
     */
    fun define(store: PluginStore) {
        store.getLibraries().forEach {
            it.getClasses().forEach { (name, cls) ->
                registeredClasses[name] = cls
            }
            it.getResources().forEach { (name, res) ->
                registeredResources[name] = res
            }
        }
        store.getModules().forEach {
            it.getClasses().forEach { (name, cls) ->
                registeredClasses[name] = cls
            }
            it.getResources().forEach { (name, res) ->
                registeredResources[name] = res
            }
        }
    }

    /**
     * Returns the kotlin class variant
     */
    fun loadKClass(name: String): KClass<*> {
        val cls = loadClass(name)
        return cls.kotlin
    }

    /**
     * Finds the class with the specified [binary name](#binary-name).
     * This method should be overridden by class loader implementations that
     * follow the delegation model for loading classes, and will be invoked by
     * the [loadClass][.loadClass] method after checking the
     * parent class loader for the requested class.
     *
     * @implSpec The default implementation throws `ClassNotFoundException`.
     *
     * @param   name
     * The [binary name](#binary-name) of the class
     *
     * @return  The resulting `Class` object
     *
     * @throws  ClassNotFoundException
     * If the class could not be found
     *
     * @since  1.2
     */
    override fun findClass(name: String): Class<*>? {
        if (pLoader != null) {
            var cls: Class<*>? = null
            try {
                cls = pLoader.findClass(name)
            } catch (ex: Exception) {
                log.warn { "Ignored parent loader execution: ${ex.message}" }
            }
            if (cls != null) {
                log.debug { "Loading class with name ${cls.name} from parent class loader of type ${pLoader::class.simpleName}" }
                return cls
            }
        }
        try {
            if (registeredClasses.containsKey(name)) {
                val data: ByteArray = registeredClasses[name]!!.readBytes() // Convert binary stream to byte array
                return defineClass(name, data, 0, data.size)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            log.error { "Caught exception: ${exception.message}" }
        }
        return super.findClass(name)
    }


    /**
     * This is used for locating resources, we should look within the jar or zip or directory
     */
    override fun findResource(name: String): URL? {
        if (!registeredResources.containsKey(name))
            return super.findResource(name)
        log.info { "Loading in resource $name" }
        return registeredResources[name]
    }


    override fun getResource(name: String): URL? {
        return findResource(name) ?: return super.getResource(name)
    }

}