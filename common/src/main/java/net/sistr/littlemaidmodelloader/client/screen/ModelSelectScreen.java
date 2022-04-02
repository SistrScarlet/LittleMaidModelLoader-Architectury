package net.sistr.littlemaidmodelloader.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.network.SyncMultiModelPacket;
import net.sistr.littlemaidmodelloader.resource.holder.TextureHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMModelManager;
import net.sistr.littlemaidmodelloader.resource.manager.LMTextureManager;
import net.sistr.littlemaidmodelloader.resource.util.ArmorPart;
import net.sistr.littlemaidmodelloader.resource.util.ArmorSets;
import net.sistr.littlemaidmodelloader.resource.util.TexturePair;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

//todo isContract
//GUIが小さくならない画面の最低サイズ640x480を想定して組む
//ただし、描画は320x240でやって倍にされるっぽい
@Environment(EnvType.CLIENT)
public class ModelSelectScreen<T extends Entity & IHasMultiModel> extends Screen {
    public static final Identifier EMPTY_TEXTURE =
            new Identifier(LMMLMod.MODID, "textures/empty.png");
    public static final TexturePair EMPTY_TEXTURE_PAIR = new TexturePair(EMPTY_TEXTURE, null);
    public static final ArmorPart EMPTY_ARMOR_DATA =
            new ArmorPart(null, null, null, null,
                    null, null);
    public static final Identifier MODEL_SELECT_GUI_TEXTURE =
            new Identifier(LMMLMod.MODID, "textures/gui/model_select.png");
    private static final ItemStack ARMOR = Items.DIAMOND_CHESTPLATE.getDefaultStack();
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 196;
    private final T entity;
    private final MultiModelGUIUtil.DummyModelEntity dummy;
    private final ArmorSets<ArmorModelGUI> armors = new ArmorSets<>();
    private ScrollBar modelScrollBar;
    private ScrollBar armorScrollBar;
    private ListGUI<MultiModelGUI> modelListGUI;
    private ListGUI<ArmorModelGUI> armorListGUI;
    private boolean guiSwitch = true;

    public ModelSelectScreen(Text titleIn, World world, T entity) {
        super(titleIn);
        this.entity = entity;
        this.dummy = new MultiModelGUIUtil.DummyModelEntity(world);
    }

