package net.sistr.littlemaidmodelloader.client.util;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.ModelPart;

public interface ModelPartAccessor {

    ObjectList<ModelPart.Cuboid> getCuboids();
    ObjectList<ModelPart> getChildren();

}
