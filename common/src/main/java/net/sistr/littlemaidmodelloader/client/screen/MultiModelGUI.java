package net.sistr.littlemaidmodelloader.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
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
    private final int width;
    private final int height;
    private final int scale;
    private final MultiModelGUIUtil.DummyModelEntity dummy;
    private final TextureHolder texture;
    private final boolean isContract;
    private int x;
    private int y;
    private TextureColors selectColor = null;
    private boolean selected;

    public MultiModelGUI(TextureHolder texture, boolean isContract, int scale, MultiModelGUIUtil.DummyModelEntity dummy) {
        this.isContract = isContract;
        this.width = scale * 16;
        this.height = scale * 3;
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
        if (selected && selectColor != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            fill(matrices, selectColor.getIndex() * scale, 0,
                    selectColor.getIndex() * scale + scale, height,
                    (0x80 << 24) | selectColor.getColorCode());
            RenderSystem.disableBlend();
        }

        MultiModelGUIUtil.getModel(LMModelManager.INSTANCE, texture).ifPresent(model ->
                renderAllColorModel(matrices, scale, mouseX, mouseY, model, texture, isContract));

    }

    private void renderAllColorModel(MatrixStack matrixStack,
                                     int scale, float mouseX, float mouseY,
                                     IMultiModel model, TextureHolder holder, boolean isContract) {
        for (TextureColors color : TextureColors.values()) {
            MultiModelGUIUtil.getTexturePair(holder, color, isContract).ifPresent(texturePair ->
                    MultiModelGUIUtil.renderModel(
                            this.x + (color.getIndex() + 1) * scale - scale / 2,
                            this.y + height,
                            this.x + mouseX, this.y + mouseY, scale,
                            model, texturePair, dummy
                    )
            );
        }
        TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;
        fontRenderer.draw(matrixStack, holder.getTextureName(),
                0, 0, 0xFFFFFFFF);
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

    @Override
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
