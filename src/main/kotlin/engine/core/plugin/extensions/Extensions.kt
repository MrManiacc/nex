package engine.core.plugin.extensions

import engine.core.plugin.extensions.manifest.Manifest

/**
 * This is used internally for storing extension templates, that will be used to register into
 * the global extension factory
 */
data class Extensions(val namespace: String = Manifest.DEFAULT_NAMESPACE, val extensions: List<Extension>)
