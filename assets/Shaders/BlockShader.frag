
#define UP_NORMAL_BITMASK 0x0F000000
#define TYPE_BITMASK 0x0000FFFF

varying vec4 texCoord;
varying float light;
flat in int blockInfo;
uniform sampler2D Atlas;

void main() {
    int type = blockInfo & TYPE_BITMASK;
    vec4 color;
    if ((blockInfo & UP_NORMAL_BITMASK) == 0) {
        color = texture2D(Atlas, vec2( (fract(texCoord.x + texCoord.z) + type - 1) / 32.0, fract(texCoord.y) / 32.0 )); // Type - 1 to use zero index texture atlas
    } else {
        color = texture2D(Atlas, vec2( (fract(texCoord.x) + type - 1) / 32.0, fract(texCoord.z) / 32.0 )); // Type - 1 to use zero index texture atlas
    }
    gl_FragColor = color*light;
}

