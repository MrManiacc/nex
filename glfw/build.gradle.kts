plugins {
    java
    kotlin("jvm")
}

group = "me.jraynor"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":"))
    implementation(project(":core"))
}