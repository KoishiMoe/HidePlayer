package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.events.PlayerJoinCallback;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.List;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow public abstract List<ServerPlayerEntity> getPlayerList();
    @Shadow @Final private MinecraftServer server;

    @Inject(at = @At("TAIL"), method = "onPlayerConnect")
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        PlayerJoinCallback.EVENT.invoker().joinServer(player);
    }

    @Redirect(
            method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendMessageToClient(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private void redirectSendMessageToClient(ServerPlayerEntity instance, Text text, boolean overlay) {
        if (text instanceof HiddenPlayerText) {
            if (HidePlayer.getVisibilityManager().getPlayerCapability(instance).canSeeHiddenPlayer()
                || ((HiddenPlayerText) text)._getPlayerUUID().equals(instance.getUuidAsString())
            ) {
                instance.sendMessageToClient(text, overlay);
            }
            return;
        }
        instance.sendMessageToClient(text, overlay);
    }

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

    @ModifyArg(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            ),
            index = 0
    )
    private Text redirectJoinBroadcast(Text message, @Local(argsOnly = true) ServerPlayerEntity player) {
        if (!HidePlayer.getVisibilityManager().getPlayerCapability(player).showSystemMessage()) {
            return new HiddenPlayerText(message, player);
        }
        return message;
    }

    @Redirect(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void redirectSendToAll(PlayerManager instance, Packet<?> packet, @Local(argsOnly = true) ServerPlayerEntity player) {
        assert packet instanceof PlayerListS2CPacket;
        if (HidePlayer.getVisibilityManager().getPlayerCapability(player).showInGame()) {
            instance.sendToAll(packet);
        } else {
            for (ServerPlayerEntity p : instance.getPlayerList()) {
                if (HidePlayer.getVisibilityManager().getPlayerCapability(p).canSeeHiddenPlayer()) {
                    p.networkHandler.sendPacket(packet);
                }
            }
            player.networkHandler.sendPacket(packet);
        }
    }

    @Redirect(
            method = "updatePlayerLatency",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void redirectSendToAll2(PlayerManager instance, Packet<?> packet) {
        var hiddenPlayers = instance.getPlayerList().stream().filter(p -> !HidePlayer.getVisibilityManager().getPlayerCapability(p).showInGame()).toList();
        if (hiddenPlayers.isEmpty()) {
            instance.sendToAll(packet);
        } else {
            var packet2 = new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_LATENCY),
                    instance.getPlayerList().stream().filter(p -> !hiddenPlayers.contains(p)).toList());
            for (ServerPlayerEntity p : instance.getPlayerList()) {
                if (HidePlayer.getVisibilityManager().getPlayerCapability(p).canSeeHiddenPlayer()) {
                    p.networkHandler.sendPacket(packet);
                } else {
                    p.networkHandler.sendPacket(packet2);
                }
            }
        }
    }

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

    @Redirect(
            method = "sendScoreboard",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void redirectSendScoreboard(ServerPlayNetworkHandler instance, Packet<?> packet) {
        if (packet instanceof ScoreboardScoreUpdateS2CPacket scorePacket) {
            ServerPlayerEntity viewer = instance.getPlayer();
            String targetName = scorePacket.scoreHolderName();
            ServerPlayerEntity target = server.getPlayerManager().getPlayer(targetName);

            if (targetName.equals(viewer.getGameProfile().getName())
                    || HidePlayer.getVisibilityManager().getPlayerCapability(viewer).canSeeHiddenPlayer()
                    || !HidePlayer.getVisibilityManager().getScoreBoardCache().check(targetName)) {
                instance.sendPacket(packet);
            }
        } else {
            instance.sendPacket(packet);
        }
    }
}