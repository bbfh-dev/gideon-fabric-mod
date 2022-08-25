package xyz.bubblefish.gideon.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.bubblefish.gideon.config.ClanManager;
import xyz.bubblefish.gideon.config.ConfigManager;

import java.util.Objects;
import java.util.UUID;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    @Shadow
    protected abstract Text applyGameModeFormatting(PlayerListEntry entry, MutableText name);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
    private Text getDisplayName(PlayerListHud playerListHud, PlayerListEntry entry) {
        if (Objects.equals(ConfigManager.configMap.getOrDefault("display_clans", "true"), "true")) {
            String clan = ClanManager.playerMap.getOrDefault(entry.getProfile().getId(), "null");
            if (!Objects.equals(clan, "null")) {
                return this.applyGameModeFormatting(entry, ClanManager.getFormattedPlayer(entry.getProfile().getName(), clan, ClanManager.playerRoleMap.getOrDefault(entry.getProfile().getId(), "M"), false));
            } else {
                return this.applyGameModeFormatting(entry, new LiteralText(entry.getProfile().getName()).formatted(Formatting.GRAY));
            }
        }
        return entry.getDisplayName() != null ? this.applyGameModeFormatting(entry, (MutableText) entry.getDisplayName()) : this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));
    }

    @ModifyVariable(method = "render", at = @At(value = "STORE"), ordinal = 7)
    private int modifyN(int n) {
        return n + MinecraftClient.getInstance().textRenderer.getWidth("999ms");
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(Lnet/minecraft/client/util/math/MatrixStack;IIILnet/minecraft/client/network/PlayerListEntry;)V"))
    private void renderLatencyIcon(PlayerListHud instance, MatrixStack matrices, int width, int x, int y, PlayerListEntry entry) {
        MinecraftClient client = MinecraftClient.getInstance();
        int latency = entry.getLatency();
        int color = 0x66ff88; // Green
        if(latency > 300) {
            color = 0xff5252; // Red
        } else if (latency > 80) {
            color = 0xffba52; // Orange
        }
        String strLatency = latency + "ms";
        int strOffset = client.textRenderer.getWidth(strLatency);
        client.textRenderer.drawWithShadow(matrices, strLatency, x + width - strOffset, y, color);
    }
}
