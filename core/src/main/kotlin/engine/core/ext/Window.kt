package engine.core.ext

/**
 * This is used for defining a window extension
 */
interface Window {
    var width: Int
    var height: Int
    var vsync: Boolean
    var fullscreen: Boolean
    var title: String
    var center: Boolean

    fun clearBuffer()
    fun renderBuffer()
    fun isOpen(): Boolean
    fun init()
    fun dispose()
}