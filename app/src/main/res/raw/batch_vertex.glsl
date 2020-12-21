
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoords;

uniform mat4 u_projection;

varying vec2 v_texCoords;
varying vec4 v_color;

void main() {
    gl_Position = u_projection * a_position;
    v_color = a_color;
    v_texCoords = vec2(a_texCoords.x, 1.0 - a_texCoords.y);
}
