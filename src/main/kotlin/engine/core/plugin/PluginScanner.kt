package engine.core.plugin

import engine.core.plugin.library.Library
import engine.core.plugin.module.Module
import engine.core.plugin.module.ModuleType
import mu.KotlinLogging
import java.io.File

/**
 * This class is used for locating modules from a given directory.
 */
object PluginScanner {
    private val log = KotlinLogging.logger {}

    /**
     * This should locate all modules in the given folder
     */
    fun locate(scanDir: File): PluginStore {
        val store = PluginStore()
        locate(scanDir, store)
        return store
    }

    /**
     * This should locate all modules in the given folder
     */
    fun locate(scanDir: File, store: PluginStore) {
        log.info { "Scanning directory for modules: ${scanDir.path}" }
        locateLibraries(scanDir, store)
        locateJarModules(scanDir, store)
        locateZipModules(scanDir, store)
        locateDirectoryModules(scanDir, store)
    }

    /**
     * This is used to locate all libraries within the scan directory
     */
    private fun locateLibraries(scanDir: File, store: PluginStore) {
        val libraryPath = File(scanDir, "libs")
        libraryPath.listFiles()?.filter { it.isFile && it.name.endsWith(".jar") }?.forEach {
            store.add(Library(it))
        }
    }

    /**
     * This is used for locating all directory based modules
     */
    private fun locateDirectoryModules(scanDir: File, modules: PluginStore) {
        val processedPaths = HashSet<String>() //cache the processed scanDirs
        scanDir.walkBottomUp().forEach {
            if (it.isFile && it.name.endsWith(".class")) {
                var parent = it.parentFile
                while (parent != (scanDir)) {
                    if (parent.parentFile == scanDir)
                        break
                    parent = parent.parentFile
                }
                if (!processedPaths.contains(parent.path)) {
                    processedPaths.add(parent.path)
                    modules.add(Module(parent, ModuleType.Directory))
                }
            }
        }
    }

    /**
     * This is used for locating all jar based modules
     */
    private fun locateJarModules(scanDir: File, modules: PluginStore) {
        //Locates all modules in the root directory of the scanDir, this ensures that library files will not be processed
        scanDir.listFiles()?.filter { it.name.endsWith(".jar") }?.forEach {
            modules.add(Module(it, ModuleType.Jar))
        }
    }

    /**
     * This is used for locating all jar based modules
     */
    private fun locateZipModules(scanDir: File, modules: PluginStore) {
        //Locates all modules in the root directory of the scanDir, this ensures that library files will not be processed
        scanDir.listFiles()?.filter { it.name.endsWith(".zip") }?.forEach {
            modules.add(Module(it, ModuleType.Zip))
        }
    }


}