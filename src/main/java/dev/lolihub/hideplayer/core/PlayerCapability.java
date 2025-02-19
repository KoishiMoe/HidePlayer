package dev.lolihub.hideplayer.core;

import dev.lolihub.hideplayer.HidePlayer;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerCapability {
    static class HideFrom {
        boolean systemMessage = false;

        boolean inGame = false;  // tab list, social, target selector, chat, and rendering.

        boolean statusAndQuery = false;
        boolean scoreBoard = false;
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

    public boolean showSystemMessage(ServerPlayerEntity player) {
        return player.getUuidAsString().equals(this.player.getUuidAsString())
                ||  HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer()
                || this.showSystemMessage();
    }

    public boolean showInGame() {
        return !this.hideFrom.inGame;
    }

    public boolean showInGame(ServerPlayerEntity player) {
        return player.getUuidAsString().equals(this.player.getUuidAsString())
                || HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer()
                || this.showInGame();
    }

    public boolean showStatusAndQuery() {
        return !this.hideFrom.statusAndQuery;
    }

    public boolean showScoreBoard() {
        return !this.hideFrom.scoreBoard;
    }

    public boolean showScoreBoard(ServerPlayerEntity player) {
        return player.getUuidAsString().equals(this.player.getUuidAsString())
                || HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer()
                || this.showScoreBoard();
    }

    public void flush() {
        if (this.player != null) {
            this.hideFrom.systemMessage = Permissions.check(this.player, "hideplayer.hide.systemmessage");
            this.hideFrom.inGame = Permissions.check(this.player, "hideplayer.hide.ingame");
            this.hideFrom.statusAndQuery = Permissions.check(this.player, "hideplayer.hide.statusandquery");
            this.hideFrom.scoreBoard = Permissions.check(this.player, "hideplayer.hide.scoreboard");
            this.privilege.canSeeHiddenPlayer = Permissions.check(this.player, "hideplayer.privilege.seehiddenplayer", 2);
        }
        if (this.hideFrom.scoreBoard) {
            HidePlayer.getVisibilityManager().getScoreBoardCache().add(this.player.getGameProfile().getName());
        } else {
            HidePlayer.getVisibilityManager().getScoreBoardCache().remove(this.player.getGameProfile().getName());
        }
    }
}
