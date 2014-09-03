
in vec2 outTexCoord;
in vec4 outColor;
uniform sampler2D Texture;

void main() {
    vec4 texColor = texture2D(Texture,outTexCoord);
    gl_FragColor = texColor.a != 0 ? texColor : outColor;
}

