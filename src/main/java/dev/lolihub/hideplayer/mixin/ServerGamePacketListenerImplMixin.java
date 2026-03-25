package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.events.PlayerLeaveCallback;
import dev.lolihub.hideplayer.utils.Commons;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    // player leave event
    @Inject(at = @At(value = "TAIL"), method = "onDisconnect")
    private void onPlayerLeave(DisconnectionDetails info, CallbackInfo ci) {
        PlayerLeaveCallback.EVENT.invoker().leaveServer(this.player);
    }

    // player leave message
    @ModifyArg(
            method = "removePlayerFromWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"
            )
    )
    private Component onPlayerLeaveBroadcast(Component message) {
        if (HidePlayer.getVisibilityManager().getPlayerCapability(this.player).hideSystemMessage()) {
            return new HiddenPlayerText(message, this.player);
        }
        return message;
    }

    // hat change
    @Redirect(
            method = "handleClientInformation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void redirectSendToAll(PlayerList instance, Packet<?> packet) {
        Commons.redirectSendToAll(instance, packet, this.player);
    }

    // initialize chat session
    @Redirect(
            method = "lambda$resetPlayerChatState$0",  // inside resetPlayerChatState()
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void redirectSendToAll2(PlayerList instance, Packet<?> packet) {
        Commons.redirectSendToAll(instance, packet, this.player);
    }
}
