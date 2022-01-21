package nexus.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class NexusPlugin : Plugin<Project> {
    /**
     * Apply this plugin to the given target object.
     *
     * @param target The target object
     */
    override fun apply(target: Project) {
        target.tasks.create("PrepareNexus", BuildTask::class.java)
    }
}