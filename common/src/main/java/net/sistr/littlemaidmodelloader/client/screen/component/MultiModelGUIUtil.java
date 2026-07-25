package net.sistr.littlemaidmodelloader.client.screen.component;

import com.google.common.collect.Lists;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sistr.littlemaidmodelloader.client.screen.ModelSelectScreen;
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

public class MultiModelGUIUtil {

    public static Optional<IMultiModel> getModel(
            LMModelManager modelManager, TextureHolder texture) {
        if (modelManager.getModel(texture.getModelName(), IHasMultiModel.Layer.SKIN).isEmpty()) {
            return Optional.empty();
        }
        return modelManager.getModel(texture.getModelName(), IHasMultiModel.Layer.SKIN);
    }

    public static Optional<TexturePair> getTexturePair(
            TextureHolder holder, TextureColors color, boolean isContract) {
        Optional<Identifier> optional = holder.getTexture(color, isContract, false);
        return optional.map(
                resourceLocation ->
                        new TexturePair(
                                resourceLocation,
                                holder.getTexture(color, isContract, true).orElse(null)));
    }

    public static void renderModel(
            DrawContext context,
            int posX,
            int posY,
            float mouseX,
            float mouseY,
            int scale,
            IMultiModel model,
            TexturePair texturePair,
            DummyModelEntity dummy) {
        dummy.setSkinModel(model);
        dummy.setSkinTexture(texturePair);
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            dummy.setArmorVisible(false, part);
            dummy.setArmorData(ModelSelectScreen.EMPTY_ARMOR_DATA, part);
        }
        dummy.setAllArmorVisible(false);
        renderEntity(context, posX, posY, mouseX, mouseY, scale, model, dummy);
    }

    public static ArmorPart getArmorDate(
            LMModelManager modelManager, TextureHolder texture, String armorName) {
        IMultiModel innerModel =
                modelManager
                        .getModel(texture.getModelName(), IHasMultiModel.Layer.INNER)
                        .orElseThrow(() -> new IllegalStateException("モデルが存在しません"));
        IMultiModel outerModel =
                modelManager
                        .getModel(texture.getModelName(), IHasMultiModel.Layer.OUTER)
                        .orElseThrow(() -> new IllegalStateException("モデルが存在しません"));
        Identifier innerTex =
                texture.getArmorTexture(IHasMultiModel.Layer.INNER, armorName, 0, false)
                        .orElse(null);
        Identifier innerLightTex =
                texture.getArmorTexture(IHasMultiModel.Layer.INNER, armorName, 0, true)
                        .orElse(null);
        Identifier outerTex =
                texture.getArmorTexture(IHasMultiModel.Layer.OUTER, armorName, 0, false)
                        .orElse(null);
        Identifier outerLightTex =
                texture.getArmorTexture(IHasMultiModel.Layer.OUTER, armorName, 0, true)
                        .orElse(null);
        return new ArmorPart(
                innerTex, innerLightTex,
                outerTex, outerLightTex,
                innerModel, outerModel);
    }

    public static void renderArmor(
            DrawContext context,
            int posX,
            int posY,
            float mouseX,
            float mouseY,
            int scale,
            IMultiModel model,
            ArmorPart data,
            DummyModelEntity dummy) {
        dummy.setSkinModel(model);
        dummy.setSkinTexture(ModelSelectScreen.EMPTY_TEXTURE_PAIR);
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            dummy.setArmorVisible(true, part);
            dummy.setArmorData(data, part);
        }
        renderEntity(context, posX, posY, mouseX, mouseY, scale, model, dummy);
    }

    public static void renderArmorPart(
            DrawContext context,
            int posX,
            int posY,
            float mouseX,
            float mouseY,
            int scale,
            IMultiModel model,
            ArmorPart data,
            IHasMultiModel.Part armorPart,
            DummyModelEntity dummy) {
        dummy.setSkinModel(model);
        dummy.setSkinTexture(ModelSelectScreen.EMPTY_TEXTURE_PAIR);
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            dummy.setArmorVisible(false, part);
            dummy.setArmorData(ModelSelectScreen.EMPTY_ARMOR_DATA, part);
        }
        dummy.setArmorVisible(true, armorPart);
        dummy.setArmorData(data, armorPart);
        renderEntity(context, posX, posY, mouseX, mouseY, scale, model, dummy);
    }

    public static void renderEntity(
            DrawContext context,
            int posX,
            int posY,
            float mouseX,
            float mouseY,
            int scale,
            IMultiModel model,
            DummyModelEntity dummy) {
        // 1.21: drawEntity は矩形指定 + 自動センタリング型に変更
        // - 矩形 (x1, y1, x2, y2) の中心にエンティティを描画する
        // - 矩形外は内部で enableScissor によりクリップされるため、矩形高は
        //   model pack が描画する最大ビジュアル高 (hitbox の 1.35 ではなく
        //   モデルパック側で頭部が上に伸びるケース) を吸収できる必要がある
        // - rect 高を 3 * scale 確保し、頭が伸びる model でもクリップしないようにする
        // - エンティティは矩形中心 (centerY = posY - 1.5*scale) に置かれるが、
        //   yOffset で entity origin (足元) を下方向に押し下げ、見かけ上の足元が
        //   posY 付近に来るよう調整する:
        //     feet_screen_y = centerY + (entity_height/2 + yOffset * entity.getScale()) * size
        //                   = (posY - 1.5*scale) + (entity_height/2 + 1.0) * scale
        //   dummy の hitbox はモデル実寸に追従するようになったため entity_height は
        //   モデル依存 (標準メイドさんの 1.35 なら posY + 0.175*scale)。背の高いモデルほど
        //   足元が下がるので、モデル間で足元を揃えたい場合はここで補正する必要がある
        // - mouseX/mouseY は絶対スクリーン座標 (vanilla 内部で centerX - mouseX_param を計算)
        int halfSize = scale / 2;
        InventoryScreen.drawEntity(
                context,
                posX - halfSize,
                posY - scale * 3,
                posX + halfSize,
                posY,
                scale,
                1.0f,
                mouseX,
                mouseY,
                dummy);
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
            // 描画のたびに呼ばれるため、モデルが変わったときだけ hitbox を組み直す
            if (skinModel == model) return;
            skinModel = model;
            calculateDimensions();
        }

        // drawEntity はエンティティの dimensions を基準に自動センタリングするため、
        // GUI プレビューでもモデル実寸を返さないと背の高いモデルで位置がずれる。
        // MultiModelEntity と同じく getBaseDimensions が 1.21 での拡張点。
        // 乗客や子どもスケールは GUI では発生しないので PASSENGER attachment と
        // getScaleFactor() の適用は省く。
        @Override
        protected EntityDimensions getBaseDimensions(EntityPose pose) {
            IMultiModel model = skinModel;
            if (model == null) return super.getBaseDimensions(pose);
            MMPose mmPose = MMPose.convertPose(pose);
            return EntityDimensions.changing(
                            model.getWidth(caps, mmPose), model.getHeight(caps, mmPose))
                    .withEyeHeight(model.getEyeHeight(caps, mmPose));
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
            return Lists.newArrayList(
                    ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        }

        @Override
        public ItemStack getEquippedStack(EquipmentSlot slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void equipStack(EquipmentSlot slot, ItemStack stack) {}

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
                return armorsData.getArmor(part).map(armorPart -> armorPart.getModel(layer));
            }
        }

        @Environment(EnvType.CLIENT)
        @Override
        public Optional<Identifier> getTexture(Layer layer, Part part, boolean isLight) {
            if (layer == Layer.SKIN) {
                if (skinTexture == null) {
                    return Optional.empty();
                }
                return Optional.ofNullable(skinTexture.getTexture(isLight));
            } else {
                return armorsData
                        .getArmor(part)
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
        public boolean isAllowChangeTexture(
                Entity changer, TextureHolder textureHolder, Layer layer, Part part) {
            throw new UnsupportedOperationException();
        }
    }
}
