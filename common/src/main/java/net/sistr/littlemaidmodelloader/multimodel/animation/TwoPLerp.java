package net.sistr.littlemaidmodelloader.multimodel.animation;

public class TwoPLerp {
    private final Angle baseAngle;
    private final Angle angleA;
    private final Angle angleB;
    private final Angle angleAB;

    public TwoPLerp(Angle angleA, Angle angleB, Angle angleAB) {
        this.baseAngle = Angle.ZERO;
        this.angleA = angleA;
        this.angleB = angleB;
        this.angleAB = angleAB;
    }

    public TwoPLerp(Angle baseAngle, Angle angleA, Angle angleB, Angle angleAB) {
        this.baseAngle = baseAngle;
        this.angleA = angleA;
        this.angleB = angleB;
        this.angleAB = angleAB;
    }

    public Angle lerp(float percentA, float percentB) {
        return new Angle(
                lerpTwoPercent(baseAngle.getPitch(), angleA.getPitch(), angleB.getPitch(), angleAB.getPitch(), percentA, percentB),
                lerpTwoPercent(baseAngle.getYaw(), angleA.getYaw(), angleB.getYaw(), angleAB.getYaw(), percentA, percentB),
                lerpTwoPercent(baseAngle.getRoll(), angleA.getRoll(), angleB.getRoll(), angleAB.getRoll(), percentA, percentB));
    }

    private static float lerpTwoPercent(float base, float a, float b, float ab, float percentA, float percentB) {
        float pitchA = a - base;
        float pitchB = b - base;
        float pitchAB = ab - base - pitchA - pitchB;
        return base + pitchA * percentA + pitchB * percentB + pitchAB * percentA * percentB;
    }

}
