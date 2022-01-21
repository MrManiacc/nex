package engine.core.plugin.module

import engine.core.plugin.extensions.manifest.Manifest
import engine.core.plugin.extensions.manifest.ManifestReader
import engine.core.plugin.Plugin
import mu.KotlinLogging
import java.io.File


import java.io.FileInputStream
import java.net.URL

import java.util.jar.JarInputStream
import java.util.zip.ZipInputStream


/**
 * A module should simply contain a list of urls that can be loaded onto a custom class loader
 */
class Module(private val module: File, private val type: ModuleType) : Plugin {
    private val log = KotlinLogging.logger { }
    private val classes: MutableMap<String, URL> = HashMap()
    private val resources: MutableMap<String, URL> = HashMap()
    internal lateinit var manifest: Manifest
    private var resolved: Boolean = false

    /**
     * True if this instance has been resolved
     */
    override fun isResolved(): Boolean = resolved

    /**
     * This is used for resolving the classes and resources
     */
    override fun resolve() {
        if (!resolved) {
            resolved = true
            log.info { "Starting resolution of module: ${module.nameWithoutExtension}" }
            when (type) {
                ModuleType.Directory -> processDirectoryModule()
                ModuleType.Jar -> processJarModule()
                ModuleType.Zip -> processZipModule()
            }
        } else log.warn { "Attempted to resolve module again named '${manifest.name}" }

    }


    /**
     * This method should be able to locate all the classes for the given module.
     */
    private fun processDirectoryModule() {
        module.walkTopDown().forEach {
            if (it.name.endsWith(".class")) {
                val qualifiedName = it.toRelativeString(module)
                    .replace("\\", ".")
                    .substringBeforeLast(".")
                classes[qualifiedName] = it.toURI().toURL()
            } else if (it.name.equals(Manifest.MANIFEST_NAME)) {
                manifest = ManifestReader.read(it)
                log.debug { "Found and loaded manifest: $manifest at ${it.path}" }
            } else if (it.isFile) {
                resources[it.name] = it.toURI().toURL()
            }
        }
    }

    /**
     * This method should be able to locate all the classes for the given module within a jar.
     */
    private fun processJarModule() {
        val jarFile = JarInputStream(FileInputStream(module))
        var entry = jarFile.nextJarEntry
        while (entry != null) {
            val url = URL("jar:" + module.toURI().toURL() + "!/${entry.name}")
            if (entry.name.endsWith(".class")) {
                val qualifiedName = entry.name.replace("/", ".").replace(".class", "")
                classes[qualifiedName] = url
            } else if (entry.name.equals(Manifest.MANIFEST_NAME)) {
                manifest = ManifestReader.read(url)
                log.debug { "Found and loaded manifest: $manifest at $url" }
            } else if (!entry.isDirectory) {
                resources[entry.name] = url
            }
            entry = jarFile.nextJarEntry
        }
    }

    /**
     * This method should be able to locate all the classes for the given module within a zip.
     */
    private fun processZipModule() {
        val jarFile = ZipInputStream(FileInputStream(module))
        var entry = jarFile.nextEntry
        while (entry != null) {
            val url = URL("jar:" + module.toURI().toURL() + "!/${entry.name}")
            val name = entry.name
            if (name.endsWith(".class")) {
                val className = name.replace("/", ".").replace(".class", "")
                classes[className] = url
            } else if (name.equals(Manifest.MANIFEST_NAME)) {
                manifest = ManifestReader.read(url)
                log.debug { "Found and loaded manifest: $manifest at $url" }
            } else if (!entry.isDirectory) {
                resources[name] = url
            }
            entry = jarFile.nextEntry
        }
    }


    /**
     * This should return a set of urls with the qualified name as the first
     * part of the pair. This should be directly loadable into a classloader
     */
    override fun getClasses(): Map<String, URL> = classes

    /**
     * This should contain all the resources in the form an url, and fully qualified
     * name with extension, which in turn should be loadable to the classloader
     */
    override fun getResources(): Map<String, URL> = resources

    /**
     * The name of the given resolvable
     */
    override val name: String
        get() = if (resolved) manifest.name else module.nameWithoutExtension


}