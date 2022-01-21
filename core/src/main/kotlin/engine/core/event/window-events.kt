package engine.core.ext.event

/**
 * Passed along to the editor for when the window is created
 */
data class WindowCreatedEvent(val handle: Long)
data class WindowDestroyedEvent(val handle: Long)
