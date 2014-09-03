
uniform mat4 g_WorldViewProjectionMatrix;
uniform vec4 Color;
in vec3 inPosition;
in vec2 inTexCoord;
out vec2 outTexCoord;
out vec4 outColor;

void main() {
    outTexCoord = inTexCoord;
    outColor = Color;
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}
