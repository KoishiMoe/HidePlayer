package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.HiddenPlayerKillText;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    // all system messages go through this method
    @Inject(method = "sendMessageToClient", at = @At("HEAD"), cancellable = true)
    private void sendMessageToClient(Text message, boolean overlay, CallbackInfo ci, @Local(argsOnly = true) LocalRef<Text> messageRef) {
        if (message instanceof HiddenPlayerText) {
            if (((HiddenPlayerText) message)._getPlayerUUID().equals(((ServerPlayerEntity) (Object) this).getUuidAsString())) {
                return;
            }
            if (HidePlayer.getVisibilityManager().getPlayerCapability((ServerPlayerEntity) (Object) this).canSeeHiddenPlayer()) {
                return;
            }
            if (message instanceof HiddenPlayerKillText) {
                messageRef.set(((HiddenPlayerKillText) message)._getGenericText());
                return;
            }
            ci.cancel();
        }
    }
}
