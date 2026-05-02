#version 150

in vec2 uv;

uniform vec4 ColorModulator;
uniform vec2 Size;
uniform float Radius;

out vec4 fragColor;

void main() {
    vec2 pos = uv * Size;
    vec2 corner = clamp(pos, vec2(Radius), Size - vec2(Radius));
    float dist = length(pos - corner);
    float alpha = 1.0 - smoothstep(Radius - 1.0, Radius, dist);
    if (alpha < 0.001) discard;
    fragColor = ColorModulator * vec4(1.0, 1.0, 1.0, alpha);
}