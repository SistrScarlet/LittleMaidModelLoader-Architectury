package net.sistr.littlemaidmodelloader.entity;


import dev.architectury.extensions.network.EntitySpawnExtension;
import dev.architectury.networking.NetworkManager;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sistr.littlemaidmodelloader.client.screen.ModelSelectScreen;
import net.sistr.littlemaidmodelloader.client.screen.SoundPackSelectScreen;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.entity.compound.MultiModelCompound;
import net.sistr.littlemaidmodelloader.entity.compound.SoundPlayable;
import net.sistr.littlemaidmodelloader.entity.compound.SoundPlayableCompound;
import net.sistr.littlemaidmodelloader.maidmodel.IModelCaps;
import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMPose;
import net.sistr.littlemaidmodelloader.resource.holder.ConfigHolder;
import net.sistr.littlemaidmodelloader.resource.holder.TextureHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMModelManager;
import net.sistr.littlemaidmodelloader.resource.manager.LMTextureManager;
import net.sistr.littlemaidmodelloader.resource.util.LMSounds;
import net.sistr.littlemaidmodelloader.resource.util.TextureColors;
import net.sistr.littlemaidmodelloader.setup.Registration;

import java.util.Optional;

/**
 * テスト用エンティティ
 */
public class MultiModelEntity extends PathAwareEntity implements IHasMultiModel, SoundPlayable, EntitySpawnExtension {
    private final MultiModelCompound multiModel;
    private final SoundPlayableCompound soundPlayer;

    public MultiModelEntity(World worldIn) {
        this(Registration.MULTI_MODEL_ENTITY.get(), worldIn);
    }

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
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        multiModel.writeToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        multiModel.readFromNbt(nbt);
    }

    @Override
    public void saveAdditionalSpawnData(PacketByteBuf packet) {
        multiModel.writeToPacket(packet);
    }

    @Override
    public void loadAdditionalSpawnData(PacketByteBuf packet) {
        multiModel.readFromPacket(packet);
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
            openGUI(player.isSneaking());
            play(LMSounds.LIVING_DAYTIME);
        }
        return super.interactMob(player, hand);
    }

    @Environment(EnvType.CLIENT)
    public void openGUI(boolean shift) {
        MinecraftClient.getInstance().setScreen(
                shift ? new SoundPackSelectScreen<>(Text.of(""), this) :
                        new ModelSelectScreen<>(Text.of(""), this.world, this));
    }

    //このままだとEntityDimensionsが作っては捨てられてを繰り返すのでパフォーマンスはよろしくない
    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        EntityDimensions dimensions;
        IMultiModel model = getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel());
        IModelCaps caps = getCaps();
        MMPose mmPose = MMPose.convertPose(pose);
        float height = model.getHeight(caps, mmPose);
        float width = model.getWidth(caps, mmPose);
        dimensions = EntityDimensions.changing(width, height);
        return dimensions.scaled(getScaleFactor());
    }

    //視点調整
    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        //初期化前に呼ばれることがあるためチェック
        if (multiModel == null) return dimensions.height * 0.85F;
        return getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel())
                .getEyeHeight(getCaps(), MMPose.convertPose(pose));
    }

    //上になんか乗ってるやつのオフセット
    @Override
    public double getMountedHeightOffset() {
        IMultiModel model = getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel());
        return model.getMountedYOffset(getCaps());
    }

    //騎乗時のオフセット
    @Override
    public double getHeightOffset() {
        IMultiModel model = getModel(Layer.SKIN, Part.HEAD)
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
    public void setTextureHolder(TextureHolder textureHolder, Layer layer, Part part) {
        multiModel.setTextureHolder(textureHolder, layer, part);
        if (layer == Layer.SKIN) {
            calculateDimensions();
        }
    }

    @Override
    public TextureHolder getTextureHolder(IHasMultiModel.Layer layer, IHasMultiModel.Part part) {
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
    public Optional<IMultiModel> getModel(Layer layer, Part part) {
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
    public boolean isAllowChangeTexture(Entity changer, TextureHolder textureHolder, IHasMultiModel.Layer layer, IHasMultiModel.Part part) {
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
        return NetworkManager.createAddEntityPacket(this);
    }
}
