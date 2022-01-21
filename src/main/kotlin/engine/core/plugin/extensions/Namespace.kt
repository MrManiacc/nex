package engine.core.plugin.extensions

import engine.core.plugin.PluginClassloader
import engine.core.plugin.PluginStore
import engine.core.project.Project
import engine.core.project.context.Inject
import engine.core.project.context.Reflection
import engine.core.project.context.Registry
import mu.KotlinLogging
import net.engio.mbassy.bus.MBassador
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.superclasses

/**
 * A namespace is a group of ExtensionPoints
 */
class Namespace {
    private val extensions: MutableMap<String, MutableList<Extension>> = HashMap()
    private val log = KotlinLogging.logger { }
    private val namedInterfaces = HashMap<String, KClass<*>>()
    private val interfaceNames = HashMap<KClass<*>, String>()
    private val namedImplementations = HashMap<String, MutableList<Pair<KClass<*>, Extension>>>()
    private val namedInstances = HashMap<String, MutableList<Any>>()
    private val extensionInstances = HashMap<Any, Extension>()

    /**
     * This will map all of the extensions correctly
     */
    fun resolve(store: PluginStore) {
        store.getModules().forEach {
            it.manifest.extensions.extensions.forEach { ext ->
                val extensions = extensions.computeIfAbsent(ext.extensionId) { ArrayList() }
                extensions.add(ext)
            }
        }
    }

    /**
     * This loads the extensions
     */
    fun load(loader: PluginClassloader) {
        extensions.forEach { (name, extensions) ->
            extensions.forEach {
                if (it.type == ExtensionType.Interface && !namedInterfaces.containsKey(name)) {
                    val cls = loader.loadKClass(it.attributes["interface"]!!)
                    namedInterfaces[name] = cls
                    log.info { "Loaded class interface named $name with interface ${cls.qualifiedName}" }
                    interfaceNames[cls] = name
                } else if (it.type == ExtensionType.Implementation && !namedImplementations.containsKey(name)) {
                    val list = namedImplementations.computeIfAbsent(name) { ArrayList() }
                    list.add(loader.loadKClass(it.attributes["implementation"]!!) to it)
                    log.info { "Loaded class implementation named $name with class ${namedInterfaces[name]}" }
                }
            }
        }
    }


    /**
     * This instantiates the instances and returns a map with the interface as the key for
     * each instance
     */
    fun initializeInstances(map: MutableMap<KClass<*>, MutableList<Any>>, registry: Registry) {
        namedImplementations.forEach cont@{ name, impls ->
            val interfaceCls = namedInterfaces[name] ?: return@cont
            val list = map.computeIfAbsent(interfaceCls) { ArrayList() }
            val instances = namedInstances.computeIfAbsent(name) { ArrayList() }
            impls.forEach {
                val instance = createInstance(it.first, it.second, registry)
                list.add(instance)
                instances.add(instance)
            }
            extensionInstances.forEach { (instance, extension) ->
                if (extension.boolean("inject")) Reflection.inject(instance, registry)
                if (extension.boolean("subscribe")) Reflection.subscribe(instance, registry)
            }
        }
    }

    /**
     * Gets the list of instances by the given name
     */
    fun getInstances(name: String): List<Any> = namedInstances[name] ?: emptyList()


    /**
     * Internally creates and initializes the instance.
     * TODO: try to map values to the constructors
     */
    private fun createInstance(clazz: KClass<*>, extension: Extension, registry: Registry): Any {
        val instance = clazz.createInstance()
        extensionInstances[instance] = extension
        if (extension.boolean("share")) share(instance, registry, extension.string("shareAs"))
        clazz.declaredMembers.forEach {
            if (it is KMutableProperty && extension.has(it.name)) {
                extension.ifPresent(it.returnType.classifier as KClass<*>, it.name) { attrib ->
                    it.setter.call(instance, attrib)
                }
            }
        }
        return instance
    }

    private fun share(instance: Any, registry: Registry, shareAs: String? = null) {
        val cls = instance::class
        if (shareAs != null) {
            for (it in cls.superclasses) {
                if (it.qualifiedName == shareAs || it.simpleName == shareAs) {
                    registry[it] = instance
                    log.info { "Sharing instance of type '${cls.qualifiedName}' as supertype '${it.qualifiedName}'" }
                }
            }
        } else {
            registry.add(instance)
            log.info { "Sharing instance of type '${cls.qualifiedName}'" }
        }
    }

    internal fun getClasses(name: String): List<KClass<*>> = namedImplementations[name]?.map { it.first } ?: emptyList()

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> getClasses(interfaceClass: KClass<T>): List<KClass<T>> {
        val name = interfaceNames[interfaceClass] ?: return emptyList()
        return getClasses(name) as List<KClass<T>>
    }


}