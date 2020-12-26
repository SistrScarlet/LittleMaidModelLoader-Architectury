package net.sistr.lmml.entity;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sistr.lmml.client.ModelSelectScreen;
import net.sistr.lmml.entity.compound.IHasMultiModel;
import net.sistr.lmml.entity.compound.MultiModelCompound;
import net.sistr.lmml.entity.compound.SoundPlayable;
import net.sistr.lmml.entity.compound.SoundPlayableCompound;
import net.sistr.lmml.maidmodel.IModelCaps;
import net.sistr.lmml.maidmodel.ModelMultiBase;
import net.sistr.lmml.network.CustomMobSpawnPacket;
import net.sistr.lmml.network.util.CustomPacketEntity;
import net.sistr.lmml.resource.holder.ConfigHolder;
import net.sistr.lmml.resource.holder.TextureHolder;
import net.sistr.lmml.resource.manager.LMModelManager;
import net.sistr.lmml.resource.manager.LMTextureManager;
import net.sistr.lmml.resource.util.LMSounds;
import net.sistr.lmml.resource.util.TextureColors;

import java.util.Optional;

//テスト用エンティティ
public class MultiModelEntity extends PathAwareEntity implements IHasMultiModel, SoundPlayable, CustomPacketEntity {
    private final MultiModelCompound multiModel;
    private final SoundPlayableCompound soundPlayer;

    public MultiModelEntity(EntityType<MultiModelEntity> type, World worldIn) {
        super(type, worldIn);
        multiModel = new MultiModelCompound(this,
                LMTextureManager.INSTANCE.getTexture("default")
                        .orElseThrow(() -> new IllegalStateException("デフォルトモデルが存在しません。")),
                LMTextureManager.INSTANCE.getTexture("default")
                        .orElseThrow(() -> new IllegalStateException("デフォルトモデルが存在しません。")));
        soundPlayer = new SoundPlayableCompound(this,
                () -> multiModel.getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putByte("SkinColor", (byte) getColor().getIndex());
        tag.putBoolean("IsContract", isContract());
        tag.putString("SkinTexture", getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
        for (Part part : Part.values()) {
            tag.putString("ArmorTextureInner" + part.getPartName(),
                    getTextureHolder(Layer.INNER, part).getTextureName());
            tag.putString("ArmorTextureOuter" + part.getPartName(),
                    getTextureHolder(Layer.OUTER, part).getTextureName());
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("SkinColor")) {
            setColor(TextureColors.getColor(tag.getByte("SkinColor")));
        }
        setContract(tag.getBoolean("IsContract"));
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
        if (tag.contains("SkinTexture")) {
            textureManager.getTexture(tag.getString("SkinTexture"))
                    .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.SKIN, Part.HEAD));
        }
        for (Part part : Part.values()) {
            String inner = "ArmorTextureInner" + part.getPartName();
            String outer = "ArmorTextureOuter" + part.getPartName();
            if (tag.contains(inner)) {
                textureManager.getTexture(tag.getString(inner))
                        .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.INNER, part));
            }
            if (tag.contains(outer)) {
                textureManager.getTexture(tag.getString(outer))
                        .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.OUTER, part));
            }
        }
    }

    @Override
    public void writeCustomPacket(PacketByteBuf packet) {
        packet.writeEnumConstant(getColor());
        packet.writeBoolean(isContract());
        packet.writeString(getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
        for (Part part : Part.values()) {
            packet.writeString(getTextureHolder(Layer.INNER, part).getTextureName());
            packet.writeString(getTextureHolder(Layer.OUTER, part).getTextureName());
        }
    }

    @Override
    public void readCustomPacket(PacketByteBuf packet) {
        //readString()はクラ処理。このメソッドでは、クラ側なので問題なし
        setColor(packet.readEnumConstant(TextureColors.class));
        setContract(packet.readBoolean());
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

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) stack.getItem();
            this.equipStack(armor.getSlotType(), stack);
            return ActionResult.success(player.world.isClient);
        }
        if (world.isClient) {
            openGUI();
            play(LMSounds.LIVING_DAYTIME);
        }
        return super.interactMob(player, hand);
    }

    @Environment(EnvType.CLIENT)
    public void openGUI() {
        MinecraftClient.getInstance().openScreen(
                new ModelSelectScreen(new LiteralText(""), this.world, this));
    }

    //このままだとEntityDimensionsが作っては捨てられてを繰り返すのでパフォーマンスはよろしくない
    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        EntityDimensions dimensions;
        ModelMultiBase model = getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel());
        float height = model.getHeight(getCaps());
        float width = model.getWidth(getCaps());
        dimensions = EntityDimensions.changing(width, height);
        return dimensions.scaled(getScaleFactor());
    }

    //上になんか乗ってるやつのオフセット
    @Override
    public double getMountedHeightOffset() {
        ModelMultiBase model = getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel());
        return model.getMountedYOffset(getCaps());
    }

    //騎乗時のオフセット
    @Override
    public double getHeightOffset() {
        ModelMultiBase model = getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel());
        return model.getyOffset(getCaps()) - getHeight();
    }

    //防具の更新
    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            multiModel.updateArmor();
        }
        super.equipStack(slot, stack);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Optional<Identifier> getTexture(IHasMultiModel.Layer layer, IHasMultiModel.Part part, boolean isLight) {
        return multiModel.getTexture(layer, part, isLight);
    }

    @Override
    public void setTextureHolder(TextureHolder textureHolder,  IHasMultiModel.Layer layer,  IHasMultiModel.Part part) {
        multiModel.setTextureHolder(textureHolder, layer, part);
    }

    @Override
    public TextureHolder getTextureHolder( IHasMultiModel.Layer layer,  IHasMultiModel.Part part) {
        return multiModel.getTextureHolder(layer, part);
    }

    @Override
    public void setColor(TextureColors color) {
        multiModel.setColor(color);
    }

    @Override
    public TextureColors getColor() {
        return multiModel.getColor();
    }

    @Override
    public void setContract(boolean isContract) {
        multiModel.setContract(isContract);
    }

    @Override
    public boolean isContract() {
        return multiModel.isContract();
    }

    @Override
    public Optional<ModelMultiBase> getModel( IHasMultiModel.Layer layer,  IHasMultiModel.Part part) {
        return multiModel.getModel(layer, part);
    }

    @Override
    public IModelCaps getCaps() {
        return multiModel.getCaps();
    }

    @Override
    public boolean isArmorVisible(Part part) {
        return multiModel.isArmorVisible(part);
    }

    @Override
    public boolean isArmorGlint(Part part) {
        return multiModel.isArmorGlint(part);
    }

    @Override
    public boolean isAllowChangeTexture( Entity changer, TextureHolder textureHolder,  IHasMultiModel.Layer layer,  IHasMultiModel.Part part) {
        return true;
    }

    @Override
    public void play(String soundName) {
        soundPlayer.play(soundName);
    }

    @Override
    public void setConfigHolder(ConfigHolder configHolder) {
        soundPlayer.setConfigHolder(configHolder);
    }

    @Override
    public ConfigHolder getConfigHolder() {
        return soundPlayer.getConfigHolder();
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return CustomMobSpawnPacket.createPacket(this);
    }
}
