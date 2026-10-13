
package com.draxeofsnem;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatListener implements Listener {

    private final ChatFilterPlugin plugin;
    private final ChatFilter filter;
    private final File logFile;

    private final DateTimeFormatter dtf =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ChatListener(ChatFilterPlugin plugin, ChatFilter filter) {
        this.plugin = plugin;
        this.filter = filter;

        this.logFile = new File(
                plugin.getDataFolder(),
                "blocked_messages.log"
        );

        if (!logFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                logFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe(
                        "Failed to create blocked_messages.log file!"
                );
                plugin.getLogger().severe(e.getMessage());
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        String msg = PlainTextComponentSerializer.plainText()
                .serialize(event.message());

        ChatFilter.Result result = filter.filter(msg);

        if (result.blocked) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(
                    Component.text(
                            "Your message contained inappropriate language and was blocked."
                    )
            );

            event.getPlayer().sendMessage(
                    Component.text("Filtered message: " + result.filtered)
            );

            logBlockedMessage(
                    event.getPlayer().getUniqueId().toString(),
                    msg
            );
        }
    }

    private synchronized void logBlockedMessage(
            String playerId,
            String originalMessage
    ) {
        String timestamp = LocalDateTime.now().format(dtf);

        String line = String.format(
                "[%s] %s: %s%n",
                timestamp,
                playerId,
                originalMessage
        );

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(line);
        } catch (IOException e) {
            plugin.getLogger().severe(
                    "Failed to log blocked message: " + e.getMessage()
            );
        }
    }
}
