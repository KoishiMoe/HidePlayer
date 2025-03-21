package dev.lolihub.hideplayer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import dev.lolihub.hideplayer.HidePlayer;
import dev.lolihub.hideplayer.utils.HiddenPlayerKillText;
import dev.lolihub.hideplayer.utils.HiddenPlayerText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

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

    @Redirect(
            method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V"
            )
    )
    private void sendDeathPacket(ServerPlayNetworkHandler serverPlayNetworkHandler, Packet<?> packet, PacketCallbacks packetCallbacks, @Local Text text) {
        boolean sendRaw = !(text instanceof HiddenPlayerText);
        if (!sendRaw) {
            if (((HiddenPlayerText) text)._getPlayerUUID().equals(this.getUuidAsString())) sendRaw = true;
        }
        if (!sendRaw) {
            if (HidePlayer.getVisibilityManager().getPlayerCapability((ServerPlayerEntity) (Object) this).canSeeHiddenPlayer())
                sendRaw = true;
        }
        if (sendRaw) {
            serverPlayNetworkHandler.send(packet, packetCallbacks);
            return;
        }
        serverPlayNetworkHandler.send(
                new DeathMessageS2CPacket(this.getId(), ((HiddenPlayerKillText) text)._getGenericText()),
                PacketCallbacks.of(
                        () -> new DeathMessageS2CPacket(
                                this.getId(),
                                Text.translatable("death.attack.even_more_magic", this.getDisplayName()).styled(
                                        style -> style.withHoverEvent(new HoverEvent.ShowText(
                                                Text.translatable(
                                                        "death.attack.message_too_long",
                                                        Text.literal(((HiddenPlayerKillText) text)._getGenericText()
                                                                .asTruncatedString(256)).formatted(Formatting.YELLOW)
                                                )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
