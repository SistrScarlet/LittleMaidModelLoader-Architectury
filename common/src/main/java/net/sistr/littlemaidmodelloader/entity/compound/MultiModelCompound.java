package net.sistr.littlemaidmodelloader.entity.compound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.sistr.littlemaidmodelloader.maidmodel.EntityCaps;
import net.sistr.littlemaidmodelloader.maidmodel.IModelCaps;
import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;
import net.sistr.littlemaidmodelloader.resource.holder.TextureHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMModelManager;
import net.sistr.littlemaidmodelloader.resource.manager.LMTextureManager;
import net.sistr.littlemaidmodelloader.resource.util.ArmorPart;
import net.sistr.littlemaidmodelloader.resource.util.ArmorSets;
import net.sistr.littlemaidmodelloader.resource.util.TextureColors;
import net.sistr.littlemaidmodelloader.resource.util.TexturePair;

import java.util.Optional;

/**
 * モデル/テクスチャの管理クラス
 */
public class MultiModelCompound implements IHasMultiModel {

    private final Entity entity;
    private final IModelCaps caps;

    private final TextureHolder defaultMainPackage;
    private final TextureHolder defaultArmorPackage;

    private TextureHolder skinTexHolder;
    private IMultiModel skinModel;
    private TexturePair skinTexture;

    private final ArmorSets<TextureHolder> armorsTexHolder = new ArmorSets<>();
    private final ArmorSets<ArmorPart> armorsData = new ArmorSets<>();

    private TextureColors color;
    private boolean isContract;

    public MultiModelCompound(LivingEntity entity, TextureHolder defaultMainPackage, TextureHolder defaultArmorPackage) {
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
        skinModel = modelManager.getOrDefaultModel(skinTexHolder.getModelName(), Layer.SKIN);
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
        dataBuilder.innerModel(manager.getOrDefaultModel(textureHolder.getModelName(), Layer.INNER));
        dataBuilder.outerModel(manager.getOrDefaultModel(textureHolder.getModelName(), Layer.OUTER));
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
    public Optional<IMultiModel> getModel(Layer layer, Part part) {
        if (layer == Layer.SKIN) {
            return Optional.ofNullable(skinModel);
        } else {
            IMultiModel model = armorsData.getArmor(part)
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

    public void setColorMM(TextureColors color) {
        this.color = color;
        updateMain();
    }

    public TextureColors getColorMM() {
        return color;
    }

    public void setContractMM(boolean contract) {
        this.isContract = contract;
        updateMain();
    }

    @Override
    public boolean isContractMM() {
        return isContract;
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putByte("SkinColor", (byte) getColorMM().getIndex());
        nbt.putBoolean("IsContract", isContractMM());
        nbt.putString("SkinTexture", getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
        for (Part part : Part.values()) {
            nbt.putString("ArmorTextureInner" + part.getPartName(),
                    getTextureHolder(Layer.INNER, part).getTextureName());
            nbt.putString("ArmorTextureOuter" + part.getPartName(),
                    getTextureHolder(Layer.OUTER, part).getTextureName());
        }
    }

    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("SkinColor")) {
            setColorMM(TextureColors.getColor(nbt.getByte("SkinColor")));
        }
        setContractMM(nbt.getBoolean("IsContract"));
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
        if (nbt.contains("SkinTexture")) {
            textureManager.getTexture(nbt.getString("SkinTexture"))
                    .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.SKIN, Part.HEAD));
        }
        for (Part part : Part.values()) {
            String inner = "ArmorTextureInner" + part.getPartName();
            String outer = "ArmorTextureOuter" + part.getPartName();
            if (nbt.contains(inner)) {
                textureManager.getTexture(nbt.getString(inner))
                        .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.INNER, part));
            }
            if (nbt.contains(outer)) {
                textureManager.getTexture(nbt.getString(outer))
                        .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.OUTER, part));
            }
        }
    }

    public void writeToPacket(PacketByteBuf packet) {
        packet.writeEnumConstant(getColorMM());
        packet.writeBoolean(isContractMM());
        packet.writeString(getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
        for (Part part : Part.values()) {
            packet.writeString(getTextureHolder(Layer.INNER, part).getTextureName());
            packet.writeString(getTextureHolder(Layer.OUTER, part).getTextureName());
        }
    }

    public void readFromPacket(PacketByteBuf packet) {
        //readString()はクラ処理。このメソッドでは、クラ側なので問題なし
        setColorMM(packet.readEnumConstant(TextureColors.class));
        setContractMM(packet.readBoolean());
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
        textureManager.getTexture(packet.readString())
                .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.SKIN, Part.HEAD));
        for (Part part : Part.values()) {
            textureManager.getTexture(packet.readString())
                    .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.INNER, part));
            textureManager.getTexture(packet.readString())
                    .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.OUTER, part));
        }
    }

}
