package dev.bsprout.btweaks.client.helpers;

import net.minecraft.network.chat.Component;

public record DisplayEntry(Component name, Component score, int scoreWidth) {}