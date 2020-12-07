attribute vec4 a_position;
attribute vec2 a_texCoords;
attribute vec4 a_color;

varying vec4 v_color;
varying vec2 v_texCoords;

uniform mat4 u_projection;

void main() {
    v_color = a_color;
    gl_Position = u_projection * a_position;
    v_texCoords = vec2(a_texCoords.x, 1.0 - a_texCoords.y);
}