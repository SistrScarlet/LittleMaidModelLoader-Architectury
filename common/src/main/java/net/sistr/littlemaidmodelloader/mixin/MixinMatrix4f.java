package net.sistr.littlemaidmodelloader.mixin;

import net.minecraft.util.math.Matrix4f;
import net.sistr.littlemaidmodelloader.client.util.Matrix4fAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.FloatBuffer;

@Mixin(Matrix4f.class)
public class MixinMatrix4f implements Matrix4fAccessor {

    @Shadow
    protected float a00;

    @Shadow
    protected float a01;

    @Shadow
    protected float a02;

    @Shadow
    protected float a03;

    @Shadow
    protected float a10;

    @Shadow
    protected float a11;

    @Shadow
    protected float a12;

    @Shadow
    protected float a13;

    @Shadow
    protected float a20;

    @Shadow
    protected float a21;

    @Shadow
    protected float a22;

    @Shadow
    protected float a23;

    @Shadow
    protected float a30;

    @Shadow
    protected float a31;

    @Shadow
    protected float a32;

    @Shadow
    protected float a33;

    @Override
    public void readColumnMajor(FloatBuffer buf) {
        this.a00 = buf.get(pack(0, 0));
        this.a01 = buf.get(pack(0, 1));
        this.a02 = buf.get(pack(0, 2));
        this.a03 = buf.get(pack(0, 3));
        this.a10 = buf.get(pack(1, 0));
        this.a11 = buf.get(pack(1, 1));
        this.a12 = buf.get(pack(1, 2));
        this.a13 = buf.get(pack(1, 3));
        this.a20 = buf.get(pack(2, 0));
        this.a21 = buf.get(pack(2, 1));
        this.a22 = buf.get(pack(2, 2));
        this.a23 = buf.get(pack(2, 3));
        this.a30 = buf.get(pack(3, 0));
        this.a31 = buf.get(pack(3, 1));
        this.a32 = buf.get(pack(3, 2));
        this.a33 = buf.get(pack(3, 3));
    }

    @Unique
    private static int pack(int x, int y) {
        return y * 4 + x;
    }
}
