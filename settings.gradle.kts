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

includeProject("korlibs/korlibs-audio")
includeProject("korlibs/korlibs-audio-core")
includeProject("korlibs/korlibs-bignumber")
includeProject("korlibs/korlibs-compression")
includeProject("korlibs/korlibs-concurrent")
includeProject("korlibs/korlibs-crypto")
includeProject("korlibs/korlibs-datastructure")
includeProject("korlibs/korlibs-datastructure-core")
includeProject("korlibs/korlibs-dyn")
includeProject("korlibs/korlibs-encoding")
includeProject("korlibs/korlibs-ffi")
includeProject("korlibs/korlibs-image")
includeProject("korlibs/korlibs-image-core")
includeProject("korlibs/korlibs-inject")
includeProject("korlibs/korlibs-io")
includeProject("korlibs/korlibs-io-fs")
includeProject("korlibs/korlibs-io-network-core")
includeProject("korlibs/korlibs-io-nodejs")
includeProject("korlibs/korlibs-io-stream")
includeProject("korlibs/korlibs-io-network-stream")
includeProject("korlibs/korlibs-io-vfs")
includeProject("korlibs/korlibs-jseval")
includeProject("korlibs/korlibs-logger")
includeProject("korlibs/korlibs-math")
includeProject("korlibs/korlibs-math-core")
includeProject("korlibs/korlibs-math-vector")
includeProject("korlibs/korlibs-memory")
includeProject("korlibs/korlibs-number")
includeProject("korlibs/korlibs-platform")
includeProject("korlibs/korlibs-serialization")
includeProject("korlibs/korlibs-string")
includeProject("korlibs/korlibs-template")
includeProject("korlibs/korlibs-time")
includeProject("korlibs/korlibs-time-core")
includeProject("korlibs/korlibs-wasm")
include(":korge")
include(":korge-core")
include(":korge-ipc")
include(":korge-kotlin-plugin")
include(":korge-sandbox")
