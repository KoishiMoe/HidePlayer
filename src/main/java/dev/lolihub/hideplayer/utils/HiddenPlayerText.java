package dev.lolihub.hideplayer.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

import java.util.List;

public class HiddenPlayerText implements Text {
    private final Text text;
    private final String playerUUID;

    public HiddenPlayerText(Text text, ServerPlayerEntity player) {
        this.text = text;
        this.playerUUID = player.getUuidAsString();
    }

    @Override
    public Style getStyle() {
        return text.getStyle();
    }

    @Override
    public TextContent getContent() {
        return text.getContent();
    }

    @Override
    public List<Text> getSiblings() {
        return text.getSiblings();
    }

    @Override
    public OrderedText asOrderedText() {
        return text.asOrderedText();
    }

    public String _getPlayerUUID() {
        return playerUUID;
    }
}
