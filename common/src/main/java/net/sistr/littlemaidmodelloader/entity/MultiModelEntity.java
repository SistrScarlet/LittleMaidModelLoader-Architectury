package net.sistr.littlemaidmodelloader.entity;

import dev.architectury.extensions.network.EntitySpawnExtension;
import dev.architectury.networking.NetworkManager;
import java.util.Optional;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
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

/** テスト用エンティティ */
public class MultiModelEntity extends PathAwareEntity
        implements IHasMultiModel, SoundPlayable, EntitySpawnExtension {
    private final MultiModelCompound multiModel;
    private final SoundPlayableCompound soundPlayer;

    public MultiModelEntity(EntityType<MultiModelEntity> type, World worldIn) {
        super(type, worldIn);
        multiModel =
                new MultiModelCompound(
                        this,
                        LMTextureManager.INSTANCE
                                .getTexture("default")
                                .orElseThrow(() -> new IllegalStateException("デフォルトモデルが存在しません。")),
                        LMTextureManager.INSTANCE
                                .getTexture("default")
                                .orElseThrow(() -> new IllegalStateException("デフォルトモデルが存在しません。")));
        soundPlayer =
                new SoundPlayableCompound(
                        this,
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
        if (player.isSneaking()) {
            if (!getWorld().isClient()) {
                this.setSneaking(!this.isSneaking());
            }
            return ActionResult.success(player.getWorld().isClient);
        }
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof ArmorItem armor) {
            this.equipStack(armor.getSlotType(), stack);
            return ActionResult.success(player.getWorld().isClient);
        }
        ItemStack thisStack = this.getMainHandStack();
        if (hand == Hand.MAIN_HAND
                && !getWorld().isClient
                && !stack.isEmpty() == thisStack.isEmpty()) {
            player.setStackInHand(Hand.MAIN_HAND, thisStack);
            this.setStackInHand(Hand.MAIN_HAND, stack);
        }
        if (getWorld().isClient) {
            openGUI(!player.isOnGround());
            play(LMSounds.LIVING_DAYTIME);
        }
        return super.interactMob(player, hand);
    }

    @Environment(EnvType.CLIENT)
    public void openGUI(boolean isSound) {
        MinecraftClient.getInstance()
                .setScreen(
                        isSound
                                ? new SoundPackSelectScreen<>(Text.of(""), this)
                                : new ModelSelectScreen<>(Text.of(""), this.getWorld(), this));
    }

    // 1.21 で LivingEntity#getDimensions は final 化され、サブクラスの拡張点は
    // getBaseDimensions に移された。ここでモデルの実寸を返すことで、hitbox に加えて
    // 視点高さ (final な getEyeHeight は EntityDimensions#eyeHeight を読むだけ) と
    // 乗客の座席位置 (PASSENGER attachment、旧 getMountedHeightOffset 相当) も追従する。
    // scaled() は width/height/eyeHeight/attachments をまとめてスケールするため最後に呼ぶ。
    // 既定実装が内包している getScaleFactor() の適用は override すると失われるので掛け直す。
    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        // 初期化前に呼ばれることがあるためチェック
        if (multiModel == null) return super.getBaseDimensions(pose);
        IMultiModel model =
                getModel(Layer.SKIN, Part.HEAD).orElseGet(LMModelManager.INSTANCE::getDefaultModel);
        IModelCaps caps = getCaps();
        MMPose mmPose = MMPose.convertPose(pose);
        return EntityDimensions.changing(
                        model.getWidth(caps, mmPose), model.getHeight(caps, mmPose))
                .withEyeHeight(model.getEyeHeight(caps, mmPose))
                .withAttachments(
                        EntityAttachments.builder()
                                .add(
                                        EntityAttachmentType.PASSENGER,
                                        0.0F,
                                        model.getMountedYOffset(caps),
                                        0.0F))
                .scaled(getScaleFactor());
    }

    // 旧 getHeightOffset (自分が何かに乗るときの自身のオフセット) の後継。
    // 1.20.1 は vehicleY + vehicle.getMountedHeightOffset() + passenger.getHeightOffset() の
    // 加算式、1.21.1 は vehicle.getPassengerRidingPos() - passenger.getVehicleAttachmentPos() の
    // 減算式なので符号が反転する。0.2875 はボートでの見た目を 1.20.1 に合わせるための補正
    // (getMountedHeightOffset=-0.1 → PASSENGER attachment=0.1875 の差分)。
    @Override
    public Vec3d getVehicleAttachmentPos(Entity vehicle) {
        IMultiModel model =
                getModel(Layer.SKIN, Part.HEAD).orElseGet(LMModelManager.INSTANCE::getDefaultModel);
        return new Vec3d(0.0, 0.2875 + getHeight() - model.getyOffset(getCaps()), 0.0);
    }

    // 防具の更新
    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        if (slot.isArmorSlot()) {
            multiModel.updateArmor();
        }
        super.equipStack(slot, stack);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Optional<Identifier> getTexture(
            IHasMultiModel.Layer layer, IHasMultiModel.Part part, boolean isLight) {
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
    public void setColorMM(TextureColors color) {
        multiModel.setColorMM(color);
    }

    @Override
    public TextureColors getColorMM() {
        return multiModel.getColorMM();
    }

    @Override
    public void setContractMM(boolean isContract) {
        multiModel.setContractMM(isContract);
    }

    @Override
    public boolean isContractMM() {
        return multiModel.isContractMM();
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
    public boolean isAllowChangeTexture(
            Entity changer,
            TextureHolder textureHolder,
            IHasMultiModel.Layer layer,
            IHasMultiModel.Part part) {
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
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry trackerEntry) {
        return NetworkManager.createAddEntityPacket(this, trackerEntry);
    }
}
