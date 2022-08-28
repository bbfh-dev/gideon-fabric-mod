package xyz.bubblefish.gideon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.bubblefish.gideon.config.ClanManager;
import xyz.bubblefish.gideon.config.ConfigManager;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static xyz.bubblefish.gideon.config.ClanManager.hudFormattingCache;
import static xyz.bubblefish.gideon.config.ClanManager.nameFormattingCache;

public class GideonClient implements ClientModInitializer {
    public static float playerDealtReach = 0f;
    public static final Logger LOGGER = LoggerFactory.getLogger("GideonIntegration");
    private static int tick = 0;
    public static KeyBinding killKeyBinding;
    public static KeyBinding lobbyKeyBinding;
    public static KeyBinding toggleClansKeyBinding;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Gideon...");

        killKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Run /kill",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "Gideon Integration"
        ));

        lobbyKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Run /lobby",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "Gideon Integration"
        ));

        toggleClansKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle clans display",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_ENTER,
                "Gideon Integration"
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (killKeyBinding.wasPressed()) {
                assert client.player != null;
                client.player.sendChatMessage("/kill");
            }

            if (lobbyKeyBinding.wasPressed()) {
                assert client.player != null;
                client.player.sendChatMessage("/lobby");
            }

            if (toggleClansKeyBinding.wasPressed()) {
                if (Objects.equals(ConfigManager.configMap.get("display_clans"), "true")) ConfigManager.configMap.replace("display_clans", "false");
                else ConfigManager.configMap.replace("display_clans", "true");
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tick += 1;
            if (tick > 6000) {  // Every 5 minutes
                CompletableFuture.runAsync(() -> {
                    nameFormattingCache.clear();
                    hudFormattingCache.clear();
                    ClanManager.loadFromDatabase();
                });
                tick = 0;
            }
        });

        CompletableFuture.runAsync(() -> {
            ConfigManager.loadConfig();
            ConfigManager.saveConfig();
            ConfigManager.loadConfig();
            ClanManager.loadFromDatabase();
            new GideonCommandManager(ClientCommandManager.DISPATCHER);
            LOGGER.info("Pulled in {} clans & {} players.", ClanManager.clanMap.size(), ClanManager.playerMap.size());
        });
    }
}
