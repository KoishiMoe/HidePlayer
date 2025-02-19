package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {
    // add scoreboard
    @Redirect(
            method = "startSyncing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void filterScoreboardPackets(ServerPlayNetworkHandler instance, Packet<?> packet) {
        if (packet instanceof ScoreboardScoreUpdateS2CPacket scorePacket) {
            ServerPlayerEntity viewer = instance.getPlayer();
            String targetName = scorePacket.scoreHolderName();

            if (targetName.equals(viewer.getGameProfile().getName())
                    || HidePlayer.getVisibilityManager().getPlayerCapability(viewer).canSeeHiddenPlayer()
                    || HidePlayer.getVisibilityManager().getScoreBoardCache().checkNoHide(targetName)) {
                instance.sendPacket(packet);
            }
        } else {
            instance.sendPacket(packet);
        }
    }

    // score update
    @Redirect(
            method = "updateScore",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void filterScoreUpdate(PlayerManager instance, Packet<?> packet) {
        var vm = HidePlayer.getVisibilityManager();
        if (packet instanceof ScoreboardScoreUpdateS2CPacket scorePacket) {
            for (ServerPlayerEntity viewer : instance.getPlayerList()) {
                String targetName = scorePacket.scoreHolderName();

                if (targetName.equals(viewer.getGameProfile().getName())
                        || vm.getPlayerCapability(viewer).canSeeHiddenPlayer()
                        || vm.getScoreBoardCache().checkNoHide(targetName)) {
                    viewer.networkHandler.sendPacket(packet);
                }
            }
        } else {
            instance.sendToAll(packet);
        }
    }
}