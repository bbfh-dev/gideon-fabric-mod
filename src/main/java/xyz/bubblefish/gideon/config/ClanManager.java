package xyz.bubblefish.gideon.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ClanManager {
    public static HashMap<UUID, String> playerMap = new HashMap<>();
    public static HashMap<UUID, String> playerRoleMap = new HashMap<>();
    public static HashMap<String, Clan> clanMap = new HashMap<>();
    public static final HashMap<String, MutableText> nameFormattingCache = new HashMap<>();
    public static final HashMap<String, MutableText> hudFormattingCache = new HashMap<>();


    public static MutableText getFormattedPlayer(String name, String clan, String role, boolean moreContrast) {
        String cacheName = name + "/" + clan;
        MutableText output;

        if (moreContrast) {
            if (nameFormattingCache.get(cacheName) != null) return nameFormattingCache.get(cacheName);

            Clan clanKey = clanMap.getOrDefault(clan, null);
            if (clanKey == null) return new LiteralText(name).formatted(Formatting.GRAY);

            String configClan = ConfigManager.configMap.getOrDefault("clan", "");
            if (configClan.equals("")) {
                assert MinecraftClient.getInstance().player != null;
                configClan = ClanManager.playerMap.get(MinecraftClient.getInstance().player.getUuid());
            }

            if (Objects.equals(configClan, clan)) {
                if (role.equals("S")) output = new LiteralText("\ud83d\udd25 ").formatted(Formatting.YELLOW).append(new LiteralText(name).formatted(Formatting.BOLD));
                else if (role.equals("T")) output = new LiteralText("\ud83d\udd25 ").formatted(Formatting.LIGHT_PURPLE).append(new LiteralText(name).formatted(Formatting.BOLD));
                else output = new LiteralText("\ud83d\udd25 ").formatted(Formatting.AQUA).append(new LiteralText(name).formatted(Formatting.BOLD));
                nameFormattingCache.put(cacheName, output);
                return output;
            }

            if (Objects.equals(ConfigManager.configMap.getOrDefault("group", "1"), String.valueOf(clanKey.group()))) {
                if (role.equals("S")) output = new LiteralText("\u2b50 ").formatted(Formatting.GOLD).append(new LiteralText(name).formatted(Formatting.BOLD));
                else output = new LiteralText("\u2b50 ").formatted(Formatting.GREEN).append(new LiteralText(name).formatted(Formatting.BOLD));
                nameFormattingCache.put(cacheName, output);
                return output;
            }

            if (Math.abs(Integer.parseInt(ConfigManager.configMap.getOrDefault("group", "1")) - clanKey.group()) > 1) {
                output = new LiteralText("\u21c4 ").formatted(Formatting.WHITE).append(new LiteralText(name).formatted(Formatting.BOLD));
                nameFormattingCache.put(cacheName, output);
                return output;
            }

            output = new LiteralText("\u2620 ").formatted(Formatting.DARK_RED).append(new LiteralText(name).formatted(Formatting.BOLD));
            nameFormattingCache.put(cacheName, output);
        } else {
            if (hudFormattingCache.get(cacheName) != null) return hudFormattingCache.get(cacheName);

            Clan clanKey = clanMap.getOrDefault(clan, null);
            if (clanKey == null) return new LiteralText(name);

            String configClan = ConfigManager.configMap.getOrDefault("clan", "");
            if (configClan.equals("")) {
                assert MinecraftClient.getInstance().player != null;
                configClan = ClanManager.playerMap.get(MinecraftClient.getInstance().player.getUuid());
            }

            if (Objects.equals(configClan, clan)) {
                if (role.equals("S")) output = new LiteralText("\ud83d\udd25 ").formatted(Formatting.YELLOW).append(new LiteralText(name).formatted(Formatting.BOLD));
                else if (role.equals("T")) output = new LiteralText("\ud83d\udd25 ").formatted(Formatting.LIGHT_PURPLE).append(new LiteralText(name).formatted(Formatting.BOLD));
                else output = new LiteralText("\ud83d\udd25 ").formatted(Formatting.AQUA).append(new LiteralText(name).formatted(Formatting.BOLD));
                hudFormattingCache.put(cacheName, output);
                return output;
            }

            if (Objects.equals(ConfigManager.configMap.getOrDefault("group", "1"), String.valueOf(clanKey.group()))) {
                if (role.equals("S")) output = new LiteralText("\u2b50 ").formatted(Formatting.GOLD).append(new LiteralText(name).formatted(Formatting.BOLD));
                else output = new LiteralText("\u2b50 ").formatted(Formatting.GREEN).append(new LiteralText(name).formatted(Formatting.BOLD));
                hudFormattingCache.put(cacheName, output);
                return output;
            }

            if (Math.abs(Integer.parseInt(ConfigManager.configMap.getOrDefault("group", "1")) - clanKey.group()) > 1) {
                output = new LiteralText("\u21c4 ").formatted(Formatting.WHITE).append(new LiteralText(name).formatted(Formatting.BOLD));
                hudFormattingCache.put(cacheName, output);
                return output;
            }

            if (role.equals("S")) output = new LiteralText("\u2620 ").formatted(Formatting.DARK_RED).append(new LiteralText(name).formatted(Formatting.BOLD));
            else output = new LiteralText("\u2620 ").formatted(Formatting.RED).append(new LiteralText(name).formatted(Formatting.BOLD));
            hudFormattingCache.put(cacheName, output);
        }
        return output;
    }

    public static void loadFromDatabase() {
        String[] clans = new Request("/fetch/clans", ConfigManager.token).getText().split(", ");
        String[] players = new Request("/fetch/players", ConfigManager.token).getText().split(", ");
        HashMap<UUID, String> newPlayerMap = new HashMap<>();
        HashMap<UUID, String> newPlayerRoleMap = new HashMap<>();
        HashMap<String, Clan> newClanMap = new HashMap<>();

        for (String clanField : clans) {
            String[] values = clanField.split(":");
            newClanMap.put(values[0], new Clan(values[1], Byte.valueOf(values[3])));
        }

        for (String playerField : players) {
            String[] values = playerField.split(":");
            String uuid = new StringBuffer(values[0]).insert(8, "-")
                    .insert(13, "-")
                    .insert(18, "-")
                    .insert(23, "-")
                    .toString();
            newPlayerMap.put(UUID.fromString(uuid), values[4]);
            newPlayerRoleMap.put(UUID.fromString(uuid), values[3]);
        }

        clanMap = newClanMap;
        playerMap = newPlayerMap;
        playerRoleMap = newPlayerRoleMap;
    }
}
