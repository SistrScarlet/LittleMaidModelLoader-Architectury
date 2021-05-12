package net.sistr.littlemaidmodelloader.multimodel.animation;

import net.sistr.littlemaidmodelloader.multimodel.model.ModelPart;

public interface ModelAngleSupplier {
    IAngleList getAngles(float animationProgress);
    Angle getAngle(ModelPart model, float animationProgress);
}
