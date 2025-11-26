package dev.lolihub.hideplayer.utils;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;

public class HiddenPlayerText implements Component {
    private final Component text;
    private final String playerUUID;

    public HiddenPlayerText(Component text, ServerPlayer player) {
        this.text = text;
        this.playerUUID = player.getStringUUID();
    }

    @Override
    public Style getStyle() {
        return text.getStyle();
    }

    @Override
    public ComponentContents getContents() {
        return text.getContents();
    }

    @Override
    public List<Component> getSiblings() {
        return text.getSiblings();
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        return text.getVisualOrderText();
    }

    public String _getPlayerUUID() {
        return playerUUID;
    }
}
