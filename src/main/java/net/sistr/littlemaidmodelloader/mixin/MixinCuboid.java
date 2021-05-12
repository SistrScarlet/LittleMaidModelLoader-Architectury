package net.sistr.littlemaidmodelloader.mixin;

import net.minecraft.client.model.ModelPart;
import net.sistr.littlemaidmodelloader.client.util.CuboidAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPart.Cuboid.class)
public class MixinCuboid implements CuboidAccessor {

    @Shadow @Final private ModelPart.Quad[] sides;

    @Override
    public ModelPart.Quad[] getQuads() {
        return this.sides;
    }
}
