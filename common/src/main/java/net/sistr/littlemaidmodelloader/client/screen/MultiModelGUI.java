package net.sistr.littlemaidmodelloader.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;
import net.sistr.littlemaidmodelloader.resource.holder.TextureHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMModelManager;
import net.sistr.littlemaidmodelloader.resource.util.TextureColors;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class MultiModelGUI extends GUIElement implements ListGUIElement {
    private final MarginedClickable selectBox = new MarginedClickable(4);
    private final int scale;
    private final MultiModelGUIUtil.DummyModelEntity dummy;
    private final TextureHolder texture;
    private final boolean isContract;
    private TextureColors selectColor = null;
    private boolean selected;

    public MultiModelGUI(TextureHolder texture, boolean isContract, int scale, MultiModelGUIUtil.DummyModelEntity dummy) {
        super(scale * 16, scale * 3);
        this.isContract = isContract;
        this.scale = scale;
        this.dummy = dummy;
        this.texture = texture;
    }

    public TextureHolder getTexture() {
        return this.texture;
    }

    public Optional<TextureColors> getSelectColor() {
        return Optional.ofNullable(this.selectColor);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        var fontRenderer = MinecraftClient.getInstance().textRenderer;
        ModelSelectScreen.renderColor(matrices,
                this.x,
                this.y,
                this.x + this.width,
                this.y + fontRenderer.fontHeight,
                0xFF404040
        );
        if (selected && selectColor != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            fill(matrices, this.x + selectColor.getIndex() * scale, this.y,
                    this.x + selectColor.getIndex() * scale + scale, this.y + height,
                    (0x80 << 24) | selectColor.getColorCode());
            RenderSystem.disableBlend();
        }

        MultiModelGUIUtil.getModel(LMModelManager.INSTANCE, texture).ifPresent(model ->
                renderAllColorModel(matrices, scale, mouseX, mouseY, model, texture, isContract));

        fontRenderer.draw(matrices, texture.getTextureName(),
                this.x, this.y, 0xFFFFFFFF);

    }

    private void renderAllColorModel(MatrixStack matrixStack,
                                     int scale, float mouseX, float mouseY,
                                     IMultiModel model, TextureHolder holder, boolean isContract) {
        for (TextureColors color : TextureColors.values()) {
            MultiModelGUIUtil.getTexturePair(holder, color, isContract).ifPresent(texturePair ->
                    MultiModelGUIUtil.renderModel(matrixStack,
                            this.x + (color.getIndex() + 1) * scale - scale / 2,
                            this.y + height,
                            mouseX, mouseY, scale,
                            model, texturePair, dummy
                    )
            );
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            selectBox.click(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (selectBox.release(mouseX, mouseY)) {
                //モデル選択
                this.selectColor = TextureColors.getColor(MathHelper.floor(mouseX / scale));
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }
}
