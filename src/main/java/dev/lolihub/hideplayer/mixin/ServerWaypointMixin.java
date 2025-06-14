package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.waypoint.ServerWaypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWaypoint.class)
public interface ServerWaypointMixin {
    @Inject(method = "cannotReceive(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at= @At("HEAD"), cancellable = true)
    private static void cannotReceive(LivingEntity source, ServerPlayerEntity receiver, CallbackInfoReturnable<Boolean> cir) {
        if (source instanceof ServerPlayerEntity) {
            if (!HidePlayer.getVisibilityManager().getPlayerCapability((ServerPlayerEntity) source).showInLocatorBar(receiver)) {
                cir.setReturnValue(true);
            }
        }
    }
}
