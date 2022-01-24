package net.sistr.littlemaidmodelloader.client.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;
import net.sistr.littlemaidmodelloader.resource.holder.TextureHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMModelManager;
import net.sistr.littlemaidmodelloader.resource.util.ArmorPart;
import net.sistr.littlemaidmodelloader.resource.util.ArmorSets;
import org.lwjgl.glfw.GLFW;

public class ArmorModelGUI extends GUIElement implements ListGUIElement {
    private static final ArmorSets<ItemStack> ARMOR_ICONS = new ArmorSets<>();
    private final MarginedClickable selectBox = new MarginedClickable(4);
    private final int width;
    private final int height;
    private final int scale;
    private final MultiModelGUIUtil.DummyModelEntity dummy;
    private final TextureHolder texture;
    private final ImmutableList<String> armorNames;
    private final ArmorSets<ArmorModelGUI> armors;
    private int x;
    private int y;
    private boolean selected;

    static {
        ARMOR_ICONS.setArmor(Items.DIAMOND_HELMET.getDefaultStack(), IHasMultiModel.Part.HEAD);
        ARMOR_ICONS.setArmor(Items.DIAMOND_CHESTPLATE.getDefaultStack(), IHasMultiModel.Part.BODY);
        ARMOR_ICONS.setArmor(Items.DIAMOND_LEGGINGS.getDefaultStack(), IHasMultiModel.Part.LEGS);
        ARMOR_ICONS.setArmor(Items.DIAMOND_BOOTS.getDefaultStack(), IHasMultiModel.Part.FEET);
    }

    public ArmorModelGUI(TextureHolder texture, int scale, MultiModelGUIUtil.DummyModelEntity dummy,
                         ArmorSets<ArmorModelGUI> armors) {
        this.width = scale * 16;
        this.height = scale * 3;
        this.scale = scale;
        this.dummy = dummy;
        this.texture = texture;
        this.armorNames = ImmutableList.copyOf(texture.getArmorNames());
        this.armors = armors;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MultiModelGUIUtil.getModel(LMModelManager.INSTANCE, texture).ifPresent(model ->
                renderAllArmorModel(matrices, scale, mouseX, mouseY, model, texture, dummy));
    }

    public void renderAllArmorModel(MatrixStack matrixStack, int scale, float mouseX, float mouseY,
                                    IMultiModel model, TextureHolder texture, MultiModelGUIUtil.DummyModelEntity dummy) {
        /*if (this.clicked && this.selected) {
            renderColor(matrixStack, 0, 0, this.width, this.height, 0x80FFFFFF);
        }*/

        int index = 0;
        LMModelManager modelManager = LMModelManager.INSTANCE;
        for (String armorName : armorNames) {
            index++;
            ArmorPart armorData = MultiModelGUIUtil.getArmorDate(modelManager, texture, armorName);
            MultiModelGUIUtil.renderArmor(
                    this.x + index * scale - scale / 2, this.y + height,
                    this.x + mouseX, this.y + mouseY, scale,
                    model, armorData, dummy);
        }
        TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;
        fontRenderer.draw(matrixStack, texture.getTextureName(),
                0, 0, 0xFFFFFFFF);

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ARMOR_ICONS.foreach((part, stack) ->
                itemRenderer.renderGuiItemIcon(stack,
                        this.x + this.width - 16 * (part.getIndex() + 1),
                        this.y + fontRenderer.fontHeight
                ));
        armors.foreach((p, g) -> {
            if (g == this) {
                ModelSelectScreen.renderColor(matrixStack,
                        this.width - 16 * (p.getIndex() + 1),
                        fontRenderer.fontHeight,
                        this.width - 16 * (p.getIndex() + 1) + 16,
                        fontRenderer.fontHeight + 16,
                        0x80FFFFFF
                );
            }
        });
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
                TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;
                if (this.width - 16 * 4 <= mouseX && mouseX < this.width
                        && fontRenderer.fontHeight <= mouseY && mouseY < fontRenderer.fontHeight + 16) {
                    int index = 3 - MathHelper.floor((mouseX - (this.width - 16 * 4)) / 16);
                    IHasMultiModel.Part part = IHasMultiModel.Part.getPart(index);
                    if (armors.getArmor(part).filter(g -> g == this).isPresent()) {
                        armors.setArmor(null, part);
                    } else {
                        armors.setArmor(this, part);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public TextureHolder getTexture() {
        return this.texture;
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
