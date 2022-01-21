plugins {
    java
    kotlin("jvm") version "1.6.10"
}

group = "me.jraynor"
version = "1.0-SNAPSHOT"



task("execute", JavaExec::class) {
    main = "engine.core.Launcher"
    group = "run"
    classpath = sourceSets["main"].runtimeClasspath
    dependsOn(project.subprojects.map { "${it.name}:PrepareNexus" })
}
dependencies {
    implementation("org.dom4j:dom4j:2.1.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
}

subprojects {
    plugins.withId("java") {
        apply(plugin = "nexus.plugin.NexusPlugin")
    }
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
