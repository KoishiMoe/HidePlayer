package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.HiddenPlayerKillText;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@Mixin(CombatTracker.class)
public class CombatTrackerMixin {
    @Final
    @Shadow
    private LivingEntity mob;

    @Shadow @Final private List<CombatEntry> entries;

    // death message
    @Inject(method = "getDeathMessage", at = @At("RETURN"), cancellable = true)
    private void onGetDeathMessage(CallbackInfoReturnable<Component> cir) {
        // death caused by hidden player
        if (!entries.isEmpty()) {
            DamageSource source = entries.getLast().source();
            ServerPlayer player = null;
            if (source.getDirectEntity() instanceof ServerPlayer) {
                player = (ServerPlayer) source.getDirectEntity();
            } else if (mob.getKillCredit() instanceof ServerPlayer) {
                player = (ServerPlayer) mob.getKillCredit();
            }
            if (player != null && HidePlayer.getVisibilityManager().getPlayerCapability(player).hideSystemMessage()) {
                cir.setReturnValue(new HiddenPlayerKillText(cir.getReturnValue(), mob, player));
            }
        }

        // death of hidden player
        // check this after the above check to avoid hidden player killing hidden player causing leak
        if (mob instanceof ServerPlayer player2) {
            if (HidePlayer.getVisibilityManager().getPlayerCapability(player2).hideSystemMessage()) {
                cir.setReturnValue(new HiddenPlayerText(cir.getReturnValue(), player2));
            }
        }
    }
}