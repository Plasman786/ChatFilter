import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatListener implements Listener {

    private final ChatFilterPlugin plugin;
    private final ChatFilter filter;
    private final File logFile;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ChatListener(ChatFilterPlugin plugin, ChatFilter filter) {
        this.plugin = plugin;
        this.filter = filter;

        // Initialize log file in plugin data folder
        this.logFile = new File(plugin.getDataFolder(), "blocked_messages.log");
        if (!logFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs(); // make data folder if needed
                logFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create blocked_messages.log file!");
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        ChatFilter.Result result = filter.filter(msg);

        if (result.blocked) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYour message contained inappropriate language and was blocked.");
            event.getPlayer().sendMessage("§7Filtered message: §f" + result.filtered);

            // Log to file
            logBlockedMessage(event.getPlayer().getUniqueId().toString(), msg);
        }
    }

    private void logBlockedMessage(String playerId, String originalMessage) {
        String timestamp = LocalDateTime.now().format(dtf);
        String line = String.format("[%s] %s: %s%n", timestamp, playerId, originalMessage);

        // Append to file
        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(line);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to log blocked message!");
            e.printStackTrace();
        }
    }
}
