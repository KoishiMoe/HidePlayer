package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.QueryResponseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(QueryResponseHandler.class)
public class QueryResponseHandlerMixin {
    // get player count, method 1
    @Redirect(
            method = "handle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/dedicated/DedicatedServer;getCurrentPlayerCount()I"
            )
    )
    private int getFilteredPlayerCount(DedicatedServer server) {
        if (HidePlayer.getServer() == null) return server.getCurrentPlayerCount();
        return (int) HidePlayer.getServer().getPlayerManager().getPlayerList().stream()
                .filter(player -> HidePlayer.getVisibilityManager()
                        .getPlayerCapability(player)
                        .showStatusAndQuery())
                .count();
    }

    // get player count, in rules reply
    @Redirect(
            method = "createRulesReply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/dedicated/DedicatedServer;getCurrentPlayerCount()I"
            )
    )
    private int getFilteredPlayerCount1(DedicatedServer server) {
        return getFilteredPlayerCount(server);
    }

    // get player names
    @Redirect(
            method = "createRulesReply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/dedicated/DedicatedServer;getPlayerNames()[Ljava/lang/String;"
            )
    )
    private String[] getFilteredPlayerNames(DedicatedServer server) {
        if (HidePlayer.getServer() == null) return server.getPlayerNames();
        return HidePlayer.getServer().getPlayerManager().getPlayerList().stream()
                .filter(player -> HidePlayer.getVisibilityManager()
                        .getPlayerCapability(player)
                        .showStatusAndQuery())
                .map(player -> player.getGameProfile().getName())
                .toArray(String[]::new);
    }
}