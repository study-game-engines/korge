package com.esotericsoftware.spine.korge

/*
import korlibs.korge.view.*
import korlibs.korge.view.ktree.*
import korlibs.io.serialization.xml.*

@Deprecated("KTree is going to be removed in a future version")
fun Views.registerSpine() {
    ktreeSerializer.register(
        name = "spine",
        deserializer = { xml ->
            when (xml.nameLC) {
                "spineref" -> SpineViewRef()
                else -> null
            }
        },
        serializer = { view, properties ->
            when (view) {
                is SpineViewRef -> Xml("spineref", properties)
                else -> null
            }
        }
    )
}
*/
