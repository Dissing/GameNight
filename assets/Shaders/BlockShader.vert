
uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;
in uint inNormal;
varying vec4 texCoord;
flat out int blockInfo;

void main() { 
    // Vertex transformation 
    texCoord = vec4(inPosition,1.0);
    blockInfo = int(inNormal);
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0); 
}
