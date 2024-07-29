dependencies {
    commonMainApi(project(":korge-core"))
    jvmMainApi(project(":korge-ipc"))
    add("jvmMainApi", "org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.9.0-RC")
}
