package engine.core.project.context

import mu.KotlinLogging
import net.engio.mbassy.bus.MBassador

/**
 * This object is used to do various reflections/injections upon instances within the engine
 */
object Reflection {
    private val log = KotlinLogging.logger { }

    /**
     * Subscribes the given instance to the eventbus
     */
    fun subscribe(instance: Any, registry: Registry) {
        registry.ifPresent<MBassador<*>> {
            it.subscribe(instance)
            log.debug { "Subscribed instance with name '${instance::class.qualifiedName}'" }
        }
    }

    /**
     * Injects the given instance with corresponding registry instances
     */
    fun inject(instance: Any, registry: Registry) {
        instance::class.java.declaredFields.forEach {
            if (it.isAnnotationPresent(Inject::class.java)) {
                if (it.isAccessible || it.trySetAccessible()) {
                    val name = it.getAnnotation(Inject::class.java).name
                    if (name.isBlank())
                        registry.ifPresent(it.type.kotlin) { injection ->
                            it.set(instance, injection)
                            log.debug { "Injected singleton instance of type '${it.type.simpleName}' from registry into instance '${instance::class.simpleName}'" }
                        }
                    else
                        registry.ifPresent(it.type.kotlin, name) { injection ->
                            it.set(instance, injection)
                            log.debug { "Injected instance named '$name' of type '${it.type.simpleName}' from registry into instance '${instance::class.simpleName}'" }
                        }
                }
            }
        }
    }

}