package dev.lolihub.hideplayer.utils;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

import java.util.List;

public class HiddenPlayerText implements Text {
    private final Text text;

    public HiddenPlayerText(Text text) {
        this.text = text;
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
}
