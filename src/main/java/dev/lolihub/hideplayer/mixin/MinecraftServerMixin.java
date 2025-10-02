package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    // filter player list in status and query
    @Redirect(method = "createMetadataPlayers()Lnet/minecraft/server/ServerMetadata$Players;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;getPlayerList()Ljava/util/List;"))
    private List<ServerPlayerEntity> redirectGetPlayerList(PlayerManager instance) {
        var vm = HidePlayer.getVisibilityManager();
        return instance.getPlayerList().stream().filter(
                p -> vm.getPlayerCapability(p).showStatusAndQuery()).toList();
    }
}