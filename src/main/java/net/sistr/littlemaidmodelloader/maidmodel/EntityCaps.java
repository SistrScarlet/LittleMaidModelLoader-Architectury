package net.sistr.littlemaidmodelloader.maidmodel;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entityのデータ読み取り用のクラス
 */
public class EntityCaps implements IModelCaps {
    private static final Setter EMPTY_SETTER = (entity, arg) -> false;
    private static final Map<String, Integer> caps = new HashMap<>();
    private static final Int2ObjectArrayMap<Getter> capGetter = new Int2ObjectArrayMap<>();
    private static final Int2ObjectArrayMap<Setter> capSetter = new Int2ObjectArrayMap<>();
    protected LivingEntity owner;

    static {
        register("onGround", caps_onGround, (entity, arg) -> entity.isOnGround());
        register("isRiding", caps_isRiding, (entity, arg) -> entity.hasVehicle());
        register("isChild", caps_isChild, (entity, arg) -> entity.isBaby());
        register("heldItemLeft", caps_heldItemLeft, (entity, arg) -> 0F);
        register("heldItemRight", caps_heldItemRight, (entity, arg) -> 0F);
        register("heldItems", caps_heldItems, (entity, arg) -> new float[]{0.0F, 0.0F});
        register("isSneak", caps_isSneak, (entity, arg) -> entity.isSneaking());
        register("aimedBow", caps_aimedBow, (entity, arg) -> 0 < entity.getItemUseTime());
        register("Entity", caps_Entity, (entity, arg) -> entity);
        register("health", caps_health, (entity, arg) -> (int) entity.getHealth());
        register("ticksExisted", caps_ticksExisted, (entity, arg) -> entity.age);
        register("currentEquippedItem", caps_currentEquippedItem, (entity, arg) -> {
            List<ItemStack> items = Lists.newArrayList(entity.getItemsEquipped());
            ItemStack item = items.get((Integer) arg[0]);
            if (item.isEmpty()) item = null;
            return item;
        });
        register("currentArmor", caps_currentArmor, (entity, arg) -> {
            List<ItemStack> armors = Lists.newArrayList(entity.getArmorItems());
            ItemStack armor = armors.get((Integer) arg[0]);
            if (armor.isEmpty()) armor = null;
            return armor;
        });
        register("healthFloat", caps_healthFloat, (entity, arg) -> entity.getHealth());
        register("currentLeftHandItem", caps_currentLeftHandItem, (entity, arg) ->
                entity.getMainArm() == Arm.LEFT
                        ? entity.getMainHandStack()
                        : entity.getOffHandStack());
        register("currentRightHandItem", caps_currentRightHandItem, (entity, arg) ->
                entity.getMainArm() == Arm.RIGHT
                        ? entity.getMainHandStack()
                        : entity.getOffHandStack());
        register("isWet", caps_isWet, (entity, arg) -> entity.isWet());
        register("isDead", caps_isDead, (entity, arg) -> !entity.isAlive());
        register("isSwingInProgress", caps_isSwingInProgress, (entity, arg) -> entity.handSwingProgress);
        register("isBurning", caps_isBurning, (entity, arg) -> entity.isOnFire());
        register("isInWater", caps_isInWater, (entity, arg) -> entity.isTouchingWater());
        register("isInvisible", caps_isInvisible, (entity, arg) -> entity.isInvisible());
        register("isSprinting", caps_isSprinting, (entity, arg) -> entity.isSprinting());
        register("getRidingName", caps_getRidingName, (entity, arg) -> entity.getVehicle() == null
                ? ""
                : EntityType.getId(entity.getVehicle().getType()).toString());
        register("posX", caps_posX, (entity, arg) -> entity.getX());
        register("posY", caps_posY, (entity, arg) -> entity.getY());
        register("posZ", caps_posZ, (entity, arg) -> entity.getZ());
        register("pos", caps_pos, (entity, arg) -> {
            if (arg == null) {
                return new Double[]{entity.getX(), entity.getY(), entity.getZ()};
            }
            return (Integer) arg[0] == 0 ? entity.getX() : (Integer) arg[0] == 1 ? entity.getY() : entity.getZ();
        });
        register("motionX", caps_motionX, (entity, arg) -> entity.getVelocity().getX());
        register("motionY", caps_motionY, (entity, arg) -> entity.getVelocity().getY());
        register("motionZ", caps_motionZ, (entity, arg) -> entity.getVelocity().getZ());
        register("motion", caps_motion, (entity, arg) -> {
            Vec3d vec = entity.getVelocity();
            if (arg == null) {
                return new Double[]{vec.getX(), vec.getY(), vec.getZ()};
            }
            return (Integer) arg[0] == 0 ? vec.getX() : (Integer) arg[0] == 1 ? vec.getY() : vec.getZ();
        });
        register("boundingBox", caps_boundingBox, (entity, arg) -> {
            if (arg == null) {
                return entity.getBoundingBox();
            }
            switch ((Integer) arg[0]) {
                case 0:
                    return entity.getBoundingBox().maxX;
                case 1:
                    return entity.getBoundingBox().maxY;
                case 2:
                    return entity.getBoundingBox().maxZ;
                case 3:
                    return entity.getBoundingBox().minX;
                case 4:
                    return entity.getBoundingBox().minY;
                case 5:
                    return entity.getBoundingBox().minZ;
            }
            return null;
        });
        register("rotationYaw", caps_rotationYaw, (entity, arg) -> entity.yaw);
        register("rotationPitch", caps_rotationPitch, (entity, arg) -> entity.pitch);
        register("prevRotationYaw", caps_prevRotationYaw, (entity, arg) -> entity.prevYaw);
        register("prevRotationPitch", caps_prevRotationPitch, (entity, arg) -> entity.prevPitch);
        register("renderYawOffset", caps_renderYawOffset, (entity, arg) -> entity.bodyYaw);
        register("isRidingPlayer", caps_isRidingPlayer, (entity, arg) -> entity.getVehicle() instanceof PlayerEntity);
        register("WorldTotalTime", caps_WorldTotalTime, (entity, arg) -> entity.getEntityWorld().getTime());
        register("WorldTime", caps_WorldTime, (entity, arg) -> entity.getEntityWorld().getTimeOfDay());
        register("MoonPhase", caps_MoonPhase, (entity, arg) -> entity.getEntityWorld().getMoonPhase());
        register("entityIdFactor", caps_entityIdFactor, (entity, arg) -> 0F);
        register("height", caps_height, (entity, arg) -> entity.getHeight());
        register("width", caps_width, (entity, arg) -> entity.getWidth());
        register("YOffset", caps_YOffset, (entity, arg) -> entity.getHeightOffset());
        register("mountedYOffset", caps_mountedYOffset, (entity, arg) -> entity.getMountedHeightOffset());
        register("dominantArm", caps_dominantArm, (entity, arg) -> entity.getMainArm() == Arm.LEFT ? 0 : 1);
        register("PosBlockID", caps_PosBlockID, (entity, arg) ->
                entity.getEntityWorld().getBlockState(new BlockPos(
                        MathHelper.floor(entity.getX() + (Double) arg[0]),
                        MathHelper.floor(entity.getY() + (Double) arg[1]),
                        MathHelper.floor(entity.getZ() + (Double) arg[2]))).getBlock());
        register("PosBlockState", caps_PosBlockState, (entity, arg) ->
                entity.getEntityWorld().getBlockState(new BlockPos(
                        MathHelper.floor(entity.getX() + (Double) arg[0]),
                        MathHelper.floor(entity.getY() + (Double) arg[1]),
                        MathHelper.floor(entity.getZ() + (Double) arg[2]))));
        register("PosBlockAir", caps_PosBlockAir, (entity, arg) -> {
            BlockPos pos = new BlockPos(
                    MathHelper.floor(entity.getX() + (Double) arg[0]),
                    MathHelper.floor(entity.getY() + (Double) arg[1]),
                    MathHelper.floor(entity.getZ() + (Double) arg[2]));
            BlockState state = entity.getEntityWorld().getBlockState(pos);
            //移動可能ブロックかつ通常ブロックではない
            //Block.causesSuffocationから変更
            return !(state.getMaterial().blocksMovement() && !state.isFullCube(entity.world, pos));
        });
        register("PosBlockLight", caps_PosBlockLight, (entity, arg) ->
                entity.getEntityWorld().getLightLevel(new BlockPos(
                        MathHelper.floor(entity.getX() + (Double) arg[0]),
                        MathHelper.floor(entity.getY() + (Double) arg[1]),
                        MathHelper.floor(entity.getZ() + (Double) arg[2]))));
        register("PosBlockPower", caps_PosBlockPower, (entity, arg) ->
                entity.getEntityWorld().isAir(new BlockPos(
                        MathHelper.floor(entity.getX() + (Double) arg[0]),
                        MathHelper.floor(entity.getY() + (Double) arg[1]),
                        MathHelper.floor(entity.getZ() + (Double) arg[2]))));
        register("isSwimming", caps_isSwimming, (entity, arg) -> entity.isSwimming());
        register("roll", caps_roll, (entity, arg) -> entity.getRoll());
        register("leaningPitch", caps_leaningPitch, (entity, arg) -> entity.getLeaningPitch(1F));
        register("lastLeaningPitch", caps_lastLeaningPitch, (entity, arg) -> entity.getLeaningPitch(0F));
        register("pose", caps_pose, (entity, arg) -> entity.getPose());
        register("isPoseStanding", caps_isPoseStanding, (entity, arg) -> entity.getPose() == EntityPose.STANDING);
        register("isPoseFallFlying", caps_isPoseFallFlying, (entity, arg) -> entity.isFallFlying());
        register("isPoseSleeping", caps_isPoseSleeping, (entity, arg) -> entity.isSleeping());
        register("isPoseSwimming", caps_isPoseSwimming, (entity, arg) -> entity.isInSwimmingPose());
        register("isPoseSpinAttack", caps_isPoseSpinAttack, (entity, arg) -> entity.getPose() == EntityPose.SPIN_ATTACK);
        register("isPoseCrouching", caps_isPoseCrouching, (entity, arg) -> entity.isInSneakingPose());
        register("isPoseDying", caps_isPoseDying, (entity, arg) -> entity.isDead());
    }

    private static void register(String name, int index, Getter getter) {
        register(name, index, getter, EMPTY_SETTER);
    }

    private static void register(String name, int index, Getter getter, Setter setter) {
        caps.putIfAbsent(name, index);
        capGetter.put(index, getter);
        capSetter.put(index, setter);
    }

    public EntityCaps(LivingEntity pOwner) {
        owner = pOwner;
    }

    @Override
    public Map<String, Integer> getModelCaps() {
        return caps;
    }

    @Override
    public Object getCapsValue(int index, Object... arg) {
        return capGetter.computeIfAbsent(index, i -> (entity, arg1) -> null).get(owner, arg);
    }

    @Override
    public boolean setCapsValue(int index, Object... arg) {
        return capSetter.computeIfAbsent(index, i -> (entity, arg1) -> false).set(owner, arg);
    }

    public interface Getter {
        Object get(LivingEntity entity, Object... arg);
    }

    public interface Setter {
        boolean set(LivingEntity entity, Object... arg);
    }

}
