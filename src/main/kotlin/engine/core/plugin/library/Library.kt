package engine.core.plugin.library

import engine.core.plugin.Plugin
import mu.KotlinLogging
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.util.jar.JarEntry
import java.util.jar.JarInputStream

/**
 * This is used for libraries loaded up from a jar file, for example the lwjgl jar file library will internally
 * be represented as an instance of this library class
 */
class Library(private val library: File) : Plugin {
    private val log = KotlinLogging.logger { }
    private var resolved: Boolean = false
    private val classes = HashMap<String, URL>()
    private val resources = HashMap<String, URL>()
    private var libraryName: String = library.nameWithoutExtension

    /**
     * This should actually resolve the given module/library, if it's a module it should locate
     * the classes as well as resolve the manifest file. If it's a library, it should simply
     * load up all the classes and resources (module does this as well as resolution of the manifest)
     */
    override fun resolve() {
        if (!resolved) {
            resolved = true
            val stream = JarInputStream(FileInputStream(library))
            var entry = stream.nextJarEntry
            while (entry != null) {
                processEntry(entry)
                entry = stream.nextJarEntry
            }
        } else log.warn { "Attempted to resolve a library named '$libraryName' again!" }
    }

    /**
     * Processes the given jar entry.
     */
    private fun processEntry(entry: JarEntry) {
        val name = entry.name
        val url = URL("jar:" + library.toURI().toURL() + "!/${entry.name}")
        if (name.endsWith(".class")) {
            val className = entry.name.replace("/", ".").replace(".class", "")
            classes[className] = url
        } else if (!entry.isDirectory) {
            resources[entry.name] = url
        }
    }



    /**
     * True if this instance has been resolved
     */
    override fun isResolved(): Boolean = resolved

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
     * Prints out the libraries jar file name here.
     */
    override fun toString(): String = library.nameWithoutExtension

    /**
     * The name of the given resolvable
     */
    override val name: String
        get() = if (resolved) libraryName else library.nameWithoutExtension
}