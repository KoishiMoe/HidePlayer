package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaypointTransmitter.class)
public interface WaypointTransmitterMixin {
    @Inject(method = "doesSourceIgnoreReceiver(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/server/level/ServerPlayer;)Z", at= @At("HEAD"), cancellable = true)
    private static void doesSourceIgnoreReceiver(LivingEntity source, ServerPlayer receiver, CallbackInfoReturnable<Boolean> cir) {
        if (source instanceof ServerPlayer) {
            if (!HidePlayer.getVisibilityManager().getPlayerCapability((ServerPlayer) source).showInLocatorBar(receiver)) {
                cir.setReturnValue(true);
            }
        }
    }
}
