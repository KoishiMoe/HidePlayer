package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.world.ServerChunkLoadingManager$EntityTracker")
public class ServerChunkLoadingManagerMixin {
    @Final
    @Shadow
    Entity entity;

    // Prevents server from sending entity spawn packet to players who shouldn't see the player. This solves the issue of leaking player's UUID.
    @Inject(
            at = @At("HEAD"),
            method = "updateTrackedStatus(Lnet/minecraft/server/network/ServerPlayerEntity;)V",
            cancellable = true
    )
    private void onUpdateTrackedStatus(ServerPlayerEntity player, CallbackInfo ci) {
        if (player != entity && entity instanceof ServerPlayerEntity &&
                !HidePlayer.getVisibilityManager().getPlayerCapability((ServerPlayerEntity) entity).showInGame(player)) {
            ci.cancel();
        }
    }
}
