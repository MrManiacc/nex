package nexus.plugin

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * This class is used for correctly outputting the given project
 */
open class BuildTask : DefaultTask() {
    @Input
    var workspace: String = File(project.rootDir, "out").absolutePath
    private val libsOutputDir: File = File("$workspace/libs")

    @Input
    var projects: Set<Project> = project.subprojects

    init {
        projects.map { "${it.name}:classes" }.forEach {
            dependsOn(it)
        }
    }


    @TaskAction
    fun perform() {
        File(workspace).deleteRecursively()
        projects.forEach {
            println("resolving project ${it.name}")
            moveClassesAndResource(it)
            moveDependencies(it)
        }
    }

    /**
     * Moves our classes and resources
     */
    private fun moveClassesAndResource(project: Project) {
        val dir = getOutputDirFor(project)
        File(project.buildDir, "classes/kotlin/main")
            .listFiles { _, name -> name != "META-INF" }?.forEach { file ->
                FileUtils.copyToDirectory(file, dir)
            }
        File(project.buildDir, "resources/main")
            .listFiles()?.forEach { file ->
                FileUtils.copyToDirectory(file, dir)
            }
    }

    private fun getOutputDirFor(project: Project): File = File("$workspace/${project.name}")

    /**
     * This moves over the dependencies of the project
     */
    private fun moveDependencies(project: Project) {
        project.configurations.named("runtimeClasspath") {
            this.filter { it.exists() }.files.forEach {
                FileUtils.copyFileToDirectory(it, libsOutputDir)
            }
        }
    }


}