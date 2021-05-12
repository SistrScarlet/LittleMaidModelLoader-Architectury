package net.sistr.littlemaidmodelloader.multimodel.animation;

import net.sistr.littlemaidmodelloader.multimodel.model.ModelPart;

public class AngleList implements IAngleList {
    private final ModelAngleSupplier supplier;
    private final float animationProgress;

    public AngleList(ModelAngleSupplier supplier, float animationProgress) {
        this.supplier = supplier;
        this.animationProgress = animationProgress;
    }

    @Override
    public Angle getAngle(ModelPart model) {
        return supplier.getAngle(model, animationProgress);
    }
}
