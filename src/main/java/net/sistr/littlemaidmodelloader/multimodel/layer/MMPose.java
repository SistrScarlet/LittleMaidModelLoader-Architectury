package net.sistr.littlemaidmodelloader.multimodel.layer;

import com.google.common.collect.ImmutableBiMap;
import net.minecraft.entity.EntityPose;

public class MMPose {
    private static final ImmutableBiMap<EntityPose, MMPose> POSE_MAP;
    public static final MMPose STANDING = new MMPose("Standing");
    public static final MMPose FALL_FLYING = new MMPose("FallFlying");
    public static final MMPose SLEEPING = new MMPose("Sleeping");
    public static final MMPose SWIMMING = new MMPose("Swimming");
    public static final MMPose SPIN_ATTACK = new MMPose("SpinAttack");
    public static final MMPose CROUCHING = new MMPose("Crouching");
    public static final MMPose DYING = new MMPose("Dying");

    private final String name;

    public MMPose(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MMPose convertPose(EntityPose pose) {
        return POSE_MAP.getOrDefault(pose, STANDING);
    }

    public static EntityPose convertPose(MMPose pose) {
        return POSE_MAP.inverse().getOrDefault(pose, EntityPose.STANDING);
    }

    static {
        ImmutableBiMap.Builder<EntityPose, MMPose> builder = new ImmutableBiMap.Builder<>();
        builder.put(EntityPose.STANDING, STANDING);
        builder.put(EntityPose.FALL_FLYING, FALL_FLYING);
        builder.put(EntityPose.SLEEPING, SLEEPING);
        builder.put(EntityPose.SWIMMING, SWIMMING);
        builder.put(EntityPose.SPIN_ATTACK, SPIN_ATTACK);
        builder.put(EntityPose.CROUCHING, CROUCHING);
        builder.put(EntityPose.DYING, DYING);
        POSE_MAP = builder.build();
    }
}
