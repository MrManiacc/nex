package engine.core.ext

interface Input {
    fun onKeyEvent(key: Int, scancode: Int, action: Int, mods: Int)
    fun onMouseMoveEvent(x: Double, y: Double)
    fun onMouseClickEvent(button: Int, action: Int, mods: Int)
    fun onMouseScrollEvent(xOffset: Double, yOffset: Double)
    fun onWindowClose(handle: Long)
}