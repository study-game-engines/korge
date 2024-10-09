package korlibs.io

import korlibs.io.file.*

suspend fun VfsFile.readXml(): Xml = Xml(this.readString())