    @Override
    protected void init() {
        assert this.client != null;
        Collection<TextureHolder> textureHolders =
                LMTextureManager.INSTANCE.getAllTextures();
        Map<String, TextureHolder> map = new HashMap<>();
        textureHolders.forEach(textureHolder -> map.put(textureHolder.getTextureName().toLowerCase(), textureHolder));
        LMModelManager modelManager = LMModelManager.INSTANCE;
        int scale = 15;
        int allColor = 16;
        int heightRatio = 3;
        int heightStack = 4;
        this.modelListGUI = new ListGUI<>(
                (width - scale * allColor) / 2,
                (height - scale * heightRatio * heightStack) / 2,
                1, heightStack, scale * allColor, scale * heightRatio,
                textureHolders.stream()
                        .map(TextureHolder::getTextureName)
                        .map(String::toLowerCase)
                        .sorted(Comparator.naturalOrder())
                        .map(map::get)
                        .filter(textureHolder ->
                                textureHolder.hasSkinTexture(true) &&
                                        modelManager.getModel(textureHolder.getModelName(), IHasMultiModel.Layer.SKIN)
                                                .isPresent())
                        .map(t -> new MultiModelGUI(t, true, scale, this.dummy))
                        .collect(Collectors.toList())
        );
        this.modelScrollBar = new ScrollBar(
                (width + GUI_WIDTH) / 2 + 4, (height - GUI_HEIGHT) / 2,
                8, GUI_HEIGHT, this.modelListGUI.size(),
                new TextureAddress(0, 200, 8, 8, 256, 256),
                new TextureAddress(0, 208, 8, 8, 256, 256),
                new TextureAddress(0, 216, 8, 8, 256, 256),
                new TextureAddress(0, 224, 10, 6, 256, 256),
                MODEL_SELECT_GUI_TEXTURE);
        TextureHolder ownerSkinTex = entity.getTextureHolder(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD);
        int index = 0;
        for (MultiModelGUI g : this.modelListGUI.getAllElements()) {
            if (g.getTexture() == ownerSkinTex) {
                modelScrollBar.setPoint(index);
                modelListGUI.setScroll(index);
            }
            index++;
        }
        this.armorListGUI = new ListGUI<>(
                (width - scale * allColor) / 2,
                (height - scale * heightRatio * heightStack) / 2,
                1, heightStack, scale * allColor, scale * heightRatio,
                textureHolders.stream()
                        .map(TextureHolder::getTextureName)
                        .map(String::toLowerCase)
                        .sorted(Comparator.naturalOrder())
                        .map(map::get)
                        .filter(textureHolder ->
                                textureHolder.hasArmorTexture() &&
                                        modelManager.getModel(textureHolder.getModelName(), IHasMultiModel.Layer.INNER)
                                                .isPresent())
                        .map(t -> new ArmorModelGUI(t, scale, this.dummy, this.armors))
                        .collect(Collectors.toList())
        );
        this.armorScrollBar = new ScrollBar(
                (width + GUI_WIDTH) / 2 + 4, (height - GUI_HEIGHT) / 2,
                8, GUI_HEIGHT, this.armorListGUI.size(),
                new TextureAddress(0, 200, 8, 8, 256, 256),
                new TextureAddress(0, 208, 8, 8, 256, 256),
                new TextureAddress(0, 216, 8, 8, 256, 256),
                new TextureAddress(0, 224, 10, 6, 256, 256),
                MODEL_SELECT_GUI_TEXTURE);
        TextureHolder ownerArmorTex = entity.getTextureHolder(IHasMultiModel.Layer.INNER, IHasMultiModel.Part.HEAD);
        index = 0;
        for (ArmorModelGUI g : this.armorListGUI.getAllElements()) {
            if (g.getTexture() == ownerArmorTex) {
                armorScrollBar.setPoint(index);
                armorListGUI.setScroll(index);
            }
            index++;
        }
    }

    public static void renderColor(MatrixStack matrixStack, int minX, int minY, int maxX, int maxY, int rgba) {
        DrawableHelper.fill(matrixStack, minX, minY, maxX, maxY, rgba);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        assert this.client != null;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MODEL_SELECT_GUI_TEXTURE);
        int relX = (this.width - GUI_WIDTH) / 2;
        int relY = (this.height - GUI_HEIGHT) / 2;
        this.drawTexture(matrixStack, relX, relY, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(ARMOR, relX - 24, relY + GUI_HEIGHT - 16);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MODEL_SELECT_GUI_TEXTURE);
        this.drawTexture(matrixStack, relX - 24, relY + GUI_HEIGHT - 16, 0, 240, 16, 16);

