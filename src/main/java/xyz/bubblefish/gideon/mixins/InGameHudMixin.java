package xyz.bubblefish.gideon.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.bubblefish.gideon.config.ConfigManager;

import java.util.Objects;

import static xyz.bubblefish.gideon.GideonClient.playerDealtReach;
import static net.minecraft.client.gui.DrawableHelper.fill;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
    @Shadow @Final private MinecraftClient client;

    @Shadow private int scaledHeight;

    @Shadow private int scaledWidth;

    @Shadow @Final private static Identifier VIGNETTE_TEXTURE;

    @Inject(method = "render", at = @At(value = "TAIL", target = "Lnet/minecraft/client/gui/hud/InGameHud;InGameHud()Z"))
    public void InGameHud(MatrixStack matrix, float _timeDelta, CallbackInfo _info) {
        MinecraftClient instance = MinecraftClient.getInstance();
        if (!instance.options.debugEnabled && Objects.equals(ConfigManager.configMap.getOrDefault("display_reach", "true"), "true")) {
            String reach = (String.valueOf(playerDealtReach).length() == 3) ? playerDealtReach + "0" : String.valueOf(playerDealtReach);
            LiteralText text = (LiteralText) new LiteralText("\ud83d\udde1 " + reach).formatted(Formatting.WHITE);
            int width = instance.textRenderer.getWidth(text);
            int x = instance.getWindow().getScaledWidth() / 2 - 6 * instance.options.guiScale;
            int y = instance.getWindow().getScaledHeight() / 2 + 4 * instance.options.guiScale;

            fill(
                    matrix,
                    x - 2,
                    y - 2,
                    x + width + 2,
                    y + 10,
                    0x55000000
            );

            instance.textRenderer.drawWithShadow(
                    matrix,
                    text,
                    (float) x,
                    (float) y,
                    0xffffffff
            );
        }
    }

    @Inject(method = "render", at = @At(value = "FIELD", ordinal = 0,
            target = "Lnet/minecraft/client/MinecraftClient;interactionManager:Lnet/minecraft/client/network/ClientPlayerInteractionManager;"))
    private void renderTint(MatrixStack matrices, float tickDelta, CallbackInfo ci)
    {
        ClientPlayerEntity player = this.client.player;
        if (player != null && this.client.interactionManager != null)
        {
            if (isGameModeWithHearts(this.client.interactionManager.getCurrentGameMode()))
                this.renderDamageTint(player);
        }
    }

    private boolean isGameModeWithHearts(GameMode mode)
    {
        return mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE;
    }

    private void renderDamageTint(ClientPlayerEntity player) {
        float health = player.getHealth();
        float threshold = 8f;
        if (health <= threshold) {
            float f = (threshold - health) / threshold + 1.0F / threshold * 2.0F;
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            RenderSystem.setShaderColor(0.1F, f, f, 1.0F);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(0.0D, this.scaledHeight, -90.0D).texture(0.0F, 1.0F).next();
            bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0D).texture(1.0F, 1.0F).next();
            bufferBuilder.vertex(this.scaledWidth, 0.0D, -90.0D).texture(1.0F, 0.0F).next();
            bufferBuilder.vertex(0.0D, 0.0D, -90.0D).texture(0.0F, 0.0F).next();
            tessellator.draw();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.defaultBlendFunc();
        }
    }
}