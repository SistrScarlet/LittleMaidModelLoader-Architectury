package net.sistr.littlemaidmodelloader.maidmodel;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//TextureOffsetは死んだ。
public abstract class ModelBase extends AbstractModelBase {

    public static final float PI = (float) Math.PI;

    public EntityRenderer<?> render;

    // ModelBaseとある程度互換
    public int textureWidth = 64;
    public int textureHeight = 32;
    public float[] onGrounds = new float[]{0.0F, 0.0F};//R L
    public int dominantArm = 0;
    public boolean isRiding = false;
    public boolean isChild = true;
    public List<ModelRenderer> boxList = new ArrayList<>();

    //カスタム設定
    public boolean motionSitting = false;


    // ModelBase互換関数群

    public void render(IModelCaps pEntityCaps, float limbSwing, float limbSwingAmount,
                       float ticksExisted, float pheadYaw, float pheadPitch, float scale, boolean pIsRender) {
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float pTicksExisted,
                                  float pHeadYaw, float pHeadPitch, float scale, IModelCaps pEntityCaps) {
    }

    public void setLivingAnimations(IModelCaps pEntityCaps, float limbSwing, float limbSwingAmount, float pRenderPartialTicks) {
    }

    public ModelRenderer getRandomModelBox(Random par1Random) {
        // 膝に矢を受けてしまってな・・・
        int li = par1Random.nextInt(this.boxList.size());
        ModelRenderer lmr = this.boxList.get(li);
        for (int lj = 0; lj < boxList.size(); lj++) {
            if (!lmr.cubeList.isEmpty()) {
                break;
            }
            // 箱がない
            if (++li >= boxList.size()) {
                li = 0;
            }
            lmr = this.boxList.get(li);
        }
        return lmr;
    }

    @Deprecated
    protected void setTextureOffset(String par1Str, int par2, int par3) {
        //modelTextureMap.put(par1Str, new TextureOffset(par2, par3));
    }

    @Deprecated
    public Object getTextureOffset(String par1Str) {
        return null;
    }


    // MathHelperトンネル関数群

    public static float mh_sin(float f) {
        return MathHelper.sin(f);
    }

    public static float mh_cos(float f) {
        return MathHelper.cos(f);
    }

    public static float mh_sqrt(float f) {
        return MathHelper.sqrt(f);
    }

    public static float mh_sqrt(double d) {
        return MathHelper.sqrt((float) d);
    }

    public static int mh_floor(float f) {
        return MathHelper.floor(f);
    }

    public static int mh_floor(double d) {
        return MathHelper.floor(d);
    }

    public static long mh_floor_long(double d) {
        return MathHelper.floor(d);
    }

    public static float mh_abs(float f) {
        return MathHelper.abs(f);
    }

    public static double mh_abs_max(double d, double d1) {
        return MathHelper.absMax(d, d1);
    }

    public static int mh_bucketInt(int i, int j) {
        return MathHelper.floorDiv(i, j);
    }

    public static boolean mh_stringNullOrLengthZero(String s) {
        return s == null || s.equals("");
    }

    public static int mh_getRandomIntegerInRange(Random random, int minimum, int maximum) {
        return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
    }

}
