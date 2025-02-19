package dev.lolihub.hideplayer.core;

import dev.lolihub.hideplayer.HidePlayer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ScoreBoardCache {
    private static final File cacheFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), HidePlayer.MOD_ID + "_cache.json");

    private final Set<String> scoreBoardHiddenCache = new HashSet<>();

    public ScoreBoardCache() {
        if (cacheFile.exists() && cacheFile.isFile() && cacheFile.canRead() && cacheFile.canWrite()) {
            try (var reader = new Scanner(cacheFile)) {
                while (reader.hasNextLine()) {
                    var next = reader.nextLine();
                    if (next.isBlank()) {
                        continue;
                    }
                    scoreBoardHiddenCache.add(next);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);  // Should not happen
            }
        }
    }


    public boolean checkNoHide(String playerName) {
        return !scoreBoardHiddenCache.contains(playerName);
    }

    public void add(String playerName) {
        scoreBoardHiddenCache.add(playerName);
    }

    public void remove(String playerName) {
        scoreBoardHiddenCache.remove(playerName);
    }

    public void save() {
        try (var writer = new java.io.FileWriter(cacheFile)) {
            for (var playerName : scoreBoardHiddenCache) {
                writer.write(playerName);
                writer.write('\n');
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
