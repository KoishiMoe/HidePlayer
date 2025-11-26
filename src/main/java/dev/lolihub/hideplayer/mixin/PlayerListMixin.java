package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.Commons;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow public abstract List<ServerPlayer> getPlayers();

    // normal chat message
    @Redirect(
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;sendChatMessage(Lnet/minecraft/network/chat/OutgoingChatMessage;ZLnet/minecraft/network/chat/ChatType$Bound;)V"
            )
    )
    private void redirectChatMessage(ServerPlayer instance, OutgoingChatMessage message, boolean filterMaskEnabled, ChatType.Bound params, @Local(argsOnly = true) ServerPlayer sender) {
        if (sender != null && sender != instance && !HidePlayer.getVisibilityManager().getPlayerCapability(sender).showInGame(instance)) {
            return;
        }
        instance.sendChatMessage(message, filterMaskEnabled, params);
    }

    // join message
    @ModifyArg(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"
            ),
            index = 0
    )
    private Component redirectJoinBroadcast(Component message, @Local(argsOnly = true) ServerPlayer player) {
        if (HidePlayer.getVisibilityManager().getPlayerCapability(player).hideSystemMessage()) {
            return new HiddenPlayerText(message, player);
        }
        return message;
    }

    // playlist to others when player join
    @Redirect(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void redirectSendToAll(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) ServerPlayer player) {
        Commons.redirectSendToAll(instance, packet, player);
    }

    // playerlist to everyone when update player latency
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void redirectSendToAll2(PlayerList instance, Packet<?> packet) {
        var vm = HidePlayer.getVisibilityManager();
        var noHiddenPlayers = instance.getPlayers().stream().filter(p -> vm.getPlayerCapability(p).showInGame()).toList();
        if (noHiddenPlayers.size() == instance.getPlayerCount()) {
            instance.broadcastAll(packet);
        } else {
            var packet2 = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY), noHiddenPlayers);
            for (ServerPlayer p : instance.getPlayers()) {
                if (vm.getPlayerCapability(p).canSeeHiddenPlayer()) {
                    p.connection.send(packet);
                } else {
                    p.connection.send(packet2);
                }
            }
        }
    }

    // playerlist to the joining player
    @ModifyArg(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
            ),
            index = 0
    )
    private Packet<?> redirectJoinSendPacket(Packet<?> packet, @Local(argsOnly = true) ServerPlayer player) {
        if (packet instanceof ClientboundPlayerInfoUpdatePacket) {
            if (!HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer()) {
                return ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(
                    this.getPlayers().stream().filter(p -> HidePlayer.getVisibilityManager().getPlayerCapability(p).showInGame()).toList()
                );
            }
        }
        return packet;
    }

    // initial scoreboard setup when player join
    @Redirect(
            method = "updateEntireScoreboard",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void redirectSendScoreboard(ServerGamePacketListenerImpl instance, Packet<?> packet) {
        Commons.filterScoreBoardPackets(instance, packet);
    }
}