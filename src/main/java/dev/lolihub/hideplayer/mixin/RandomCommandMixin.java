package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.server.command.RandomCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RandomCommand.class)
public class RandomCommandMixin {
    // /random roll will broadcast a message
    @ModifyArg(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            ),
            index = 0
    )
    private static Text modifyRollMessage(Text message, @Local(argsOnly = true) ServerCommandSource source) {
        if (source.isExecutedByPlayer()) {
            var player = source.getPlayer();
            if (HidePlayer.getVisibilityManager().getPlayerCapability(player).hideSystemMessage()) {
                assert player != null;
                return new HiddenPlayerText(message, player);
            }
        }
        return message;
    }
}
