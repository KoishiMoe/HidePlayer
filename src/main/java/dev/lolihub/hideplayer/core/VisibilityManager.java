package dev.lolihub.hideplayer.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;

public class VisibilityManager {
    private final Map<ServerPlayer, PlayerCapability> capabilityMap = new ConcurrentHashMap<>();
    private final ScoreBoardCache scoreBoardCache;

    public VisibilityManager() {
        scoreBoardCache = new ScoreBoardCache();
    }

    public void playerLeave(ServerPlayer player) {
        capabilityMap.remove(player);
    }

    public PlayerCapability getPlayerCapability(ServerPlayer player) {
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