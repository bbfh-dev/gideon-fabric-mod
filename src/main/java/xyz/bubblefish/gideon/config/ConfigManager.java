package xyz.bubblefish.gideon.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {
    public static String token = "c9c8b92132512275d5ad9d583c6e2e94";
    public static final HashMap<String, String> configMap = new HashMap<>();


    public static void saveConfig() {
        String value = String.valueOf(ClanManager.clanMap.getOrDefault(ConfigManager.configMap.getOrDefault("clan", ""), new Clan("Empty", (byte) 1)).group());
        if (configMap.get("group") == null) configMap.put("group", value);
        else configMap.replace("group", value);

        List<String> buffer = new ArrayList<>();
        buffer.add("display_health=" + configMap.getOrDefault("display_health", "true"));
        buffer.add("display_clans=" + configMap.getOrDefault("display_clans", "true"));
        buffer.add("toggle_sprint=" + configMap.getOrDefault("toggle_sprint", "true"));
        buffer.add("display_reach=" + configMap.getOrDefault("display_reach", "true"));
        buffer.add("group=" + configMap.getOrDefault("group", "1"));
        buffer.add("clan=" + configMap.getOrDefault("clan", "null"));
        FileManager.writeToConfig("config.txt", buffer);
    }

    public static void loadConfig() {
        List<String> tokens = FileManager.readFile(FileManager.getConfigPath("token.txt"));
        if (!tokens.isEmpty()) token = tokens.get(0);

        List<String> config = FileManager.readFile(FileManager.getConfigPath("config.txt"));
        if (!config.isEmpty()) {
            for (String property : config) {
                String[] values = property.split("=");
                if (values.length == 2) {
                    configMap.put(values[0], values[1]);
                }
            }
        }
    }
}
