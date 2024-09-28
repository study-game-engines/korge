package korlibs.graphics.metal.shader

import korlibs.graphics.shader.*
import korlibs.korge.internal.*

fun Pair<VertexShader, FragmentShader>.toNewMetalShaderStringResult(bufferInputsLayout: MetalShaderBufferInputLayouts): MetalShaderGenerator.Result = let { (vertexShader, fragmentShader) -> MetalShaderGenerator(vertexShader, fragmentShader, bufferInputsLayout) }
        .generateResult()
