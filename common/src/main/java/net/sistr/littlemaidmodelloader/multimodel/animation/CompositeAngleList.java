package net.sistr.littlemaidmodelloader.multimodel.animation;

import net.sistr.littlemaidmodelloader.multimodel.model.ModelPart;

public class CompositeAngleList implements IAngleList {
    private final IAngleList a;
    private final IAngleList b;
    private final float percent;

    public CompositeAngleList(IAngleList a, IAngleList b, float percent) {
        this.a = a;
        this.b = b;
        this.percent = percent;
    }

    @Override
    public Angle getAngle(ModelPart model) {
        return Angle.angleLerp(a.getAngle(model), b.getAngle(model), percent);
    }
}
