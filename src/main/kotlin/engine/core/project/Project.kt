package engine.core.project

import engine.core.plugin.PluginClassloader
import engine.core.plugin.PluginScanner
import engine.core.plugin.PluginStore
import engine.core.plugin.extensions.Namespace
import engine.core.util.call
import mu.KotlinLogging
import java.io.File
import kotlin.reflect.KClass


/**
 * A project contains a list of modules and libraries. It internally manages all extensions
 * from modules. It also sandboxes plugins from within a plugin-classloader.
 */
class Project(val name: String, val directory: File) {
    private val log = KotlinLogging.logger { }
    private val store: PluginStore = PluginStore()
    private val loader = PluginClassloader()
    private val namespace = Namespace()
    private val instances = HashMap<KClass<*>, MutableList<Any>>()

    /**
     * This loads the project
     */
    internal fun load() {
        log.info { "Starting scanning for project '$name" }
        PluginScanner.locate(directory, store)
        startResolution()
        log.info { "Starting definitions of modules and libraries" }
        loader.define(store)
        namespace.resolve(store)
        namespace.load(loader)
        namespace.initializeInstances(instances, this)
        initialize()
    }

    /**
     * This gets all extensions with the 'initialize' name and calls it's initialize method
     */
    private fun initialize() {
        namespace.getInstances("initialize").forEach {
            it.call("onInitialization", this)
        }
    }

    /**
     * This starts resolving all the libraries and module classes asynchronously
     */
    private fun startResolution() {
        log.info { "Starting to resolve libraries" }
        store.resolveLibraries()
        log.info { "Starting to resolve modules" }
        store.resolveModules()
        log.info { "Starting resolution of modules and libraries" }
        val start = System.currentTimeMillis()
        val diff = System.currentTimeMillis() - start
        log.info { "Finished resolving all modules in $diff ms" }
    }

    /**
     * Gets all extensions for the given interface
     */
    @Suppress("UNCHECKED_CAST") fun <T : Any> extensionsFor(extensionInterface: KClass<T>): List<T> {
        if (!instances.containsKey(extensionInterface)) return emptyList()
        return instances[extensionInterface]!! as List<T>
    }

    /**
     * Gets all extensions for the given interface
     */
    @Suppress("UNCHECKED_CAST") fun <T : Any> extensionFor(extensionInterface: KClass<T>): T? {
        val exts = extensionsFor(extensionInterface)
        if (exts.isEmpty()) return null
        return exts[0]
    }
}