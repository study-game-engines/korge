import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.StringReader
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
    sourceSets.getByName("main").java {
        srcDir("src/main/gen")
    }
}

val properties: Properties = Properties().apply { load(StringReader(file("../gradle.properties").text)) }
val buildsVersionFile: File = file("../buildSrc/src/main/gen/korlibs/korge/gradle/BuildVersions.kt")
if (!buildsVersionFile.exists()) {
    buildsVersionFile.parentFile.mkdirs()
    buildsVersionFile.text = """
    package korlibs.korge.gradle
    
    object BuildVersions {
        const val GIT = "---"
        const val KOTLIN = "${libs.versions.kotlin.get()}"
        const val NODE_JS = "${libs.versions.node.get()}"
        const val JNA = "${libs.versions.jna.get()}"
        const val COROUTINES = "${libs.versions.kotlinx.coroutines.get()}"
        const val ANDROID_BUILD = "${libs.versions.android.build.gradle.get()}"
        const val KOTLIN_SERIALIZATION = "${libs.versions.kotlinx.serialization.get()}"
        const val KORGE = "${properties.getProperty("version")}"
    
        val ALL_PROPERTIES by lazy { listOf(
            ::GIT, ::KOTLIN, ::NODE_JS, ::JNA, ::COROUTINES, 
            ::ANDROID_BUILD, ::KOTLIN_SERIALIZATION, ::KORGE
        ) }
        val ALL by lazy { ALL_PROPERTIES.associate { it.name to it.get() } }
    }
    """.trim()
}
