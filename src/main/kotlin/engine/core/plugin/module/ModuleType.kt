package engine.core.plugin.module
/**
 * Used for determine the type of module this is
 */
enum class ModuleType {
    Jar, //loads from jar file
    Zip, //loads from zip file
    Directory // Loads from a directory
}