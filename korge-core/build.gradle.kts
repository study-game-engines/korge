import korlibs.applyProjectProperties

description = "Multiplatform Game Engine written in Kotlin"

project.extensions.extraProperties.properties.apply {
    applyProjectProperties(
        "https://github.com/korlibs/korge",
        "MIT License",
        "https://raw.githubusercontent.com/korlibs/korge/master/LICENSE"
    )
}

dependencies {
    commonMainApi(project(":korlibs-audio"))
    commonMainApi(project(":korlibs-audio-core"))
    commonMainApi(project(":korlibs-bignumber"))
    commonMainApi(project(":korlibs-compression"))
    commonMainApi(project(":korlibs-concurrent"))
    commonMainApi(project(":korlibs-crypto"))
    commonMainApi(project(":korlibs-datastructure"))
    commonMainApi(project(":korlibs-datastructure-core"))
    commonMainApi(project(":korlibs-dyn"))
    commonMainApi(project(":korlibs-encoding"))
    commonMainApi(project(":korlibs-ffi"))
    commonMainApi(project(":korlibs-image"))
    commonMainApi(project(":korlibs-image-core"))
    commonMainApi(project(":korlibs-inject"))
    commonMainApi(project(":korlibs-io"))
    commonMainApi(project(":korlibs-io-fs"))
    commonMainApi(project(":korlibs-io-network-core"))
    commonMainApi(project(":korlibs-io-network-stream"))
    commonMainApi(project(":korlibs-io-vfs"))
    commonMainApi(project(":korlibs-jseval"))
    commonMainApi(project(":korlibs-logger"))
    commonMainApi(project(":korlibs-math"))
    commonMainApi(project(":korlibs-math-core"))
    commonMainApi(project(":korlibs-math-vector"))
    commonMainApi(project(":korlibs-memory"))
    commonMainApi(project(":korlibs-number"))
    commonMainApi(project(":korlibs-platform"))
    commonMainApi(project(":korlibs-serialization"))
    commonMainApi(project(":korlibs-string"))
    commonMainApi(project(":korlibs-template"))
    commonMainApi(project(":korlibs-time"))
    commonMainApi(project(":korlibs-time-core"))
    commonMainApi(project(":korlibs-wasm"))
    //commonMainApi(libs.korlibs.audio)
    //commonMainApi(libs.korlibs.image)
    //commonMainApi(libs.korlibs.inject)
    //commonMainApi(libs.korlibs.template)
    //commonMainApi(libs.korlibs.time)
    commonMainApi(libs.kotlinx.atomicfu)
    commonMainApi(libs.kotlinx.coroutines.core)
    //commonTestApi(project(":korge-test"))
    jvmMainApi("org.jetbrains.kotlin:kotlin-reflect")
    jvmMainImplementation(libs.jackson.databind)
    jvmMainImplementation(libs.jackson.module.kotlin)

    //commonTestApi(testFixtures(project(":korma")))

    //add("jvmMainApi", project(":korte"))

    //add("commonTestApi", "it.krzeminski.vis-assert:vis-assert:0.4.0-beta")
}
