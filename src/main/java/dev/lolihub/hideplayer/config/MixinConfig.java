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
                    String defaultConfig = """
                            # HidePlayer Mixin Configuration
                            # This file is used to control which mixins are loaded by HidePlayer.
                            # The key is the fully qualified class name of the mixin, and the value is a boolean indicating whether the mixin should be loaded.
                            # If the key is not present in this file, the mixin will **still** be loaded.
                            # Please note that this file is not reloaded at runtime, you need to restart the server to apply changes.
                            # Also, this file is for advanced users only, who would like to solve compatibility issues with other mods, or tune the performance of HidePlayer.
                            # Make sure you know what are the mixins used for by reading the source code of HidePlayer.
                            
                            # PLACEHOLDER #
                            
                            """; // generate this at build time
                    os.write(defaultConfig.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return config;
    }
}
