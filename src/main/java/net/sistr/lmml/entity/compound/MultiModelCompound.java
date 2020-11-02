package net.sistr.lmml.entity.compound;

import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.sistr.lmml.LittleMaidModelLoader;
import net.sistr.lmml.maidmodel.EntityCaps;
import net.sistr.lmml.maidmodel.IModelCaps;
import net.sistr.lmml.maidmodel.ModelMultiBase;
import net.sistr.lmml.resource.util.ArmorPart;
import net.sistr.lmml.resource.util.ArmorSets;
import net.sistr.lmml.resource.holder.TextureHolder;
import net.sistr.lmml.resource.util.TexturePair;
import net.sistr.lmml.resource.manager.LMModelManager;
import net.sistr.lmml.resource.util.TextureColors;

import java.util.Optional;

//モデル/テクスチャの管理クラス
//テクスチャとモデルの保持方法を旧管理クラスの時の配列から変更した
public class MultiModelCompound implements IHasMultiModel {

    private final Entity entity;
    private final IModelCaps caps;

    private final TextureHolder defaultMainPackage;
    private final TextureHolder defaultArmorPackage;

    private TextureHolder skinTexHolder;
    private ModelMultiBase skinModel;
    private TexturePair skinTexture;

    private final ArmorSets<TextureHolder> armorsTexHolder = new ArmorSets<>();
    private final ArmorSets<ArmorPart> armorsData = new ArmorSets<>();

    private TextureColors color;
    private boolean isContract;

    public MultiModelCompound(Entity entity, TextureHolder defaultMainPackage, TextureHolder defaultArmorPackage) {
        this.entity = entity;
        this.caps = new EntityCaps(entity);
        this.defaultMainPackage = defaultMainPackage;
        this.defaultArmorPackage = defaultArmorPackage;
        this.color = TextureColors.BROWN;
        update();
    }

    public void update() {
        updateMain();
        updateArmor();
    }

    public void updateMain() {
        if (skinTexHolder == null) {
            skinTexHolder = defaultMainPackage;
        }
        LMModelManager modelManager = LMModelManager.INSTANCE;
        skinModel = modelManager.getModel(skinTexHolder.getModelName(), Layer.SKIN)
                .orElseThrow(() -> new IllegalStateException("指定されたモデルが不正です。"));
        skinTexture = new TexturePair(skinTexHolder.getTexture(color, isContract, false).orElse(null),
                skinTexHolder.getTexture(color, isContract, true).orElse(null));
    }

    public void updateArmor() {
        int index = 0;
        for (ItemStack stack : entity.getArmorItems()) {
            if (4 < index) {
                break;
            }
            updateArmorPart(Part.getPart(index++), getName(stack.getItem()), getDamagePercent(stack));
        }
    }

    private String getName(Item item) {
        //クライアント限定
        if (entity.world.isClient && item instanceof ArmorItem) {
            return ((ArmorItem) item).getMaterial().getName().toLowerCase();
        }
        Identifier location = Registry.ITEM.getId(item);
        return location.toString();
    }

    private float getDamagePercent(ItemStack stack) {
        float damagePercent = 0F;
        if (stack.isDamageable() && 0 < stack.getMaxDamage()) {
            damagePercent = (float) stack.getDamage() / (float) stack.getMaxDamage();
        }
        return damagePercent;
    }

    private void updateArmorPart(Part part, String armorName, float damagePercent) {
        TextureHolder textureHolder = armorsTexHolder.getArmor(part).orElse(defaultArmorPackage);
        armorsTexHolder.setArmor(textureHolder, part);
        LMModelManager manager = LMModelManager.INSTANCE;
        ArmorPart.Builder dataBuilder = ArmorPart.Builder.newInstance();
        manager.getModel(textureHolder.getModelName(), Layer.INNER);
        dataBuilder.innerModel(manager.getModel(textureHolder.getModelName(), Layer.INNER)
                .orElseThrow(() -> new IllegalStateException("指定されたモデルが不正です。")));
        dataBuilder.outerModel(manager.getModel(textureHolder.getModelName(), Layer.OUTER)
                .orElseThrow(() -> new IllegalStateException("指定されたモデルが不正です。")));
        dataBuilder.innerTex(textureHolder.getArmorTexture(Layer.INNER, armorName,
                damagePercent, false).orElse(null));
        dataBuilder.innerTexLight(textureHolder.getArmorTexture(Layer.INNER, armorName,
                damagePercent, true).orElse(null));
        dataBuilder.outerTex(textureHolder.getArmorTexture(Layer.OUTER, armorName,
                damagePercent, false).orElse(null));
        dataBuilder.outerTexLight(textureHolder.getArmorTexture(Layer.OUTER, armorName,
                damagePercent, true).orElse(null));
        armorsData.setArmor(dataBuilder.build(), part);
    }

    @Override
    public void setTextureHolder(TextureHolder textureHolder, Layer layer, Part part) {
        if (layer == Layer.SKIN) {
            skinTexHolder = textureHolder;
            updateMain();
        } else {
            armorsTexHolder.setArmor(textureHolder, part);
            int index = 0;
            for (ItemStack stack : entity.getArmorItems()) {
                if (part.getIndex() == index++) {
                    updateArmorPart(part, getName(stack.getItem()), getDamagePercent(stack));
                }
            }
        }
    }

    @Override
    public TextureHolder getTextureHolder(Layer layer, Part part) {
        if (layer == Layer.SKIN) {
            return skinTexHolder;
        } else {
            return armorsTexHolder.getArmor(part)
                    .orElseThrow(() -> new IllegalStateException("防具テクスチャホルダーが存在しません。"));
        }
    }

    @Override
    public Optional<ModelMultiBase> getModel(Layer layer, Part part) {
        if (layer == Layer.SKIN) {
            return Optional.ofNullable(skinModel);
        } else {
            ModelMultiBase model = armorsData.getArmor(part)
                    .orElseThrow(() -> new IllegalStateException("防具データが存在しません"))
                    .getModel(layer);
            return Optional.ofNullable(model);
        }
    }

    @Override
    public Optional<Identifier> getTexture(Layer layer, Part part, boolean isLight) {
        if (layer == Layer.SKIN) {
            return Optional.ofNullable(skinTexture.getTexture(isLight));
        } else {
            Identifier resourceLocation = armorsData.getArmor(part)
                    .orElseThrow(() -> new IllegalStateException("防具データが存在しません"))
                    .getTexture(layer, isLight);
            return Optional.ofNullable(resourceLocation);
        }
    }

    @Override
    public IModelCaps getCaps() {
        return this.caps;
    }

    //ちと処理重いか？
    @Override
    public boolean isArmorVisible(Part part) {
        int index = 0;
        for (ItemStack stack : entity.getArmorItems()) {
            if (part.getIndex() == index++ && !stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isArmorGlint(Part part) {
        int index = 0;
        for (ItemStack stack : entity.getArmorItems()) {
            if (part.getIndex() == index++ && !stack.isEmpty()) {
                return stack.hasEnchantments();
            }
        }
        return false;
    }

    @Override
    public boolean isAllowChangeTexture(Entity changer, TextureHolder textureHolder, Layer layer, Part part) {
        return true;
    }

    public void setColor(TextureColors color) {
        this.color = color;
        updateMain();
    }

    public TextureColors getColor() {
        return color;
    }

    public void setContract(boolean contract) {
        this.isContract = contract;
        updateMain();
    }

    @Override
    public boolean isContract() {
        return isContract;
    }

}
