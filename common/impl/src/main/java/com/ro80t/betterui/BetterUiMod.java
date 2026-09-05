package com.ro80t.betterui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import com.ro80t.betterui.api.IBetterUiMod;
import com.ro80t.betterui.impl.config.Config;
import com.ro80t.betterui.impl.config.ConfigIo;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

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
     * One on/off setting on the BetterUI settings screen: a human-readable
     * label plus the getter/setter pair backing it in {@link Config}. Loader
     * UI code builds one button per entry instead of hand-wiring each toggle.
     */
    public record ToggleSetting(String label, BooleanSupplier getter, Consumer<Boolean> setter) {
        public boolean isEnabled() {
            return getter.getAsBoolean();
        }

        public void toggle() {
            setter.accept(!isEnabled());
        }
    }

    /**
     * Every setting the BetterUI settings screen should show a toggle for.
     * Add a new entry here (backed by a new {@link Config} field) and every
     * loader's settings screen picks it up automatically.
     */
    public static List<ToggleSetting> toggleSettings() {
        return List.of(
                new ToggleSetting("Durability Item",
                        () -> config.isDurabilityShowEnabled(),
                        value -> betterui$setAndSave(config::setDurabilityShowEnabled, value)),
                new ToggleSetting("Durability Armor",
                        () -> config.isDurabilityHudEnabled(),
                        value -> betterui$setAndSave(config::setDurabilityHudEnabled, value))
        );
    }

    private static void betterui$setAndSave(final Consumer<Boolean> fieldSetter, final boolean value) {
        fieldSetter.accept(value);
        ConfigIo.save(config, configFile);
    }
}
