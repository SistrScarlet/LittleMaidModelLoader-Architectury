package net.sistr.littlemaidmodelloader.multimodel.animation;

import net.sistr.littlemaidmodelloader.multimodel.model.ModelPart;

public final class CompositeModelAngleSupplier {
    private final ModelAngleSupplier supplierA;
    private final ModelAngleSupplier supplierB;

    public CompositeModelAngleSupplier(ModelAngleSupplier supplierA, ModelAngleSupplier supplierB) {
        this.supplierA = supplierA;
        this.supplierB = supplierB;
    }

    public Angle getAngle(ModelPart model, float animationProgress, float percent) {
        return Angle.angleLerp(
                supplierA.getAngle(model, animationProgress),
                supplierB.getAngle(model, animationProgress),
                percent);
    }
}
