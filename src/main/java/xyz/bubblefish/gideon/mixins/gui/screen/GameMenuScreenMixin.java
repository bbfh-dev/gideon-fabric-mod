package xyz.bubblefish.gideon.mixins.gui.screen;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.bubblefish.gideon.config.ConfigManager;
import xyz.bubblefish.gideon.gui.screen.ConfigScreen;

import java.util.Objects;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void addWidgets(CallbackInfo ci) {
        this.addDrawableChild(new TexturedButtonWidget(this.width - 24, 4, 20, 20, 0, 0, 20, new Identifier("gideon:textures/gui/widgets.png"), 256, 256, (button) -> {
            if (Objects.equals(ConfigManager.configMap.get("display_clans"), "true")) ConfigManager.configMap.replace("display_clans", "false");
            else ConfigManager.configMap.replace("display_clans", "true");
            assert this.client != null;
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }));

        this.addDrawableChild(new TexturedButtonWidget(this.width - 48, 4, 20, 20, 20, 0, 20, new Identifier("gideon:textures/gui/widgets.png"), 256, 256, (button) -> {
            assert this.client != null;
            this.client.setScreen(new ConfigScreen(this));
        }));
    }
}
