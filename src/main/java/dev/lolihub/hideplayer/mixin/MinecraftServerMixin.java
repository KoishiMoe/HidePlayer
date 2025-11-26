package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    // filter player list in status and query
    @Redirect(method = "buildPlayerStatus()Lnet/minecraft/network/protocol/status/ServerStatus$Players;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;getPlayers()Ljava/util/List;"))
    private List<ServerPlayer> redirectGetPlayers(PlayerList instance) {
        var vm = HidePlayer.getVisibilityManager();
        return instance.getPlayers().stream().filter(
                p -> vm.getPlayerCapability(p).showStatusAndQuery()).toList();
    }
}