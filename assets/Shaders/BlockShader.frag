
varying vec4 texCoord;
//aflat in uint blockInfo;
uniform sampler2D Atlas;

void main() {
    vec4 color;
    //if (blockInfo == 2) {
      //  color = vec4(1.0,0.5,0.2,1.0);//texture2D(Atlas, vec2( (fract(texCoord.x)) / 32.0, fract(texCoord.z) / 32.0 ));
    //} else {
        color = texture2D(Atlas, vec2( (fract(texCoord.x + texCoord.z)) / 32.0, fract(texCoord.y) / 32.0 ));
    //}
    gl_FragColor = color;
}

