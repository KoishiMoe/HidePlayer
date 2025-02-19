package dev.lolihub.hideplayer.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class HiddenPlayerKillText extends HiddenPlayerText {
    private final ServerPlayerEntity victim;

    public HiddenPlayerKillText(Text text, ServerPlayerEntity player) {
        super(text, player);
        this.victim = player;
    }

    public Text _getGenericText() {
        return Text.translatable("death.attack.generic", this.victim.getDisplayName());
    }
}
