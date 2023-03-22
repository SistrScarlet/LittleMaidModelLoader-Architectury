package net.sistr.littlemaidmodelloader.client.screen;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.maidmodel.EntityCaps;
import net.sistr.littlemaidmodelloader.maidmodel.IModelCaps;
import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMPose;
import net.sistr.littlemaidmodelloader.resource.holder.TextureHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMModelManager;
import net.sistr.littlemaidmodelloader.resource.util.ArmorPart;
import net.sistr.littlemaidmodelloader.resource.util.ArmorSets;
import net.sistr.littlemaidmodelloader.resource.util.TextureColors;
import net.sistr.littlemaidmodelloader.resource.util.TexturePair;
import net.sistr.littlemaidmodelloader.setup.Registration;

import java.util.Optional;

public class MultiModelGUIUtil {

    public static Optional<IMultiModel> getModel(LMModelManager modelManager, TextureHolder texture) {
        if (modelManager.getModel(texture.getModelName(), IHasMultiModel.Layer.SKIN).isEmpty()) {
            return Optional.empty();
        }
        return modelManager.getModel(texture.getModelName(), IHasMultiModel.Layer.SKIN);
    }

    public static Optional<TexturePair> getTexturePair(TextureHolder holder, TextureColors color, boolean isContract) {
        Optional<Identifier> optional = holder.getTexture(color, isContract, false);
        return optional.map(resourceLocation ->
                new TexturePair(resourceLocation,
                        holder.getTexture(color, isContract, true).orElse(null)));
    }

