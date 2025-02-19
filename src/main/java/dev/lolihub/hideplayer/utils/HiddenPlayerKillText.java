package dev.lolihub.hideplayer.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class HiddenPlayerKillText extends HiddenPlayerText {
    private final LivingEntity victim;

    public HiddenPlayerKillText(Text text, LivingEntity victim, ServerPlayerEntity attacker) {
        super(text, attacker);
        this.victim = victim;
    }

    public Text _getGenericText() {
        return Text.translatable("death.attack.generic", this.victim.getDisplayName());
    }
}
