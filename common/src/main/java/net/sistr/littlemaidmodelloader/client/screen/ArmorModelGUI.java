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
    private final int scale;
    private final MultiModelGUIUtil.DummyModelEntity dummy;
    private final TextureHolder texture;
    private final ImmutableList<String> armorNames;
    private final ArmorSets<ArmorModelGUI> armors;
    private boolean selected;

    static {
        ARMOR_ICONS.setArmor(Items.DIAMOND_HELMET.getDefaultStack(), IHasMultiModel.Part.HEAD);
        ARMOR_ICONS.setArmor(Items.DIAMOND_CHESTPLATE.getDefaultStack(), IHasMultiModel.Part.BODY);
        ARMOR_ICONS.setArmor(Items.DIAMOND_LEGGINGS.getDefaultStack(), IHasMultiModel.Part.LEGS);
        ARMOR_ICONS.setArmor(Items.DIAMOND_BOOTS.getDefaultStack(), IHasMultiModel.Part.FEET);
    }

    public ArmorModelGUI(TextureHolder texture, int scale, MultiModelGUIUtil.DummyModelEntity dummy,
                         ArmorSets<ArmorModelGUI> armors) {
        super(scale * 16, scale * 3);
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

        TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;
        ModelSelectScreen.renderColor(matrixStack,
                this.x,
                this.y,
                this.x + this.width,
                this.y + fontRenderer.fontHeight,
                0xFF404040
        );

        int index = 0;
        LMModelManager modelManager = LMModelManager.INSTANCE;
        for (String armorName : armorNames) {
            index++;
            ArmorPart armorData = MultiModelGUIUtil.getArmorDate(modelManager, texture, armorName);
            MultiModelGUIUtil.renderArmor(
                    this.x + index * scale - scale / 2, this.y + height,
                    mouseX, mouseY, scale,
                    model, armorData, dummy);
        }

        fontRenderer.draw(matrixStack, texture.getTextureName(),
                this.x, this.y, 0xFFFFFFFF);

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ARMOR_ICONS.foreach((part, stack) ->
                itemRenderer.renderGuiItemIcon(stack,
                        this.x + this.width - 16 * (part.getIndex() + 1),
                        this.y + fontRenderer.fontHeight
                ));
        armors.foreach((p, g) -> {
            if (g == this) {
                ModelSelectScreen.renderColor(matrixStack,
                        this.x + this.width - 16 * (p.getIndex() + 1),
                        this.y + fontRenderer.fontHeight,
                        this.x + this.width - 16 * (p.getIndex() + 1) + 16,
                        this.y + fontRenderer.fontHeight + 16,
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
                } else {
                    //全選択か全除去
                    boolean selectAll = false;
                    for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
                        //選択していたものが今選択したやつ・・・でなければ全選択
                        if (!armors.getArmor(part).filter(g -> g == this).isPresent()) {
                            selectAll = true;
                            break;
                        }
                    }
                    for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
                        armors.setArmor(selectAll ? this : null, part);
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
}
