pluginManagement {
    //    Eval.xy(this, it, file('./gradle/repositories.settings.gradle').text)
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
}

val enableMetalPlayground: String by settings

rootProject.name = "${rootDir.name}-root"

fun isPropertyTrue(name: String): Boolean {
    return System.getenv(name) == "true" || System.getProperty(name) == "true"
}

val inCI = isPropertyTrue("CI")
val disabledExtraKorgeLibs = isPropertyTrue("DISABLED_EXTRA_KORGE_LIBS")
fun includeProject(path: String) {
    include(path)
    project(":$path").name = path.substringAfterLast("/")
}

includeProject("libraries/korlibs-audio")
includeProject("libraries/korlibs-audio-core")
includeProject("libraries/korlibs-bignumber")
includeProject("libraries/korlibs-compression")
includeProject("libraries/korlibs-concurrent")
includeProject("libraries/korlibs-crypto")
includeProject("libraries/korlibs-datastructure")
includeProject("libraries/korlibs-datastructure-core")
includeProject("libraries/korlibs-dyn")
includeProject("libraries/korlibs-encoding")
includeProject("libraries/korlibs-ffi")
includeProject("libraries/korlibs-image")
includeProject("libraries/korlibs-image-core")
includeProject("libraries/korlibs-inject")
includeProject("libraries/korlibs-io")
includeProject("libraries/korlibs-io-fs")
includeProject("libraries/korlibs-io-network-core")
includeProject("libraries/korlibs-io-nodejs")
includeProject("libraries/korlibs-io-stream")
includeProject("libraries/korlibs-io-network-stream")
includeProject("libraries/korlibs-io-vfs")
includeProject("libraries/korlibs-jseval")
includeProject("libraries/korlibs-logger")
includeProject("libraries/korlibs-math")
includeProject("libraries/korlibs-math-core")
includeProject("libraries/korlibs-math-vector")
includeProject("libraries/korlibs-memory")
includeProject("libraries/korlibs-number")
includeProject("libraries/korlibs-platform")
includeProject("libraries/korlibs-serialization")
includeProject("libraries/korlibs-string")
includeProject("libraries/korlibs-template")
includeProject("libraries/korlibs-time")
includeProject("libraries/korlibs-time-core")
includeProject("libraries/korlibs-wasm")
include(":korge")
include(":korge-core")
include(":korge-ipc")
include(":korge-kotlin-plugin")
include(":korge-sandbox")
