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

    public void playerJoin(ServerPlayerEntity player) {
//        var capability = CapabilityMap.get(player);
//        if (capability != null) {
//            capability.flush();
//            return;
//        }
//        capability = new PlayerCapability(player);
//        CapabilityMap.put(player, capability);
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

    public void flushCapability() {
        for (var capability : capabilityMap.values()) {
            capability.flush();
        }
    }

    public ScoreBoardCache getScoreBoardCache() {
        return scoreBoardCache;
    }
}