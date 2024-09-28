package korlibs.graphics.metal.shader

import korlibs.graphics.shader.FragmentShader
import korlibs.graphics.shader.VertexShader

fun Pair<VertexShader, FragmentShader>.toNewMetalShaderStringResult(bufferInputsLayout: MetalShaderBufferInputLayouts): MetalShaderGenerator.Result {
    return let {
            (vertexShader, fragmentShader) -> MetalShaderGenerator(vertexShader, fragmentShader, bufferInputsLayout)
    }.generateResult()
}

