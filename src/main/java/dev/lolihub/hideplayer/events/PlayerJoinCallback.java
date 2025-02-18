package dev.lolihub.hideplayer.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerJoinCallback {
    Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class, (listeners) -> (player) -> {
        for (PlayerJoinCallback listener : listeners) {
            listener.joinServer(player);
        }
    });

    void joinServer(ServerPlayerEntity player);
}
