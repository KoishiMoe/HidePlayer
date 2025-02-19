package dev.lolihub.hideplayer.core;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VisibilityManager {
    private final Map<ServerPlayerEntity, PlayerCapability> capabilityMap = new ConcurrentHashMap<>();
    private final ScoreBoardCache scoreBoardCache;

    public VisibilityManager() {
        scoreBoardCache = new ScoreBoardCache();
    }

    public void playerLeave(ServerPlayerEntity player) {
        capabilityMap.remove(player);
    }

    public PlayerCapability getPlayerCapability(ServerPlayerEntity player) {
        var capability = capabilityMap.get(player);
        if (capability == null) {
            capability = new PlayerCapability(player);
            capabilityMap.put(player, capability);
        }
        return capability;
    }

    public ScoreBoardCache getScoreBoardCache() {
        return scoreBoardCache;
    }
}