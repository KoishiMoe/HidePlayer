package dev.lolihub.hideplayer.core;

import dev.lolihub.hideplayer.HidePlayer;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerCapability {
    static class HideFrom {
        boolean systemMessage = false;

        boolean inGame = false;  // tab list, social, target selector, chat, and rendering.
        // Client will log `Server attempted to add player prior to sending player info (Player id: <uuid>)`. Will leak player's UUID.

        boolean playerListing = false;
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

    public boolean showPlayerListing() {
        return !this.hideFrom.playerListing;
    }

    public boolean showPlayerListing(ServerPlayerEntity player) {
        return player.getUuidAsString().equals(this.player.getUuidAsString())
                || HidePlayer.getVisibilityManager().getPlayerCapability(player).canSeeHiddenPlayer()
                || this.showPlayerListing();
    }

    public boolean showScoreBoard() {
        return !this.hideFrom.scoreBoard;
    }

    public void flush() {
        if (this.player != null) {
            this.hideFrom.systemMessage = Permissions.check(this.player, "hideplayer.hide.systemmessage");
            this.hideFrom.inGame = Permissions.check(this.player, "hideplayer.hide.ingame");
            this.hideFrom.playerListing = Permissions.check(this.player, "hideplayer.hide.playerlisting");
            this.hideFrom.scoreBoard = Permissions.check(this.player, "hideplayer.hide.scoreboard");
            this.privilege.canSeeHiddenPlayer = Permissions.check(this.player, "hideplayer.privilege.seehiddenplayer", 2);
        }
    }
}
