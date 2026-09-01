package com.ro80t.betterui.impl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@UtilityClass
public class ConfigIo {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Loads the config from {@code file}, falling back to defaults if the file
     * is missing or unreadable, then immediately re-saves it so a fresh install
     * gets a populated config file and any newly added fields get their
     * defaults written out.
     */
    public static Config load(final Path file) {
        Config config = null;

        if (Files.isRegularFile(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                config = GSON.fromJson(reader, Config.class);
            } catch (final IOException | JsonParseException e) {
                log.warn("Failed to read {}, falling back to defaults", file, e);
            }
        }

        if (config == null) {
            config = new Config();
        }

        save(config, file);
        return config;
    }

    public static void save(final Config config, final Path file) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (final IOException e) {
            log.warn("Failed to save {}", file, e);
        }
    }
}
