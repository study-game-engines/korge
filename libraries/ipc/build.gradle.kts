plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("maven-publish")
}

group = "com.soywiz.korge"

java {
    setSourceCompatibility("17")
    setTargetCompatibility("17")
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "17"
        apiVersion = "1.8"
        languageVersion = "1.8"
        suppressWarnings = true
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
}
