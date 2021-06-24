package net.sistr.littlemaidmodelloader.multimodel.animation;

public final class AngleInterpolator implements AngleSupplier {
    private final Angle a;
    private final Angle b;
    private final Interpolator interpolator;

    public AngleInterpolator(Angle a, Angle b, Interpolator interpolator) {
        this.a = a;
        this.b = b;
        this.interpolator = interpolator;
    }

    public Angle getAngle(float percent) {
        percent = interpolator.get(percent);
        return Angle.angleLerp(a, b, percent);
    }

}
