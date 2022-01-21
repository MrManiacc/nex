package engine.core

import engine.core.ext.Initialize
import engine.core.ext.Window
import engine.core.project.Project

/**
 * This is the main entry point for the engine. It called
 */
class Bootstrap : Initialize {

    /**
     * Called upon project initialization
     */
    override fun onInitialization(project: Project) {
        val window = project.extensionFor(Window::class) ?: return
        window.initWindow()
        while (window.isOpen()) {
            //TODO: update window
            window.clear()

            //TODO render here
            window.poll()
            window.swap()
        }
    }
}