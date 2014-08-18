
#define UP_NORMAL_BITMASK 0x0F000000

varying vec4 texCoord;
flat in int blockInfo;
uniform sampler2D Atlas;

void main() {
    vec4 color;
    if ((blockInfo & UP_NORMAL_BITMASK) == 0) {
        color = texture2D(Atlas, vec2( (fract(texCoord.x + texCoord.z)) / 32.0, fract(texCoord.y) / 32.0 )) * 0.85;
    } else {
        color = texture2D(Atlas, vec2( (fract(texCoord.x)) / 32.0, fract(texCoord.z) / 32.0 ));
    }
    gl_FragColor = color;
}

