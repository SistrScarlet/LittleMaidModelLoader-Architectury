package net.sistr.littlemaidmodelloader.multimodel.animation;

import com.google.common.collect.ImmutableList;
import net.sistr.littlemaidmodelloader.multimodel.model.ModelPart;

import java.util.List;

public class AngleApplier {
    private final IAngleList angles;
    private final ImmutableList<ModelPart> models;

    public AngleApplier(IAngleList angles, List<ModelPart> models) {
        this.angles = angles;
        this.models = ImmutableList.copyOf(models);
    }

    public void apply() {
        models.forEach(this::applyAngle);
    }

    private void applyAngle(ModelPart model) {
        Angle angle = angles.getAngle(model);
        float rad = (float) (Math.PI / 180F);
        model.setRotation(angle.getPitch() * rad, angle.getYaw() * rad, angle.getRoll() * rad);
    }

}
