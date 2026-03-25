package dev.lolihub.hideplayer.core;

import dev.lolihub.hideplayer.HidePlayer;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;

public class PlayerCapability {
    static class HideFrom {
        boolean systemMessage = false;

        boolean inGame = false;  // tab list, social, target selector, chat, and rendering.

        boolean statusAndQuery = false;
        boolean scoreBoard = false;
        boolean locatorBar = false;
    }

    static class Privilege {
        boolean canSeeHiddenPlayer = false;
    }

    HideFrom hideFrom = new HideFrom();
    Privilege privilege = new Privilege();
    ServerPlayer player;  // Store player instead of uuid, as the player object is needed to check permissions

    public PlayerCapability(ServerPlayer player) {
        this.player = player;
        this.flush();
    }

    public boolean canSeeHiddenPlayer() {
        return this.privilege.canSeeHiddenPlayer;
    }

    public boolean hideSystemMessage() {
        return this.hideFrom.systemMessage;
    }

    public boolean showInGame() {
        return !this.hideFrom.inGame;
    }

    public boolean showInGame(ServerPlayer player) {
        return this.showInGame()
                || player.getStringUUID().equals(this.player.getStringUUID())
                || HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer();
    }

    public boolean showStatusAndQuery() {
        return !this.hideFrom.statusAndQuery;
    }

    public boolean showInLocatorBar() {
        return !this.hideFrom.locatorBar;
    }

    public boolean showInLocatorBar(ServerPlayer player) {
        return this.showInLocatorBar()
                || player.getStringUUID().equals(this.player.getStringUUID())
                || HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer();
    }

    public void flush() {
        if (this.player != null) {
            this.hideFrom.systemMessage = Permissions.check(this.player, "hideplayer.hide.systemmessage");
            this.hideFrom.inGame = Permissions.check(this.player, "hideplayer.hide.ingame");
            this.hideFrom.statusAndQuery = Permissions.check(this.player, "hideplayer.hide.statusandquery");
            this.hideFrom.scoreBoard = Permissions.check(this.player, "hideplayer.hide.scoreboard");
            this.hideFrom.locatorBar = Permissions.check(this.player, "hideplayer.hide.locatorbar");
            this.privilege.canSeeHiddenPlayer = Permissions.check(this.player, "hideplayer.privilege.seehiddenplayer", PermissionLevel.GAMEMASTERS);
        }
        if (this.hideFrom.scoreBoard) {
            HidePlayer.getVisibilityManager().getScoreBoardCache().add(this.player.getGameProfile().name());
        } else {
            HidePlayer.getVisibilityManager().getScoreBoardCache().remove(this.player.getGameProfile().name());
        }
    }
}
