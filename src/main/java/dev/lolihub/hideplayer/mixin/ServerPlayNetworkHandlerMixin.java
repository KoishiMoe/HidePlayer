package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.events.PlayerLeaveCallback;
import dev.lolihub.hideplayer.utils.Commons;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    // player leave event
    @Inject(at = @At(value = "TAIL"), method = "onDisconnected")
    private void onPlayerLeave(DisconnectionInfo info, CallbackInfo ci) {
        PlayerLeaveCallback.EVENT.invoker().leaveServer(this.player);
    }

    // player leave message
    @ModifyArg(
            method = "cleanUp",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private Text onPlayerLeaveBroadcast(Text message) {
        if (HidePlayer.getVisibilityManager().getPlayerCapability(this.player).hideSystemMessage()) {
            return new HiddenPlayerText(message, this.player);
        }
        return message;
    }

    // hat change
    @Redirect(
            method = "onClientOptions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void redirectSendToAll(PlayerManager instance, Packet<?> packet) {
        Commons.redirectSendToAll(instance, packet, this.player);
    }

    // initialize chat session
    @Redirect(
            method = "method_54439",  // inside setSession()
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void redirectSendToAll2(PlayerManager instance, Packet<?> packet) {
        Commons.redirectSendToAll(instance, packet, this.player);
    }
}
