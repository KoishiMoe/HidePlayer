package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.HiddenPlayerKillText;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {
    @Final
    @Shadow
    private LivingEntity entity;

    @Shadow @Final private List<DamageRecord> recentDamage;

    // death message
    @Inject(method = "getDeathMessage", at = @At("RETURN"), cancellable = true)
    private void onGetDeathMessage(CallbackInfoReturnable<Text> cir) {
        // death caused by hidden player
        if (!recentDamage.isEmpty()) {
            DamageSource source = recentDamage.getLast().damageSource();
            ServerPlayerEntity player = null;
            if (source.getSource() instanceof ServerPlayerEntity) {
                player = (ServerPlayerEntity) source.getSource();
            } else if (entity.getPrimeAdversary() instanceof ServerPlayerEntity) {
                player = (ServerPlayerEntity) entity.getPrimeAdversary();
            }
            if (player != null && HidePlayer.getVisibilityManager().getPlayerCapability(player).hideSystemMessage()) {
                cir.setReturnValue(new HiddenPlayerKillText(cir.getReturnValue(), entity, player));
            }
        }

        // death of hidden player
        // check this after the above check to avoid hidden player killing hidden player causing leak
        if (entity instanceof ServerPlayerEntity player2) {
            if (HidePlayer.getVisibilityManager().getPlayerCapability(player2).hideSystemMessage()) {
                cir.setReturnValue(new HiddenPlayerText(cir.getReturnValue(), player2));
            }
        }
    }
}