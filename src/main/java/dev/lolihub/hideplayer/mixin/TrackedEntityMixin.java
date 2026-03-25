package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public class TrackedEntityMixin {
    @Final
    @Shadow
    private Entity entity;

    // Prevents server from sending entity spawn packet to players who shouldn't see the player. This solves the issue of leaking player's UUID.
    @Inject(
            at = @At("HEAD"),
            method = "updatePlayer(Lnet/minecraft/server/level/ServerPlayer;)V",
            cancellable = true
    )
    private void onUpdatePlayer(ServerPlayer player, CallbackInfo ci) {
        if (player != entity && entity instanceof ServerPlayer &&
                !HidePlayer.getVisibilityManager().getPlayerCapability((ServerPlayer) entity).showInGame(player)) {
            ci.cancel();
        }
    }
}
