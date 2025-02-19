package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
    @Final
    @Shadow
    private MinecraftServer server;
    @Final
    @Shadow
    private ClientConnection connection;

    @Redirect(
            method = "onHandshake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;getServerMetadata()Lnet/minecraft/server/ServerMetadata;"
            )
    )
    private ServerMetadata getServerMetadata(MinecraftServer server) {
        ServerMetadata metadata = server.getServerMetadata();
        if (metadata == null) return null;
        var players = server.getPlayerManager().getPlayerList().stream().filter(
                p -> HidePlayer.getVisibilityManager().getPlayerCapability(p).showStatusAndQuery()).toList();
        // pick first min(12, len(players)) players and shuffle them
        var list = players.stream().map(PlayerEntity::getGameProfile).limit(12).sorted((a, b) -> (int) (Math.random() * 3) - 1).toList();
        return new ServerMetadata(
                metadata.description(),
                Optional.of(new ServerMetadata.Players(server.getPlayerManager().getMaxPlayerCount(), players.size(), list)),
                metadata.version(),
                metadata.favicon(),
                metadata.secureChatEnforced()
        );
    }
}
