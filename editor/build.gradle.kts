import org.gradle.internal.os.OperatingSystem.*

plugins {
    java
    kotlin("jvm")
}

group = "me.jraynor"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.0"
val lwjglNatives = when (current()) {
    WINDOWS -> "natives-windows"
    MAC_OS -> "natives-macos"
    else -> "natives-linux"
}

dependencies {
    implementation(project(":"))
    implementation(project(":core"))
    implementation("io.github.spair:imgui-java-lwjgl3:1.86.0")
    implementation("io.github.spair:imgui-java-binding:1.86.0")
    implementation("io.github.spair:imgui-java-${lwjglNatives}:1.86.0")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-glfw")
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)

}