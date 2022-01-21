package engine.core.ext

import engine.core.project.Project


/**
 * This is the only interface present inside the main engine.
 * It is used a delegate to the core module allowing it to preform operations after the project has been initialized
 */
interface Initialize {
    /**
     * Called upon project initialization
     */
    fun onInitialization(project: Project)

}