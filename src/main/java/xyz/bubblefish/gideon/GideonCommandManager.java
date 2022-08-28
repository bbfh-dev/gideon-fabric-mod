package xyz.bubblefish.gideon;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.LiteralText;
import xyz.bubblefish.gideon.config.ClanManager;
import xyz.bubblefish.gideon.config.ConfigManager;
import xyz.bubblefish.gideon.config.Request;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;
import static xyz.bubblefish.gideon.config.ClanManager.*;
import static xyz.bubblefish.gideon.config.ConfigManager.token;

public class GideonCommandManager {
    public GideonCommandManager(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = literal("gideon");
        LiteralArgumentBuilder<FabricClientCommandSource> argumentBuilder = literal("clan");
        LiteralArgumentBuilder<FabricClientCommandSource> clanArgumentBuilder = literal("set");
        for (String clanId : ClanManager.clanMap.keySet()) {
            argumentBuilder.then(literal(ClanManager.clanMap.get(clanId).name()).executes(context -> {
                ConfigManager.configMap.replace("clan", clanId);
                ConfigManager.saveConfig();
                nameFormattingCache.clear();
                hudFormattingCache.clear();
                context.getSource().sendFeedback(new LiteralText("Changed clan to " + ClanManager.clanMap.get(clanId).name()));
                return 1;
            }));
            clanArgumentBuilder.then(literal(ClanManager.clanMap.get(clanId).name())
                    .then(literal("Staff").executes(context -> setPlayerClan(context, clanId, "S")))
                    .then(literal("Recruiter").executes(context -> setPlayerClan(context, clanId, "R")))
                    .then(literal("Member").executes(context -> setPlayerClan(context, clanId, "M")))
                    .then(literal("Rookie").executes(context -> setPlayerClan(context, clanId, "T")))
            );
        }
        dispatcher.register(command.then(argumentBuilder).then(literal("roster").then(argument("Player name", EntityArgumentType.player()).then(clanArgumentBuilder)
                .then(literal("remove").executes(context -> {
                    String playerName = context.getInput().split(" ")[2];
                    UUID uuid = getPlayerId(playerName);
                    if (uuid == null) {
                        context.getSource().sendError(new LiteralText("Could not find the player!"));
                        return 0;
                    }

                    playerMap.remove(uuid);
                    nameFormattingCache.clear();
                    hudFormattingCache.clear();
                    CompletableFuture.runAsync(() -> new Request("/player/" + uuid.toString().replace("-", "") + "/reset", token).getText());
                    context.getSource().sendFeedback(new LiteralText("Player was removed!"));
                    return 1;
                }))
        )));
    }

    private int setPlayerClan(CommandContext<FabricClientCommandSource> context, String clanId, String role) {
        String playerName = context.getInput().split(" ")[2];
        UUID uuid = getPlayerId(playerName);
        if (uuid == null) {
            context.getSource().sendError(new LiteralText("Could not find the player!"));
            return 0;
        }

        if (playerMap.get(uuid) == null) playerMap.put(uuid, clanId);
        else playerMap.replace(uuid, clanId);

        nameFormattingCache.clear();
        hudFormattingCache.clear();
        CompletableFuture.runAsync(() -> new Request("/player/" + uuid.toString().replace("-", "") + "/claim/" + playerName + "/" + clanId + "/" + role, token).getText());
        context.getSource().sendFeedback(new LiteralText("Player was set!"));
        return 1;
    }

    public static UUID getPlayerId(String name) {
        for (PlayerListEntry entry : Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getPlayerList()) {
            if (entry.getProfile().getName().equals(name)) return entry.getProfile().getId();
        }

        return null;
    }
}