    public static void renderModel(MatrixStack matrixStack, int posX, int posY, float mouseX, float mouseY, int scale,
                                   IMultiModel model, TexturePair texturePair, DummyModelEntity dummy) {
        dummy.setSkinModel(model);
        dummy.setSkinTexture(texturePair);
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            dummy.setArmorVisible(false, part);
            dummy.setArmorData(ModelSelectScreen.EMPTY_ARMOR_DATA, part);
        }
        dummy.setAllArmorVisible(false);
        renderEntity(matrixStack, posX, posY, mouseX, mouseY, scale, model, dummy);
    }

    public static ArmorPart getArmorDate(LMModelManager modelManager, TextureHolder texture, String armorName) {
        IMultiModel innerModel = modelManager.getModel(texture.getModelName(), IHasMultiModel.Layer.INNER)
                .orElseThrow(() -> new IllegalStateException("モデルが存在しません"));
        IMultiModel outerModel = modelManager.getModel(texture.getModelName(), IHasMultiModel.Layer.OUTER)
                .orElseThrow(() -> new IllegalStateException("モデルが存在しません"));
        Identifier innerTex = texture.getArmorTexture(IHasMultiModel.Layer.INNER, armorName,
                0, false).orElse(null);
        Identifier innerLightTex = texture.getArmorTexture(IHasMultiModel.Layer.INNER, armorName,
                0, true).orElse(null);
        Identifier outerTex = texture.getArmorTexture(IHasMultiModel.Layer.OUTER, armorName,
                0, false).orElse(null);
        Identifier outerLightTex = texture.getArmorTexture(IHasMultiModel.Layer.OUTER, armorName,
                0, true).orElse(null);
        return new ArmorPart(
                innerTex, innerLightTex,
                outerTex, outerLightTex,
                innerModel, outerModel
        );
    }

    public static void renderArmor(MatrixStack matrixStack, int posX, int posY, float mouseX, float mouseY, int scale,
                                   IMultiModel model, ArmorPart data, DummyModelEntity dummy) {
        dummy.setSkinModel(model);
        dummy.setSkinTexture(ModelSelectScreen.EMPTY_TEXTURE_PAIR);
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            dummy.setArmorVisible(true, part);
            dummy.setArmorData(data, part);
        }
        renderEntity(matrixStack, posX, posY, mouseX, mouseY, scale, model, dummy);
    }

    public static void renderArmorPart(MatrixStack matrixStack, int posX, int posY, float mouseX, float mouseY, int scale,
                                       IMultiModel model, ArmorPart data, IHasMultiModel.Part armorPart, DummyModelEntity dummy) {
        dummy.setSkinModel(model);
        dummy.setSkinTexture(ModelSelectScreen.EMPTY_TEXTURE_PAIR);
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            dummy.setArmorVisible(false, part);
            dummy.setArmorData(ModelSelectScreen.EMPTY_ARMOR_DATA, part);
        }
        dummy.setArmorVisible(true, armorPart);
        dummy.setArmorData(data, armorPart);
        renderEntity(matrixStack, posX, posY, mouseX, mouseY, scale, model, dummy);
    }

    public static void renderEntity(MatrixStack matrixStack, int posX, int posY, float mouseX, float mouseY, int scale,
                                    IMultiModel model, DummyModelEntity dummy) {
        InventoryScreen.drawEntity(matrixStack,
                posX, posY, scale,
                posX - mouseX,
                posY - mouseY - model.getEyeHeight(dummy.getCaps(), MMPose.STANDING) * scale,
                dummy
        );
    }

    public static class DummyModelEntity extends LivingEntity implements IHasMultiModel {
        private final EntityCaps caps = new EntityCaps(this);
        private IMultiModel skinModel;
        private TexturePair skinTexture;
        private final ArmorSets<ArmorPart> armorsData = new ArmorSets<>();
        private final ArmorSets<Boolean> armorsVisible = new ArmorSets<>();

        public DummyModelEntity(World worldIn) {
            this(Registration.DUMMY_MODEL_ENTITY.get(), worldIn);
        }

        public DummyModelEntity(EntityType<DummyModelEntity> type, World worldIn) {
            super(type, worldIn);
        }

        public void setSkinModel(IMultiModel model) {
            skinModel = model;
        }

        public void setSkinTexture(TexturePair skinTexture) {
            this.skinTexture = skinTexture;
        }

        public void setArmorData(ArmorPart data, Part part) {
            armorsData.setArmor(data, part);
        }

        public void setArmorVisible(boolean visible, Part part) {
            this.armorsVisible.setArmor(visible, part);
        }

        public void setAllArmorVisible(boolean visible) {
            for (Part part : Part.values()) {
                this.armorsVisible.setArmor(visible, part);
            }
        }

        @Override
        public Iterable<ItemStack> getArmorItems() {
            return Lists.newArrayList(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        }

        @Override
        public ItemStack getEquippedStack(EquipmentSlot slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void equipStack(EquipmentSlot slot, ItemStack stack) {

        }

        @Override
        public Arm getMainArm() {
            return Arm.RIGHT;
        }

        @Deprecated
        @Override
        public void setTextureHolder(TextureHolder textureHolder, Layer layer, Part part) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public TextureHolder getTextureHolder(Layer layer, Part part) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void setColorMM(TextureColors color) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public TextureColors getColorMM() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void setContractMM(boolean isContract) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean isContractMM() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<IMultiModel> getModel(Layer layer, Part part) {
            if (layer == Layer.SKIN) {
                return Optional.ofNullable(skinModel);
            } else {
                return armorsData.getArmor(part)
                        .map(armorPart -> armorPart.getModel(layer));
            }
        }

        @Environment(EnvType.CLIENT)
        @Override
        public Optional<Identifier> getTexture(Layer layer, Part part, boolean isLight) {
            if (layer == Layer.SKIN) {
                return Optional.ofNullable(skinTexture.getTexture(isLight));
            } else {
                return armorsData.getArmor(part)
                        .map(armorPart -> armorPart.getTexture(layer, isLight));
            }
        }


        @Override
        public IModelCaps getCaps() {
            return caps;
        }

        @Environment(EnvType.CLIENT)
        @Override
        public boolean isArmorVisible(Part part) {
            return armorsVisible.getArmor(part).orElse(false);
        }

        @Override
        public boolean isArmorGlint(Part part) {
            return false;
        }

        @Deprecated
        @Override
        public boolean isAllowChangeTexture(Entity changer, TextureHolder textureHolder, Layer layer, Part part) {
            throw new UnsupportedOperationException();
        }

    }
}
