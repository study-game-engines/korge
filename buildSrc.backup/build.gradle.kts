import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

var File.text
    get() = readText()
    set(value) {
        this.also { it.parentFile.mkdirs() }.writeText(value)
    }

plugins {
    id("publishing")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.kotlin.jvm") version libs.versions.kotlin
}

check(System.getProperty("java.version").startsWith("1.8") || System.getProperty("java.version").startsWith("9"))

dependencies {
    implementation(libs.kover)
    implementation(libs.dokka)
    implementation(libs.proguard.gradle)
    implementation(libs.gson)
    implementation(libs.gradle.publish.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.build.gradle)
    testImplementation(libs.junit)
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
    gradlePluginPortal()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap") }
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/temporary") }
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev") }
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven") }
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

tasks.withType(KotlinCompile::class).all {
    kotlinOptions.suppressWarnings = true
}

tasks.withType(KotlinCompile::class).configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
