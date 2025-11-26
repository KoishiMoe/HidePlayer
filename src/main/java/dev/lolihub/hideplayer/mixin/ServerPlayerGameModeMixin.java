package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow @Final protected ServerPlayer player;

    // packet broadcast when game mode change
    @Redirect(
            method = "changeGameModeForPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void sendGameModeChangePacket(PlayerList instance, Packet<?> packet) {
        var vm = HidePlayer.getVisibilityManager();
        if (vm.getPlayerCapability(player).showInGame()) {
            instance.broadcastAll(packet);
        } else {
            for (ServerPlayer viewer: instance.getPlayers()) {
                if (viewer == player || vm.getPlayerCapability(viewer).canSeeHiddenPlayer()) {
                    viewer.connection.send(packet);
                }
            }
        }
    }
}
