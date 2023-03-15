package net.sistr.littlemaidmodelloader.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.SoundPlayable;
import net.sistr.littlemaidmodelloader.network.SyncSoundPackPacket;
import net.sistr.littlemaidmodelloader.resource.holder.ConfigHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMConfigManager;

import java.util.stream.Collectors;

public class SoundPackSelectScreen<T extends Entity & SoundPlayable> extends Screen {
    public static final Identifier MODEL_SELECT_GUI_TEXTURE =
            new Identifier(LMMLMod.MODID, "textures/gui/model_select.png");
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 196;
    private final T entity;
    private ListGUI<SoundPackGUI> soundPackListGUI;

    public SoundPackSelectScreen(Text titleIn, T owner) {
        super(titleIn);
        this.entity = owner;
    }

    @Override
    protected void init() {
        int scale = 15;
        int allColor = 16;
        int heightRatio = 3;
        int heightStack = 4;
        this.soundPackListGUI = new ListGUI<>((width - scale * allColor) / 2,
                (height - scale * heightRatio * heightStack) / 2,
                1, scale * heightRatio * heightStack / ((this.textRenderer.fontHeight + 1) * 3),
                scale * allColor, (this.textRenderer.fontHeight + 1) * 3,
                LMConfigManager.INSTANCE.getAllConfig().stream()
                        .map(c -> new SoundPackGUI(scale * allColor, (this.textRenderer.fontHeight + 1) * 3,
                                this.textRenderer, c))
                        .collect(Collectors.toList()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        assert this.client != null;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MODEL_SELECT_GUI_TEXTURE);
        int relX = (this.width - GUI_WIDTH) / 2;
        int relY = (this.height - GUI_HEIGHT) / 2;
        drawTexture(matrices, relX, relY, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        super.render(matrices, mouseX, mouseY, delta);
        this.soundPackListGUI.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        return this.soundPackListGUI.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.soundPackListGUI.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.soundPackListGUI.mouseScrolled(mouseX, mouseY, amount)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void close() {
        super.close();
        soundPackListGUI.getSelectElement()
                .ifPresent(gui -> SyncSoundPackPacket.sendC2SPacket(this.entity, gui.getConfigHolder()));
    }

    public static class SoundPackGUI extends GUIElement implements ListGUIElement {
        private final TextRenderer textRenderer;
        private final ConfigHolder configHolder;
        private boolean selected;

        protected SoundPackGUI(int width, int height, TextRenderer textRenderer, ConfigHolder configHolder) {
            super(width, height);
            this.textRenderer = textRenderer;
            this.configHolder = configHolder;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            textRenderer.draw(matrices, configHolder.getPackName(),
                    this.x, this.y + 1, 0xffffff);
            textRenderer.draw(matrices, configHolder.getParentName(),
                    this.x, this.y + 1 + (textRenderer.fontHeight + 1), 0xffffff);
            textRenderer.draw(matrices, configHolder.getFileName(),
                    this.x, this.y + 1 + (textRenderer.fontHeight + 1) * 2, 0xffffff);
            fill(matrices, this.x, this.y + this.height - 1,
                    this.x + this.width, this.y + this.height, 0xffffffff);
            if (this.selected) {
                fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, 0x80ffffff);
            }
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public boolean isSelected() {
            return this.selected;
        }

        public ConfigHolder getConfigHolder() {
            return configHolder;
        }
    }
}
