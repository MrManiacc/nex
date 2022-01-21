package engine.core.plugin.extensions

/**
 * Represents either an interface which should only have 1 attribute with the name 'interface'
 * The [attributes] contain key to any type of value
 */
data class Extension(val extensionId: String, val type: ExtensionType, val attributes: Map<String, String>)
