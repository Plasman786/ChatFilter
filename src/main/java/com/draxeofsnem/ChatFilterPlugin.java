package com.draxeofsnem;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main plugin class for the chat filter.
 * Loads banned words and provides the filter to ChatListener.
 */
public class ChatFilterPlugin extends JavaPlugin {

    private ChatFilter chatFilter;

    @Override
    public void onEnable() {
        getLogger().info("ChatFilter enabling...");

        // Load banned words from banned.txt
        List<String> bannedWords;
        try {
            bannedWords = Files.readAllLines(Paths.get("src/main/java/com/draxeofsnem/banned.txt"));
        } catch (IOException e) {
            getLogger().severe("Failed to read banned.txt!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Loaded " + bannedWords.size() + " banned words.");

        // Create ChatFilter instance
        chatFilter = new ChatFilter(bannedWords);

        // Register chat listener with the filter
        getServer().getPluginManager().registerEvents(new ChatListener(this, chatFilter), this);

        getLogger().info("ChatFilter enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatFilter disabled.");
    }
}
