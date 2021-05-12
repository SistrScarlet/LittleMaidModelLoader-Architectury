package net.sistr.littlemaidmodelloader.multimodel.animation;


import net.sistr.littlemaidmodelloader.multimodel.model.ModelPart;

import java.util.List;

public interface IAngleList {

    Angle getAngle(ModelPart model);

    static void apply(IAngleList angles, List<ModelPart> models) {
        models.forEach(model -> {
            Angle angle = angles.getAngle(model);
            float rad = (float) (Math.PI / 180F);
            model.setRotation(angle.getPitch() * rad, angle.getYaw() * rad, angle.getRoll() * rad);
        });
    }

}
