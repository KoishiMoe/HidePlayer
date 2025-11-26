package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.Commons;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {
    // add scoreboard
    @Redirect(
            method = "startTrackingObjective",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void filterScoreboardPackets(ServerGamePacketListenerImpl instance, Packet<?> packet) {
        Commons.filterScoreBoardPackets(instance, packet);
    }

    // score update
    @Redirect(
            method = "onScoreChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void filterScoreUpdate(PlayerList instance, Packet<?> packet) {
        var vm = HidePlayer.getVisibilityManager();
        if (packet instanceof ClientboundSetScorePacket scorePacket) {
            for (ServerPlayer viewer : instance.getPlayers()) {
                String targetName = scorePacket.owner();

                if (targetName.equals(viewer.getGameProfile().name())
                        || vm.getPlayerCapability(viewer).canSeeHiddenPlayer()
                        || vm.getScoreBoardCache().checkNoHide(targetName)) {
                    viewer.connection.send(packet);
                }
            }
        } else {
            instance.broadcastAll(packet);
        }
    }
}