package com.esotericsoftware.spine.effect

import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.utils.SpineVector2
import korlibs.image.color.RGBAf

// Modifies the skeleton or vertex positions, UVs, or colors during rendering
interface VertexEffect {
    fun begin(skeleton: Skeleton)
    fun transform(position: SpineVector2, uv: SpineVector2, color: RGBAf, darkColor: RGBAf)
    fun end()
}
