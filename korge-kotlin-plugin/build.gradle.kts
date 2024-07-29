plugins {
    id("kotlin")
    id("maven-publish")
    id("com.github.gmazzo.buildconfig") version "5.3.5"
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
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation(libs.bundles.kotlin.test)
}

buildConfig {
    packageName("korlibs.korge.kotlin.plugin")
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"com.soywiz.korge.korge-kotlin-plugin\"")
}

afterEvaluate {
    tasks.getByName("sourceJar").dependsOn("generateBuildConfig")
}
