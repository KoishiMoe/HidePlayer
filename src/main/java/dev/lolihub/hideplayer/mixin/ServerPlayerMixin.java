package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.HiddenPlayerKillText;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    // all system messages go through this method
    @Inject(method = "sendSystemMessage(Lnet/minecraft/network/chat/Component;Z)V", at = @At("HEAD"), cancellable = true)
    private void sendMessageToClient(Component message, boolean overlay, CallbackInfo ci, @Local(argsOnly = true) LocalRef<Component> messageRef) {
        if (message instanceof HiddenPlayerText) {
            if (((HiddenPlayerText) message)._getPlayerUUID().equals(((ServerPlayer) (Object) this).getStringUUID())) {
                return;
            }
            if (HidePlayer.getVisibilityManager().getPlayerCapability((ServerPlayer) (Object) this).canSeeHiddenPlayer()) {
                return;
            }
            if (message instanceof HiddenPlayerKillText) {
                messageRef.set(((HiddenPlayerKillText) message)._getGenericText());
                return;
            }
            ci.cancel();
        }
    }

    @Redirect(
            method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;)V"
            )
    )
    private void sendDeathPacket(ServerGamePacketListenerImpl serverPlayNetworkHandler, Packet packet, ChannelFutureListener channelFutureListener, @Local Component text) {
        boolean sendRaw = !(text instanceof HiddenPlayerText);
        if (!sendRaw) {
            if (((HiddenPlayerText) text)._getPlayerUUID().equals(this.getStringUUID())) sendRaw = true;
        }
        if (!sendRaw) {
            if (HidePlayer.getVisibilityManager().getPlayerCapability((ServerPlayer) (Object) this).canSeeHiddenPlayer())
                sendRaw = true;
        }
        if (sendRaw) {
            serverPlayNetworkHandler.send(packet, channelFutureListener);
            return;
        }
        serverPlayNetworkHandler.send(
                new ClientboundPlayerCombatKillPacket(this.getId(), ((HiddenPlayerKillText) text)._getGenericText()),
                PacketSendListener.exceptionallySend(
                        () -> new ClientboundPlayerCombatKillPacket(
                                this.getId(),
                                Component.translatable("death.attack.even_more_magic", this.getDisplayName()).withStyle(
                                        style -> style.withHoverEvent(new HoverEvent.ShowText(
                                                Component.translatable(
                                                        "death.attack.message_too_long",
                                                        Component.literal(((HiddenPlayerKillText) text)._getGenericText()
                                                                .getString(256)).withStyle(ChatFormatting.YELLOW)
                                                )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
