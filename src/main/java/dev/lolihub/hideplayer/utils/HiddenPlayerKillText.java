package dev.lolihub.hideplayer.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class HiddenPlayerKillText extends HiddenPlayerText {
    private final LivingEntity victim;

    public HiddenPlayerKillText(Component text, LivingEntity victim, ServerPlayer attacker) {
        super(text, attacker);
        this.victim = victim;
    }

    public Component _getGenericText() {
        return Component.translatable("death.attack.generic", this.victim.getDisplayName());
    }
}
