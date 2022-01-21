plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    google()
}


dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("commons-io:commons-io:2.11.0")
}

gradlePlugin {
    plugins {
        create("nexusPlugin") {
            id = "nexus.plugin.NexusPlugin"
            implementationClass = "nexus.plugin.NexusPlugin"
        }
    }
}