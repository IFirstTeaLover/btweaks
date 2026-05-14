package dev.bsprout.btweaks.client.record;

import net.minecraft.network.chat.Component;

public record DisplayEntry(Component name, Component score, int scoreWidth) {}