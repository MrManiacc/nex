package engine.core.plugin

import java.net.URL

/**
 * This interface can represent a loadable entity to a class loader. This should either be a module, which
 * can be loaded and manipulated in more specified ways, or a library that simply encapsulates the classes
 * and resources of the given jar file.
 */
interface Plugin {
    /**
     * True if this instance has been resolved
     */
    fun isResolved(): Boolean

    /**
     * This should actually resolve the given module/library, if it's a module it should locate
     * the classes as well as resolve the manifest file. If it's a library, it should simply
     * load up all the classes and resources (module does this as well as resolution of the manifest)
     */
    fun resolve()

    /**
     * This should return a set of urls with the qualified name as the first
     * part of the pair. This should be directly loadable into a classloader
     */
    fun getClasses(): Map<String, URL>

    /**
     * This should contain all the resources in the form an url, and fully qualified
     * name with extension, which in turn should be loadable to the classloader
     */
    fun getResources(): Map<String, URL>

    /**
     * The name of the given resolvable
     */
    val name: String

}