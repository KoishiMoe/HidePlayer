package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.network.chat.Component;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {
    @Shadow
    private ServerPlayer player;

    // advancement message
    @ModifyArg(
            method = "lambda$award$0",  // holder.value().display().ifPresent lambda
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"
            )
    )
    private Component modifyAdvancementMessage(Component message) {
        if (HidePlayer.getVisibilityManager().getPlayerCapability(player).hideSystemMessage()) {
            return new HiddenPlayerText(message, player);
        }
        return message;
    }
}
