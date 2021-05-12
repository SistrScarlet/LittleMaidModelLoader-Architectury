package net.sistr.littlemaidmodelloader.multimodel.animation;

public final class Angle {
    public static final Angle ZERO = new Angle(0, 0, 0);
    private final float pitch;
    private final float yaw;
    private final float roll;

    public Angle() {
        this.pitch = 0;
        this.yaw = 0;
        this.roll = 0;
    }

    public Angle(float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public static Angle angleLerp(Angle a, Angle b, float percent) {
        return new Angle(
                lerp(a.getPitch(), b.getPitch(), percent),
                lerp(a.getYaw(), b.getYaw(), percent),
                lerp(a.getRoll(), b.getRoll(), percent));
    }

    private static float lerp(float a, float b, float percent) {
        return a + (b - a) * percent;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

}
