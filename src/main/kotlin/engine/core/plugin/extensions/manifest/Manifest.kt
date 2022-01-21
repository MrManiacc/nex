package engine.core.plugin.extensions.manifest

import engine.core.plugin.extensions.Extensions

/**
 * This is used to store relevant information pertaining a given extension.
 */
data class Manifest(
    val id: String,
    val name: String,
    val version: String,
    val extensions: Extensions,
) {

    /**
     * Stores our manifest name so that it can be used in multiple locations without the need of updating it
     */
    companion object {
        const val MANIFEST_NAME = "module.xml"
        const val DEFAULT_NAMESPACE = "engine.core"
    }

}