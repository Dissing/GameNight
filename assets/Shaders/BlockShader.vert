#define LIGHT_BITMASK 0x000F0000

uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;
in uint inNormal;
varying vec4 texCoord;
varying float light;
flat out int blockInfo;

void main() {
    // Vertex transformation
    texCoord = vec4(inPosition,1.0);
    blockInfo = int(inNormal);
    light = ((blockInfo & LIGHT_BITMASK) >> 16) / 15f;
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}
