package dev.lolihub.hideplayer.utils;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class Commons {
    public static void filterScoreBoardPackets(ServerPlayNetworkHandler instance, Packet<?> packet) {
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

    public static ServerMetadata getServerMetadata(MinecraftServer server) {
        var vm = HidePlayer.getVisibilityManager();
        ServerMetadata metadata = server.getServerMetadata();
        if (metadata == null) return null;
        var players = server.getPlayerManager().getPlayerList().stream().filter(
                p -> vm.getPlayerCapability(p).showStatusAndQuery()).toList();
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
