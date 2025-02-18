package dev.lolihub.hideplayer;

import dev.lolihub.hideplayer.core.VisibilityManager;
import dev.lolihub.hideplayer.events.PlayerJoinCallback;
import dev.lolihub.hideplayer.events.PlayerLeaveCallback;
import net.fabricmc.api.ModInitializer;

public class HidePlayer implements ModInitializer {
    private static final VisibilityManager visibilityManager = new VisibilityManager();

    @Override
    public void onInitialize() {
        PlayerJoinCallback.EVENT.register(visibilityManager::playerJoin);
        PlayerLeaveCallback.EVENT.register(visibilityManager::playerLeave);
    }

    public static VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }
}
