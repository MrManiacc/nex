@file:JvmName("Launcher")

package engine.core

import engine.core.project.Project
import java.io.File

/**
 * This is the main entry point for the engine. It should be able to load up all the extensions.
 */
fun main(args: Array<String>) {
    if (args.isEmpty()) error("Must pass in 1 parameter for project path...")
    val project = Project("project", File(args[0]))
    project.load()
}