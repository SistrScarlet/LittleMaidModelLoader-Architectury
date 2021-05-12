package net.sistr.littlemaidmodelloader.multimodel.animation;

import com.google.common.collect.ImmutableList;
import net.sistr.littlemaidmodelloader.multimodel.model.ModelPart;

import java.util.List;

public final class Animator {
    private final ModelAngleSupplier modelAngleSupplier;
    private final ImmutableList<ModelPart> parts;

    public Animator(ModelAngleSupplier modelAngleSupplier, List<ModelPart> parts) {
        this.modelAngleSupplier = modelAngleSupplier;
        this.parts = ImmutableList.copyOf(parts);
    }

    public void animate(float animationProgress) {
        //MinecraftClient.getInstance().getProfiler().push("model_animate");
        parts.forEach(e -> setModelPartAngle(e, modelAngleSupplier.getAngle(e, animationProgress)));
        //MinecraftClient.getInstance().getProfiler().pop();
    }

    private static void setModelPartAngle(ModelPart model, Angle angle) {
        float rad = (float) (Math.PI / 180F);
        model.setRotation(
                angle.getPitch() * rad,
                angle.getYaw() * rad,
                angle.getRoll() * rad);
    }

}
