package dev.lolihub.hideplayer;

import dev.lolihub.hideplayer.core.VisibilityManager;
import dev.lolihub.hideplayer.events.PlayerLeaveCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class HidePlayer implements ModInitializer {
    public static final String MOD_ID = "hideplayer";

    private static final VisibilityManager visibilityManager = new VisibilityManager();
    private static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> HidePlayer.server = server);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> visibilityManager.getScoreBoardCache().save());
        PlayerLeaveCallback.EVENT.register(visibilityManager::playerLeave);
    }

    public static VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }

    @Nullable
    public static MinecraftServer getServer() {
        return server;
    }
}
