package dev.bsprout.btweaks.client.helpers;

import net.minecraft.network.chat.Component;

public record TabEntry(Component name, int score, Component formattedScore, int scoreWidth) {}