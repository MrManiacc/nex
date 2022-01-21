package engine.core.project.context

/**
 * This is used to inject a given variable from the registry.
 * if the [name] is supplied then we will look up the named instance, otherwise
 * we will just look up the singleton instance
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Inject(val name: String = "")
