package engine.core

import engine.core.ext.Initialize
import engine.core.ext.Updatable
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
        val window = project.extensionFor<Window>() ?: return
        val updaters = project.extensionsFor<Updatable>()
        var last = System.currentTimeMillis()
        window.init()
        while (window.isOpen()) {
            //TODO: update window
            window.clearBuffer()
            val now = System.currentTimeMillis()
            val delta = now - last
            for (updatable in updaters)
                updatable.begin()
            for (updatable in updaters)
                updatable.update((delta / 1000f))
            for (updatable in updaters)
                updatable.end()
            //TODO render here
            window.renderBuffer()
            last = now
        }
    }
}