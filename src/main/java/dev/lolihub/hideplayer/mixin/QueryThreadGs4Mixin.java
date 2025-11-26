package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(QueryThreadGs4.class)
public class QueryThreadGs4Mixin {
    // get player count, method 1
    @Redirect(
            method = "processPacket(Ljava/net/DatagramPacket;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/ServerInterface;getPlayerCount()I"
            )
    )
    private int getFilteredPlayerCount(ServerInterface server) {
        if (HidePlayer.getServer() == null) return server.getPlayerCount();
        return (int) HidePlayer.getServer().getPlayerList().getPlayers().stream()
                .filter(player -> HidePlayer.getVisibilityManager()
                        .getPlayerCapability(player)
                        .showStatusAndQuery())
                .count();
    }

    // get player count, in rules reply
    @Redirect(
            method = "buildRuleResponse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/ServerInterface;getPlayerCount()I"
            )
    )
    private int getFilteredPlayerCount1(ServerInterface server) {
        return getFilteredPlayerCount(server);
    }

    // get player names
    @Redirect(
            method = "buildRuleResponse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/ServerInterface;getPlayerNames()[Ljava/lang/String;"
            )
    )
    private String[] getFilteredPlayerNames(ServerInterface server) {
        if (HidePlayer.getServer() == null) return server.getPlayerNames();
        return HidePlayer.getServer().getPlayerList().getPlayers().stream()
                .filter(player -> HidePlayer.getVisibilityManager()
                        .getPlayerCapability(player)
                        .showStatusAndQuery())
                .map(player -> player.getGameProfile().name())
                .toArray(String[]::new);
    }
}