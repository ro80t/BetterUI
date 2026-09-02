package com.ro80t.betterui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import com.ro80t.betterui.api.IBetterUiMod;
import com.ro80t.betterui.impl.config.Config;
import com.ro80t.betterui.impl.config.ConfigIo;

import java.nio.file.Path;

@UtilityClass
public class BetterUiMod {
    public static final String MOD_NAME;
    public static final String MOD_ID;

    @Getter
    private static final BuildData BUILD_DATA;
    @Getter
    @Setter
    private static IBetterUiMod instance;
    @Getter
    private static Config config = new Config();
    private static Path configFile;

    static {
        BUILD_DATA = BuildData.getInstance();
        MOD_NAME = BUILD_DATA.getModName();
        MOD_ID = BUILD_DATA.getModId();
    }

    /**
     * Loads (and, on first run, creates) {@code <configDir>/betterui.json}.
     * Every loader entrypoint calls this once during its own init with its
     * own notion of "the config directory".
     */
    public static void loadConfig(final Path configDir) {
        configFile = configDir.resolve(MOD_ID + ".json");
        config = ConfigIo.load(configFile);
    }

    /**
     * Flips {@code durabilityHudEnabled} and immediately persists it, so
     * in-game toggles (e.g. the pause menu button) survive a restart.
     */
    public static void toggleDurabilityHud() {
        config.setDurabilityHudEnabled(!config.isDurabilityHudEnabled());
        ConfigIo.save(config, configFile);
    }
}
