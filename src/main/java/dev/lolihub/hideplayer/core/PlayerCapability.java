package dev.lolihub.hideplayer.core;

import dev.lolihub.hideplayer.HidePlayer;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerCapability {
    static class HideFrom {
        boolean systemMessage = false;
        boolean chatMessage = false;
        boolean tabList = false;
        boolean scoreBoard = false;
        boolean visual = false;
        boolean targetSelector = false;
    }

    static class Privilege {
        boolean canSeeHiddenPlayer = false;
    }

    HideFrom hideFrom = new HideFrom();
    Privilege privilege = new Privilege();
    ServerPlayerEntity player = null;  // Store player instead of uuid, as the player object is needed to check permissions

    public PlayerCapability() {
    }

    public PlayerCapability(ServerPlayerEntity player) {
        this.player = player;
        this.flush();
    }

    public boolean canSeeHiddenPlayer() {
        return this.privilege.canSeeHiddenPlayer;
    }

    public boolean showSystemMessage() {
        return !this.hideFrom.systemMessage;
    }

    public boolean showSystemMessage(ServerPlayerEntity sender) {
        return HidePlayer.getVisibilityManager().getPlayerCapability(sender).canSeeHiddenPlayer() || this.showSystemMessage();
    }

    public boolean showChatMessage() {
        return !this.hideFrom.chatMessage;
    }

    public boolean showChatMessage(ServerPlayerEntity sender) {
        return HidePlayer.getVisibilityManager().getPlayerCapability(sender).canSeeHiddenPlayer() || this.showChatMessage();
    }

    public boolean showTabList() {
        return !this.hideFrom.tabList;
    }

    public boolean showTabList(ServerPlayerEntity player) {
        return HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer() || this.showTabList();
    }

    public boolean showScoreBoard() {
        return !this.hideFrom.scoreBoard;
    }

    public boolean showVisual() {
        return !this.hideFrom.visual;
    }

    public boolean showVisual(ServerPlayerEntity player) {
        return HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer() || this.showVisual();
    }

    public boolean showTargetSelector() {
        return !this.hideFrom.targetSelector;
    }

    public boolean showTargetSelector(ServerPlayerEntity player) {
        return HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer() || this.showTargetSelector();
    }

    public void flush() {
        if (this.player != null) {
            this.hideFrom.systemMessage = Permissions.check(this.player, "hideplayer.hidesystemmessage");
            this.hideFrom.chatMessage = Permissions.check(this.player, "hideplayer.hidechatmessage");
            this.hideFrom.tabList = Permissions.check(this.player, "hideplayer.hidetablist");
            this.hideFrom.scoreBoard = Permissions.check(this.player, "hideplayer.hidescoreboard");
            this.hideFrom.visual = Permissions.check(this.player, "hideplayer.hidevisual");
            this.hideFrom.targetSelector = Permissions.check(this.player, "hideplayer.hidetargetselector");
            this.privilege.canSeeHiddenPlayer = Permissions.check(this.player, "hideplayer.canseehiddenplayer", 2);
        }
    }
}
