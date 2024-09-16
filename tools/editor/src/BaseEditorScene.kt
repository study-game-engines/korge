package editor

import korlibs.io.file.VfsFile
import korlibs.korge.scene.Scene

open class BaseEditorScene : Scene() {
    private val originalFile by lazy { injector.getSync<EditorFile>().file }
    private var _file: VfsFile? = null
    var file: VfsFile
        get() = _file ?: originalFile
        set(value) {
            _file = value
        }
}
