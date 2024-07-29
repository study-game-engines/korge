package korlibs.korge.gradle

object BuildVersions {
    const val GIT = "---"
    const val KOTLIN = "2.0.0"
    const val NODE_JS = "20.12.1"
    const val JNA = "5.13.0"
    const val COROUTINES = "1.9.0-RC"
    const val ANDROID_BUILD = "8.2.0"
    const val KOTLIN_SERIALIZATION = "1.7.0"
    const val KORGE = "999.0.0.999"

    val ALL_PROPERTIES by lazy { listOf(
        ::GIT, ::KOTLIN, ::NODE_JS, ::JNA, ::COROUTINES, 
        ::ANDROID_BUILD, ::KOTLIN_SERIALIZATION, ::KORGE
    ) }
    val ALL by lazy { ALL_PROPERTIES.associate { it.name to it.get() } }
}