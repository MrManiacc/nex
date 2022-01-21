package engine.core.plugin

import engine.core.plugin.library.Library
import engine.core.plugin.module.Module
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicInteger

/**
 * This is used to internally store all of the resolved libraries and modules.
 */
class PluginStore {
    private val log = KotlinLogging.logger { }
    private val modules: MutableSet<Module> = HashSet()
    private val libraries: MutableSet<Library> = HashSet()

    /**
     * Appends a newly resolved resolvable to the correct sets
     */
    fun add(plugin: Plugin) {
        if (plugin is Module) {
            modules.add(plugin)
            log.debug { "Added resolvable module named '${plugin.name}'" }
        } else if (plugin is Library) {
            libraries.add(plugin)
            log.debug { "Added resolvable library named '${plugin.name}'" }
        }
    }

    /**
     * Resolves the modules
     */
    fun resolveModules() {
        modules.forEach {
            val start = System.currentTimeMillis()
            it.resolve()
            val end = System.currentTimeMillis()
            log.info { "Finished resolution of module ${it.name} in ${(end - start)} ms." }
        }
    }

    /**
     * Resolves the libraries
     */
    fun resolveLibraries() {
        libraries.forEach {
            val start = System.currentTimeMillis()
            it.resolve()
            val end = System.currentTimeMillis()
            log.info { "Finished resolution of library ${it.name} in ${(end - start)} ms." }
        }
    }


    /**
     * Exposes our libraries
     */
    fun getLibraries(): Set<Library> = libraries

    /**
     * Exposes our modules
     */
    fun getModules(): Set<Module> = modules
}