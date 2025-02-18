package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.events.PlayerLeaveCallback;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At(value = "TAIL"), method = "onDisconnected")
    private void onPlayerLeave(DisconnectionInfo info, CallbackInfo ci) {
        PlayerLeaveCallback.EVENT.invoker().leaveServer(this.player);
    }

    @ModifyArg(
            method = "cleanUp",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private Text onPlayerLeaveBroadcast(Text message) {
        if (!HidePlayer.getVisibilityManager().getPlayerCapability(this.player).showSystemMessage()) {
            return new HiddenPlayerText(message);
        }
        return message;
    }
}
