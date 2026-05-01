#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 RectSize;
uniform float Radius;
uniform vec4 Color;

float sdRoundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    // Convert texcoords to pixel space centered at (0,0)
    vec2 p = (texCoord - 0.5) * RectSize;
    vec2 b = RectSize * 0.5;

    float dist = sdRoundedBox(p, b, Radius);

    // Smooth edges (anti-aliasing)
    float alpha = smoothstep(0.0, -1.0, dist);
    fragColor = vec4(Color.rgb, Color.a * alpha);
}