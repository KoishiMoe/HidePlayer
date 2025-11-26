package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.RandomCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RandomCommand.class)
public class RandomCommandMixin {
    // /random roll will broadcast a message
    @ModifyArg(
            method = "randomSample",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"
            ),
            index = 0
    )
    private static Component modifyRollMessage(Component message, @Local(argsOnly = true) CommandSourceStack source) {
        if (source.isPlayer()) {
            var player = source.getPlayer();
            if (HidePlayer.getVisibilityManager().getPlayerCapability(player).hideSystemMessage()) {
                assert player != null;
                return new HiddenPlayerText(message, player);
            }
        }
        return message;
    }
}
