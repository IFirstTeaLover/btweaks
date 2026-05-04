#version 150 core

in vec4 vertColor;
in vec2 rectSize;
in vec2 rectLoc;

out vec4 fragColor;

float roundedBoxSDF(vec2 center, vec2 size, float radius) {
    vec2 q = abs(center) - size + radius;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;
}

void main() {
    float radius = min(rectSize.x, rectSize.y) * 0.15;
    float softness = 1.0;
    vec2 center = gl_FragCoord.xy - rectLoc - rectSize * 0.5;
    float dist = roundedBoxSDF(center, rectSize * 0.5, radius);
    float alpha = 1.0 - smoothstep(-softness, softness, dist);
    fragColor = vec4(vertColor.rgb, vertColor.a * alpha);
}