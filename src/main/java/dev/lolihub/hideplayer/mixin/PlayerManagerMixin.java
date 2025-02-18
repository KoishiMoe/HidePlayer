package dev.lolihub.hideplayer.mixin;

import com.mojang.authlib.GameProfile;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.events.PlayerJoinCallback;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.network.ClientConnection;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At("TAIL"), method = "onPlayerConnect")
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        PlayerJoinCallback.EVENT.invoker().joinServer(player);
    }

    @Redirect(
            method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendMessageToClient(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private void redirectSendMessageToClient(ServerPlayerEntity instance, Text text, boolean overlay) {
        if (text instanceof HiddenPlayerText) {
            if (HidePlayer.getVisibilityManager().getPlayerCapability(instance).canSeeHiddenPlayer()) {
                instance.sendMessageToClient(text, overlay);
            }
            return;
        }
        instance.sendMessageToClient(text, overlay);
    }

    // Inject and cancel the join broadcast if the player is hidden
    // ModifyArg and Redirect can't capture the local variable player
    // So we inject before the broadcast method and redirect the original call to a dummy method
    @Inject(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void redirectJoinBroadcast(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci, GameProfile gameProfile, UserCache userCache, String string, Optional optional, RegistryKey registryKey, ServerWorld serverWorld, ServerWorld serverWorld2, String string2, WorldProperties worldProperties, ServerPlayNetworkHandler serverPlayNetworkHandler, GameRules gameRules, boolean bl, boolean bl2, boolean bl3, ServerRecipeManager serverRecipeManager, MutableText mutableText) {
        if (!HidePlayer.getVisibilityManager().getPlayerCapability(player).showSystemMessage()) {
            ((PlayerManager) (Object) this).broadcast(new HiddenPlayerText(mutableText.formatted(Formatting.YELLOW)), false);
        } else {
            ((PlayerManager) (Object) this).broadcast(mutableText.formatted(Formatting.YELLOW), false);
        }
    }

    @Redirect(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private void redirectJoinBroadcast(PlayerManager playerManager, Text text, boolean bl) {
        // Do nothing
    }
}