package com.ro80t.betterui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import com.ro80t.betterui.api.IBetterUiMod;
import com.ro80t.betterui.impl.config.Config;
import com.ro80t.betterui.impl.config.ConfigIo;

import java.nio.file.Path;

@Slf4j
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
     * One-shot mod bootstrap shared by every loader entrypoint: records the
     * mod instance, loads the config from {@code configDir}, and logs the
     * same "initializing.../initialized" pair every loader used to repeat
     * individually.
     */
    public static void initialize(final IBetterUiMod modInstance, final Path configDir) {
        setInstance(modInstance);

        log.info(MOD_NAME + " initializing...");
        loadConfig(configDir);
        log.info(MOD_NAME + " initialized");
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
