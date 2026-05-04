#version 150 core

in vec3 Position;
in vec4 Color;
in vec2 RectSize;
in vec2 RectLoc;

out vec4 vertColor;
out vec2 rectSize;
out vec2 rectLoc;

void main() {
    gl_Position = vec4(Position.xy, 0.0, 1.0);
    vertColor = Color;
    rectSize = RectSize;
    rectLoc = RectLoc;
}