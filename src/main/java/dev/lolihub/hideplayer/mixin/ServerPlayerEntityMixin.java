package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    // all broadcast messages go through this method
    @Inject(method = "sendMessageToClient", at = @At("HEAD"), cancellable = true)
    private void sendMessageToClient(Text message, boolean overlay, CallbackInfo ci) {
        if (message instanceof HiddenPlayerText) {
            if (((HiddenPlayerText) message)._getPlayerUUID().equals(((ServerPlayerEntity) (Object) this).getUuidAsString())) {
                return;
            }
            if (HidePlayer.getVisibilityManager().getPlayerCapability((ServerPlayerEntity) (Object) this).canSeeHiddenPlayer()) {
                return;
            }
            ci.cancel();
        }
    }
}
