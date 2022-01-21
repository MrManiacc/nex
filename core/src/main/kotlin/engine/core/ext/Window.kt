package engine.core.ext

import engine.core.project.Project

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
    fun clear()
    fun poll()
    fun swap()
    fun isOpen(): Boolean
    fun initWindow()
}