        if (guiSwitch) {
            modelListGUI.render(matrixStack, mouseX, mouseY, partialTicks);
            modelListGUI.getSelectElement()
                    .filter(MultiModelGUI::isSelected)
                    .ifPresent(g -> g.getSelectColor().ifPresent(color -> {
                        TextureHolder texture = g.getTexture();
                        MultiModelGUIUtil.getModel(LMModelManager.INSTANCE, texture).ifPresent(model -> {
                            int scale = 15 * 3;
                            MultiModelGUIUtil.getTexturePair(texture, color, true).ifPresent(texturePair ->
                                    MultiModelGUIUtil.renderModel(
                                            (width + 15 * 16 + scale * 2) / 2,
                                            height - scale,
                                            mouseX, mouseY, scale,
                                            model, texturePair, this.dummy
                                    )
                            );
                        });
                    }));
            modelScrollBar.render(matrixStack, mouseX, mouseY, partialTicks);
        } else {
            armorListGUI.render(matrixStack, mouseX, mouseY, partialTicks);
            this.armors.foreach((p, g) -> {
                TextureHolder texture = g.getTexture();
                MultiModelGUIUtil.getModel(LMModelManager.INSTANCE, texture).ifPresent(model -> {
                    int scale = 15 * 3;
                    LMModelManager modelManager = LMModelManager.INSTANCE;
                    ArmorPart armorData = MultiModelGUIUtil.getArmorDate(modelManager, texture, "default");
                    MultiModelGUIUtil.renderArmorPart((width + 15 * 16 + scale * 2) / 2, height - scale,
                            mouseX, mouseY, scale, model, armorData, p, this.dummy);
                });
            });
            armorScrollBar.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        int minX = (this.width - GUI_WIDTH) / 2 - 24;
        int minY = (this.height - GUI_HEIGHT) / 2 + GUI_HEIGHT - 16;
        if (minX <= x && x < minX + 16 && minY <= y && y < minY + 16) {
            guiSwitch = !guiSwitch;
            playDownSound();
            return true;
        }
        if (guiSwitch) {
            if (modelScrollBar.mouseClicked(x, y, button)) {
                modelListGUI.setScroll(modelScrollBar.getPoint());
                return true;
            } else {
                return modelListGUI.mouseClicked(x, y, button);
            }
        } else {
            if (armorScrollBar.mouseClicked(x, y, button)) {
                armorListGUI.setScroll(armorScrollBar.getPoint());
                return true;
            } else {
                return armorListGUI.mouseClicked(x, y, button);
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (guiSwitch) {
            if (modelScrollBar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                modelListGUI.setScroll(modelScrollBar.getPoint());
                return true;
            }
        } else {
            if (armorScrollBar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                armorListGUI.setScroll(armorScrollBar.getPoint());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (guiSwitch) {
            modelScrollBar.mouseReleased(mouseX, mouseY, button);
            return modelListGUI.mouseReleased(mouseX, mouseY, button);
        } else {
            armorScrollBar.mouseReleased(mouseX, mouseY, button);
            return armorListGUI.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollAmount) {
        if (guiSwitch) {
            if (modelScrollBar.mouseScrolled(x, y, scrollAmount)) {
                modelListGUI.setScroll(modelScrollBar.getPoint());
                return true;
            } else {
                if (modelListGUI.mouseScrolled(x, y, scrollAmount)) {
                    modelScrollBar.setPoint(modelListGUI.getScroll());
                    return true;
                }
                return false;
            }
        } else {
            if (armorScrollBar.mouseScrolled(x, y, scrollAmount)) {
                armorListGUI.setScroll(armorScrollBar.getPoint());
                return true;
            } else {
                if (armorListGUI.mouseScrolled(x, y, scrollAmount)) {
                    armorScrollBar.setPoint(armorListGUI.getScroll());
                    return true;
                }
                return false;
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        modelListGUI.getSelectElement().ifPresent(g ->
                g.getSelectColor().ifPresent(color -> {
                    TextureHolder texture = g.getTexture();
                    //カラーと契約を更新
                    entity.setColor(color);
                    entity.setContract(true);
                    //スキンを更新
                    entity.setTextureHolder(texture, IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD);
                    //防具をスキンと同様に更新
                    for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
                        entity.setTextureHolder(texture, IHasMultiModel.Layer.INNER, part);
                    }
                })
        );

        this.armors.foreach((p, g) ->
                entity.setTextureHolder(g.getTexture(), IHasMultiModel.Layer.INNER, p));

        ArmorSets<String> armorNames = new ArmorSets<>();
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            armorNames.setArmor(entity.getTextureHolder(IHasMultiModel.Layer.INNER, part).getTextureName(), part);
        }
        SyncMultiModelPacket.sendC2SPacket(entity, entity);
    }

    public static void playDownSound() {
        MinecraftClient.getInstance().getSoundManager()
                .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }


}
