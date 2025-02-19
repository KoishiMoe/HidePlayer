package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamMsgCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
    // /teammsg
    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"
            )
    )
    private static void redirectSendChatMessage(ServerPlayerEntity instance, SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params, @Local(argsOnly = true)ServerCommandSource source) {
        var player = source.getPlayer();
        if (player != null && HidePlayer.getVisibilityManager().getPlayerCapability(player).showInGame(instance)) {
            instance.sendChatMessage(message, filterMaskEnabled, params);
        }
    }
}
