package net.sistr.littlemaidmodelloader.mixin;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.ModelPart;
import net.sistr.littlemaidmodelloader.client.util.ModelPartAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPart.class)
public class MixinModelPart implements ModelPartAccessor {

    @Shadow @Final private ObjectList<ModelPart.Cuboid> cuboids;

    @Shadow @Final private ObjectList<ModelPart> children;

    @Override
    public ObjectList<ModelPart.Cuboid> getCuboids() {
        return this.cuboids;
    }

    @Override
    public ObjectList<ModelPart> getChildren() {
        return this.children;
    }


}
