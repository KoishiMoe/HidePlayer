package dev.lolihub.hideplayer.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class MixinConfig {
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("hideplayer.mixin.conf");
    private static Properties config = null;

    public static Properties getConfig() {
        if (config == null) {
            config = new Properties();
            if (CONFIG_FILE.toFile().exists()) {
                try (var is = new FileInputStream(CONFIG_FILE.toFile())) {
                    config.load(is);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try (var os = new FileOutputStream(CONFIG_FILE.toFile())) {
                    String defaultConfig = DefaultConfig.DEFAULT_CONFIG;
                    os.write(defaultConfig.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return config;
    }
}
