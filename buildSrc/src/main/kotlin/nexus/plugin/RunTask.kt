package nexus.plugin

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import java.io.File

open class RunTask : JavaExec() {

    @Input
    var workspace: String = "${project.rootDir.absolutePath}${File.separator}out"

    init {
        args = arrayListOf(workspace)
    }

    @TaskAction
    override fun exec() {
        val directory = File(workspace)
        if (directory.exists() && directory.isDirectory) {
            println(directory.path)
        }
        super.exec()

    }
}