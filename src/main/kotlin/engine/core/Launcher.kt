@file:JvmName("Launcher")
package engine.core

import engine.core.project.Project
import java.io.File

/**
 * This is the main entry point for the engine. It should be able to load up all the extensions.
 */
fun main(args: Array<String>) {
    val project = Project("testing", File("C:\\Users\\vtboy\\Documents\\Dev\\nexus\\out"))
    project.load()
//    val instance = clazz.newInstance() as WindowExtension

}