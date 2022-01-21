import nexus.plugin.BuildTask
import nexus.plugin.RunTask
import org.gradle.internal.os.OperatingSystem.*

plugins {
    java
    kotlin("jvm") version "1.6.10"
    id("nexus.plugin.NexusPlugin")
}

group = "me.jraynor"
version = "1.0-SNAPSHOT"


tasks.named("run", RunTask::class.java).configure {
    dependsOn("prepare")
    main = "engine.core.Launcher"
    classpath = sourceSets["main"].runtimeClasspath
    workspace = "${projectDir.path}/out"
    if (current().isMacOsX)
        jvmArgs = listOf("-XstartOnFirstThread")
}

tasks.named("prepare", BuildTask::class.java).configure {
    projects = subprojects
}

dependencies {
    implementation("org.dom4j:dom4j:2.1.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
}

allprojects {
    plugins.withId("java") {
        repositories {
            mavenCentral()
        }

        dependencies {
            implementation(kotlin("stdlib"))
            implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
            implementation("org.apache.logging.log4j:log4j:2.17.0")
            implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.0")
            implementation("net.engio:mbassador:1.3.2")
        }
    }
}
