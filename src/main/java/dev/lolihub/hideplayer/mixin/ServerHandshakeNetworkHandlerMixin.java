package dev.lolihub.hideplayer.mixin;

import dev.lolihub.hideplayer.utils.Commons;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
    // status query
    @Redirect(
            method = "onHandshake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;getServerMetadata()Lnet/minecraft/server/ServerMetadata;"
            )
    )
    private ServerMetadata getServerMetadata(MinecraftServer server) {
        return Commons.getServerMetadata(server);
    }
}
