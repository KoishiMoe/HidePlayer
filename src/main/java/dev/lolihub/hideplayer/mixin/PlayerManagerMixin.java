package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.Commons;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.EnumSet;
import java.util.List;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow public abstract List<ServerPlayerEntity> getPlayerList();

    // normal chat message
    @Redirect(
            method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"
            )
    )
    private void redirectChatMessage(ServerPlayerEntity instance, SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params, @Local(argsOnly = true) ServerPlayerEntity sender) {
        if (sender != null && sender != instance && !HidePlayer.getVisibilityManager().getPlayerCapability(sender).showInGame(instance)) {
            return;
        }
        instance.sendChatMessage(message, filterMaskEnabled, params);
    }

    // join message
    @ModifyArg(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            ),
            index = 0
    )
    private Text redirectJoinBroadcast(Text message, @Local(argsOnly = true) ServerPlayerEntity player) {
        if (HidePlayer.getVisibilityManager().getPlayerCapability(player).hideSystemMessage()) {
            return new HiddenPlayerText(message, player);
        }
        return message;
    }

    // playlist to others when player join
    @Redirect(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void redirectSendToAll(PlayerManager instance, Packet<?> packet, @Local(argsOnly = true) ServerPlayerEntity player) {
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

    // playerlist to everyone when update player latency
    @Redirect(
            method = "updatePlayerLatency",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void redirectSendToAll2(PlayerManager instance, Packet<?> packet) {
        var vm = HidePlayer.getVisibilityManager();
        var noHiddenPlayers = instance.getPlayerList().stream().filter(p -> vm.getPlayerCapability(p).showInGame()).toList();
        if (noHiddenPlayers.size() == instance.getCurrentPlayerCount()) {
            instance.sendToAll(packet);
        } else {
            var packet2 = new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_LATENCY), noHiddenPlayers);
            for (ServerPlayerEntity p : instance.getPlayerList()) {
                if (vm.getPlayerCapability(p).canSeeHiddenPlayer()) {
                    p.networkHandler.sendPacket(packet);
                } else {
                    p.networkHandler.sendPacket(packet2);
                }
            }
        }
    }

    // playerlist to the joining player
    @ModifyArg(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
            ),
            index = 0
    )
    private Packet<?> redirectJoinSendPacket(Packet<?> packet, @Local(argsOnly = true) ServerPlayerEntity player) {
        if (packet instanceof PlayerListS2CPacket) {
            if (!HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer()) {
                return PlayerListS2CPacket.entryFromPlayer(
                    this.getPlayerList().stream().filter(p -> HidePlayer.getVisibilityManager().getPlayerCapability(p).showInGame()).toList()
                );
            }
        }
        return packet;
    }

    // initial scoreboard setup when player join
    @Redirect(
            method = "sendScoreboard",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void redirectSendScoreboard(ServerPlayNetworkHandler instance, Packet<?> packet) {
        Commons.filterScoreBoardPackets(instance, packet);
    }

    // Currently no leak found in this call, but just in case
    @Redirect(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;getServerMetadata()Lnet/minecraft/server/ServerMetadata;"
            )
    )
    private ServerMetadata getServerMetadata(MinecraftServer server) {
        return Commons.getServerMetadata(server);
    }
}