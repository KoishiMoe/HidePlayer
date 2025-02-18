package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.events.PlayerJoinCallback;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
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
            if (HidePlayer.getVisibilityManager().getPlayerCapability(instance).canSeeHiddenPlayer()) {
                instance.sendMessageToClient(text, overlay);
            }
            return;
        }
        instance.sendMessageToClient(text, overlay);
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
            return new HiddenPlayerText(message);
        }
        return message;
    }
}