package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MsgCommand.class)
public class MsgCommandMixin {
    // /msg, /tell, /w
    @Redirect(
            method = "sendMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;sendChatMessage(Lnet/minecraft/network/chat/OutgoingChatMessage;ZLnet/minecraft/network/chat/ChatType$Bound;)V"
            )
    )
    private static void redirectSendChatMessage(ServerPlayer instance, OutgoingChatMessage message, boolean filterMaskEnabled, ChatType.Bound params, @Local(argsOnly = true)CommandSourceStack source) {
        var player = source.getPlayer();
        if (player != null && HidePlayer.getVisibilityManager().getPlayerCapability(player).showInGame(instance)) {
            instance.sendChatMessage(message, filterMaskEnabled, params);
        }
    }
}
