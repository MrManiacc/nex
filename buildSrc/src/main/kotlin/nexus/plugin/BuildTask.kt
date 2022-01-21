package nexus.plugin

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * This class is used for correctly outputting the given project
 */
open class BuildTask : DefaultTask() {
    private val outputDir: File = File(project.rootDir, "out/${project.name}")
    private val libsOutputDir: File = File(project.rootDir, "out/libs")
    private val classesDir: File = File(project.buildDir, "classes/kotlin/main")
    private val resourcesDir: File = File(project.projectDir, "src/main/resources")

    init {
        dependsOn("classes")
    }



    @TaskAction
    fun perform() {
        moveClassesAndResource()
        moveDependencies(project)
    }

    /**
     * Moves our classes and resources
     */
    private fun moveClassesAndResource() {
        classesDir.listFiles { _, name -> name != "META-INF" }?.forEach { file ->
            FileUtils.copyToDirectory(file, outputDir)
        }
        resourcesDir.listFiles()?.forEach { file ->
            FileUtils.copyToDirectory(file, outputDir)
        }
    }

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