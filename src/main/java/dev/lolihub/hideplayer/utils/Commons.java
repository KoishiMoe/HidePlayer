package dev.lolihub.hideplayer.utils;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class Commons {
    public static void filterScoreBoardPackets(ServerPlayNetworkHandler instance, Packet<?> packet) {
        if (packet instanceof ScoreboardScoreUpdateS2CPacket scorePacket) {
            ServerPlayerEntity viewer = instance.getPlayer();
            String targetName = scorePacket.scoreHolderName();

            if (targetName.equals(viewer.getGameProfile().name())
                    || HidePlayer.getVisibilityManager().getPlayerCapability(viewer).canSeeHiddenPlayer()
                    || HidePlayer.getVisibilityManager().getScoreBoardCache().checkNoHide(targetName)) {
                instance.sendPacket(packet);
            }
        } else {
            instance.sendPacket(packet);
        }
    }

    public static void redirectSendToAll(PlayerManager instance, Packet<?> packet, ServerPlayerEntity player) {
        assert packet instanceof PlayerListS2CPacket;
        var vm = HidePlayer.getVisibilityManager();
        if (vm.getPlayerCapability(player).showInGame()) {
            instance.sendToAll(packet);
        } else {
            for (ServerPlayerEntity p : instance.getPlayerList()) {
                if (vm.getPlayerCapability(p).canSeeHiddenPlayer()) {
                    p.networkHandler.sendPacket(packet);
                }
            }
            player.networkHandler.sendPacket(packet);
        }
    }
}
