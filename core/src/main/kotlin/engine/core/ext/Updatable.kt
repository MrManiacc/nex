package engine.core.ext

/**
 * This represents an updatable object that has a render method as well as an update method
 * for physics and other computations
 */
interface Updatable {
    fun begin() = Unit

    /**
     * Calls once per frame
     */
    fun update(dt: Float)

    fun end() = Unit

}