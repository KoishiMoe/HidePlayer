package dev.lolihub.hideplayer.utils;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;

public class Commons {
    public static void filterScoreBoardPackets(ServerGamePacketListenerImpl instance, Packet<?> packet) {
        if (packet instanceof ClientboundSetScorePacket scorePacket) {
            ServerPlayer viewer = instance.getPlayer();
            String targetName = scorePacket.owner();

            if (targetName.equals(viewer.getGameProfile().name())
                    || HidePlayer.getVisibilityManager().getPlayerCapability(viewer).canSeeHiddenPlayer()
                    || HidePlayer.getVisibilityManager().getScoreBoardCache().checkNoHide(targetName)) {
                instance.send(packet);
            }
        } else {
            instance.send(packet);
        }
    }

    public static void redirectSendToAll(PlayerList instance, Packet<?> packet, ServerPlayer player) {
        assert packet instanceof ClientboundPlayerInfoUpdatePacket;
        var vm = HidePlayer.getVisibilityManager();
        if (vm.getPlayerCapability(player).showInGame()) {
            instance.broadcastAll(packet);
        } else {
            for (ServerPlayer p : instance.getPlayers()) {
                if (vm.getPlayerCapability(p).canSeeHiddenPlayer()) {
                    p.connection.send(packet);
                }
            }
            player.connection.send(packet);
        }
    }
}
