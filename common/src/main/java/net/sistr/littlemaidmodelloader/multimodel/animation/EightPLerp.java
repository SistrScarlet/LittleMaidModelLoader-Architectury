package net.sistr.littlemaidmodelloader.multimodel.animation;

public class EightPLerp {
    private final TwoPLerp leftUp;
    private final TwoPLerp rightUp;
    private final TwoPLerp leftDown;
    private final TwoPLerp rightDown;

    public EightPLerp(Angle left, Angle up, Angle down,
                      Angle leftUp, Angle leftDown) {
        this(left, new Angle(left.getPitch(), -left.getYaw(), -left.getRoll()), up, down,
                leftUp, new Angle(leftUp.getPitch(), -leftUp.getYaw(), -leftUp.getRoll()),
                leftDown, new Angle(leftDown.getPitch(), -leftDown.getYaw(), -leftDown.getRoll()));
    }

    public EightPLerp(Angle left, Angle right, Angle up, Angle down,
                      Angle leftUp, Angle rightUp, Angle leftDown, Angle rightDown) {
        this.leftUp = new TwoPLerp(left, up, leftUp);
        this.rightUp = new TwoPLerp(right, up, rightUp);
        this.leftDown = new TwoPLerp(left, down, leftDown);
        this.rightDown = new TwoPLerp(right, down, rightDown);
    }

    public Angle lerp(float percentA, float percentB) {
        boolean isLeft = percentA < 0;
        boolean isUp = percentB < 0;
        if (isLeft) {
            if (isUp) {
                return leftUp.lerp(-percentA, -percentB);
            } else {
                return leftDown.lerp(-percentA, percentB);
            }
        } else {
            if (isUp) {
                return rightUp.lerp(percentA, -percentB);
            } else {
                return rightDown.lerp(percentA, percentB);
            }
        }
    }

}
