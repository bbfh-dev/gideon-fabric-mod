package xyz.bubblefish.gideon.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import xyz.bubblefish.gideon.config.ClanManager;
import xyz.bubblefish.gideon.config.ConfigManager;

import java.util.Objects;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private CheckboxWidget displayHealthCheckbox;
    private CheckboxWidget toggleSprintCheckbox;
    private CheckboxWidget displayReachCheckbox;

    public ConfigScreen(Screen parent) {
        super(new LiteralText("Gideon Config"));
        this.parent = parent;
    }

    protected void init() {
        this.displayHealthCheckbox = new CheckboxWidget(8, 48, 20, 20, new LiteralText("Display player health"), Objects.equals(ConfigManager.configMap.get("display_health"), "true"));
        this.toggleSprintCheckbox = new CheckboxWidget(8, 72, 20, 20, new LiteralText("Toggle sprint (Always)"), Objects.equals(ConfigManager.configMap.get("toggle_sprint"), "true"));
        this.displayReachCheckbox = new CheckboxWidget(8, 96, 20, 20, new LiteralText("Display reach"), Objects.equals(ConfigManager.configMap.get("display_reach"), "true"));

        this.addDrawableChild(new ButtonWidget(8, 8, 80, 20, new LiteralText("Cancel"), (button) -> {
            assert this.client != null;
            this.client.setScreen(this.parent);
        }));

        this.addDrawableChild(new ButtonWidget(92, 8, 60, 20, new LiteralText("Save"), (button) -> {
            ConfigManager.configMap.replace("display_health", (this.displayHealthCheckbox.isChecked()) ? "true" : "false");
            ConfigManager.configMap.replace("toggle_sprint", (this.toggleSprintCheckbox.isChecked()) ? "true" : "false");
            ConfigManager.configMap.replace("display_reach", (this.displayReachCheckbox.isChecked()) ? "true" : "false");
            ConfigManager.saveConfig();
            assert this.client != null;
            this.client.setScreen(this.parent);
        }));

        this.addDrawableChild(new ButtonWidget(this.width - 154, this.height - 24, 150, 20, new LiteralText("Reload Players & Clans"), (button) -> {
            ClanManager.loadFromDatabase();
            assert this.client != null;
            this.client.setScreen(this.parent);
        }));

        this.addDrawableChild(this.displayHealthCheckbox);
        this.addDrawableChild(this.toggleSprintCheckbox);
        this.addDrawableChild(this.displayReachCheckbox);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 40, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
