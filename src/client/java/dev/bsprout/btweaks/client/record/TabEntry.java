package dev.bsprout.btweaks.client.record;

import net.minecraft.network.chat.Component;

public record TabEntry(Component name, int score, Component formattedScore, int scoreWidth) {}