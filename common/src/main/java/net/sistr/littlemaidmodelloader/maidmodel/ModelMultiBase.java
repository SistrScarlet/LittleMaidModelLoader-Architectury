package net.sistr.littlemaidmodelloader.maidmodel;

import me.shedaniel.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMMatrixStack;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMPose;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMRenderContext;

import java.util.HashMap;
import java.util.Map;

/**
 * マルチモデル用の基本クラス、これを継承していればマルチモデルとして使用できる。
 * Mincraftネイティブなクラスや継承関数などを排除して、難読化対策を行う。
 * 継承クラスではなくなったため、直接的な互換性はない。
 */
//1.12->1.15における変更点はSide判定がFMLCommonHandlerからFMLEnvironmentに変わった程度
public abstract class ModelMultiBase extends ModelBase implements IModelCaps, IMultiModel {

    public float[] heldItem = new float[]{0.0F, 0.0F};
    public boolean aimedBow;
    public boolean isSneak;
    public boolean isWait;

    public ModelRenderer mainFrame;
    public ModelRenderer HeadMount;
    public ModelRenderer HeadTop;
    public ModelRenderer[] Arms;//right0/left1?
    public ModelRenderer[] HardPoint;

    public float entityIdFactor;
    public int entityTicksExisted;
    // 変数である意味ない？
    public float scaleFactor = 0.9375F;
    /**
     * モデルが持っている機能群
     */
    private final Map<String, Integer> fcapsmap = new HashMap<String, Integer>() {{
        put("onGround", caps_onGround);
        put("isRiding", caps_isRiding);
        put("isSneak", caps_isSneak);
        put("isWait", caps_isWait);
        put("isChild", caps_isChild);
        put("heldItemLeft", caps_heldItemLeft);
        put("heldItemRight", caps_heldItemRight);
        put("aimedBow", caps_aimedBow);
        put("ScaleFactor", caps_ScaleFactor);
        put("entityIdFactor", caps_entityIdFactor);
        put("dominantArm", caps_dominantArm);
    }};

    //追加
    private IModelCaps caps;
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float netHeadYaw;
    private float headPitch;


    public ModelMultiBase() {
        this(0.0F);
    }

    public ModelMultiBase(float pSizeAdjust) {
        this(pSizeAdjust, 0.0F, 64, 32);
    }

    public ModelMultiBase(float pSizeAdjust, float pYOffset, int pTextureWidth, int pTextureHeight) {
        isSneak = false;
        aimedBow = false;
        textureWidth = pTextureWidth;
        textureHeight = pTextureHeight;

        if (Platform.getEnv() == EnvType.CLIENT) {
//			LittleMaidReengaged.Debug("ModelMulti.InitClient");
            // ハードポイント
            Arms = new ModelRenderer[2];
            HeadMount = new ModelRenderer(this, "HeadMount");
            HeadTop = new ModelRenderer(this, "HeadTop");

            initModel(pSizeAdjust, pYOffset);
        }
    }

    //追加

    @Override
    public void setupTransform(IModelCaps caps, MMMatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        float leaningPitch = ModelCapsHelper.getCapsValueFloat(caps, IModelCaps.caps_leaningPitch);
        float roll;
        float k;
        if (ModelCapsHelper.getCapsValueBoolean(caps, IModelCaps.caps_isFallFlying)) {
            roll = ModelCapsHelper.getCapsValueFloat(caps, IModelCaps.caps_roll) + tickDelta;
            k = MathHelper.clamp(roll * roll / 100.0F, 0.0F, 1.0F);
            if (!ModelCapsHelper.getCapsValueBoolean(caps, IModelCaps.caps_isUsingRiptide)) {
                matrices.rotateXDeg(k * (-90.0F - ModelCapsHelper.getCapsValueFloat(caps, IModelCaps.caps_rotationPitch)));
            }

            Vec3d lookFor = getRotationVec(caps, tickDelta);
            Vec3d velocity =
                    new Vec3d(ModelCapsHelper.getCapsValueDouble(caps, IModelCaps.caps_motionX),
                            ModelCapsHelper.getCapsValueDouble(caps, IModelCaps.caps_motionY),
                            ModelCapsHelper.getCapsValueDouble(caps, IModelCaps.caps_motionZ));
            double d = Entity.squaredHorizontalLength(velocity);
            double e = Entity.squaredHorizontalLength(lookFor);
            if (d > 0.0D && e > 0.0D) {
                double l = (velocity.x * lookFor.x + velocity.z * lookFor.z) / Math.sqrt(d * e);
                double m = velocity.x * lookFor.z - velocity.z * lookFor.x;
                matrices.rotateYRad((float) (Math.signum(m) * Math.acos(l)));
            }
        } else if (leaningPitch > 0.0F) {
            roll = ModelCapsHelper.getCapsValueBoolean(caps, IModelCaps.caps_isInWater)
                    ? -90.0F - ModelCapsHelper.getCapsValueFloat(caps, IModelCaps.caps_rotationPitch)
                    : -90.0F;
            k = MathHelper.lerp(leaningPitch, 0.0F, roll);
            matrices.rotateXDeg(k);
            if (ModelCapsHelper.getCapsValueBoolean(caps, IModelCaps.caps_isSwimming)) {
                matrices.translate(0.0D, -1.0D, 0.3D);
            }
        }
    }

    private Vec3d getRotationVec(IModelCaps caps, float tickDelta) {
        float yaw = ModelCapsHelper.getCapsValueFloat(caps, IModelCaps.caps_rotationYaw);
        float prevYaw = ModelCapsHelper.getCapsValueFloat(caps, IModelCaps.caps_prevRotationYaw);
        float pitch = ModelCapsHelper.getCapsValueFloat(caps, IModelCaps.caps_rotationPitch);
        float prevPitch = ModelCapsHelper.getCapsValueFloat(caps, IModelCaps.caps_prevRotationPitch);
        return getRotationVector(MathHelper.lerp(tickDelta, prevPitch, pitch), MathHelper.lerp(tickDelta, prevYaw, yaw));
    }

    private Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    @Override
    public void animateModel(IModelCaps caps, float limbAngle, float limbDistance, float tickDelta) {
        setLivingAnimations(caps, limbAngle, limbDistance, tickDelta);
    }

    @Override
    public void setAngles(IModelCaps caps, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.caps = caps;
        this.limbSwing = limbAngle;
        this.limbSwingAmount = limbDistance;
        this.ageInTicks = animationProgress;
        this.netHeadYaw = headYaw;
        this.headPitch = headPitch;
        setRotationAngles(limbAngle, limbDistance, animationProgress, headYaw, headPitch, 0.0625F, caps);
    }

    @Override
    public void render(MMRenderContext context) {
        context.render(ModelRenderer::setParam);
        render(caps, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 0.0625F, true);
    }

    @Override
    public void adjustHandItem(MMMatrixStack matrices, boolean isLeft) {
        ModelRenderer.matrixStack = matrices.getVanillaMatrixStack();
        Arms[isLeft ? 1 : 0].postRender(0.0625F);
    }

    @Override
    public int getTextureWidth() {
        return this.textureWidth;
    }

    @Override
    public int getTextureHeight() {
        return this.textureHeight;
    }

    @Override
    public float getInnerArmorSize() {
        return getArmorModelsSize()[0];
    }

    @Override
    public float getOuterArmorSize() {
        return getArmorModelsSize()[1];
    }

    public float getEyeHeight(IModelCaps caps) {
        return this.getEyeHeight(caps, MMPose.STANDING);
    }

    @Override
    public float getEyeHeight(IModelCaps caps, MMPose pose) {
        return getHeight(caps, pose) * 0.85F;
    }

    // 独自定義関数群

    /**
     * モデルの初期化コード
     */
    public abstract void initModel(float psize, float pyoffset);

    /**
     * モデル指定詞に依らずに使用するテクスチャパック名。
     * 一つのテクスチャに複数のモデルを割り当てる時に使う。
     *
     * @return
     */
    public String getUsingTexture() {
        return null;
    }

    /**
     * 身長
     */
    @Deprecated
    public abstract float getHeight();

    /**
     * 身長
     */
    public float getHeight(IModelCaps pEntityCaps) {
        return getHeight();
    }

    /**
     * 身長
     */
    @Override
    public float getHeight(IModelCaps pEntityCaps, MMPose pose) {
        if (pose == MMPose.FALL_FLYING || pose == MMPose.SWIMMING || pose == MMPose.SPIN_ATTACK) {
            return Math.min(getHeight(pEntityCaps), getWidth(pEntityCaps, pose));
        } else if (pose == MMPose.SLEEPING || pose == MMPose.DYING) {
            return 0.2f;
        } else if (pose == MMPose.CROUCHING) {
            return Math.max(0.2f, getHeight(pEntityCaps) - 0.3f);
        }
        return getHeight(pEntityCaps);
    }

    /**
     * 横幅
     */
    @Deprecated
    public abstract float getWidth();

    /**
     * 横幅
     */
    public float getWidth(IModelCaps pEntityCaps) {
        return getWidth();
    }

    /**
     * 横幅
     */
    public float getWidth(IModelCaps pEntityCaps, MMPose pose) {
        if (pose == MMPose.SLEEPING || pose == MMPose.DYING) {
            return 0.2f;
        }
        return getWidth();
    }

    /**
     * モデルのYオフセット
     */
    @Deprecated
    public abstract float getyOffset();

    /**
     * モデルのYオフセット
     */
    public float getyOffset(IModelCaps pEntityCaps) {
        return getyOffset();
    }

    /**
     * 上に乗せる時のオフセット高
     */
    @Deprecated
    public abstract float getMountedYOffset();

    /**
     * 上に乗せる時のオフセット高
     */
    public float getMountedYOffset(IModelCaps pEntityCaps) {
        return getMountedYOffset();
    }

    /**
     * ロープの取り付け位置調整用
     *
     * @return
     */
    public float getLeashOffset(IModelCaps pEntityCaps) {
        return 0.4F;
    }

    /**
     * アイテムを持っているときに手を前に出すかどうか。
     */
    @Deprecated
    public boolean isItemHolder() {
        return false;
    }

    /**
     * アイテムを持っているときに手を前に出すかどうか。
     */
    public boolean isItemHolder(IModelCaps pEntityCaps) {
        return isItemHolder();
    }

    /**
     * 表示すべきすべての部品
     */
    public void showAllParts() {
    }

    /**
     * 表示すべきすべての部品
     */
    public void showAllParts(IModelCaps pEntityCaps) {
        showAllParts();
    }

    /**
     * 部位ごとの装甲表示。
     *
     * @param parts 3:頭部。
     *              2:胴部。
     *              1:脚部
     *              0:足部
     * @param index 0:inner
     *              1:outer
     * @return 戻り値は基本 -1
     */
    public int showArmorParts(int parts, int index) {
        return -1;
    }

    /**
     * ハードポイントに接続されたアイテムを表示する
     */
    public abstract void renderItems(IModelCaps pEntityCaps);

    public abstract void renderFirstPersonHand(IModelCaps pEntityCaps);


    // IModelCaps

    @Override
    public Map<String, Integer> getModelCaps() {
        return fcapsmap;
    }

    @Override
    public Object getCapsValue(int pIndex, Object... pArg) {
        switch (pIndex) {
            case caps_onGround:
                return onGrounds;
            case caps_isRiding:
                return isRiding;
            case caps_isSneak:
                return isSneak;
            case caps_isWait:
                return isWait;
            case caps_isChild:
                return isChild;
            case caps_heldItemLeft:
                return heldItem[1];
            case caps_heldItemRight:
                return heldItem[0];
            case caps_aimedBow:
                return aimedBow;
            case caps_entityIdFactor:
                return entityIdFactor;
            case caps_ticksExisted:
                return entityTicksExisted;
            case caps_ScaleFactor:
                return scaleFactor;
            case caps_dominantArm:
                return dominantArm;
            //お座りモーション判定
            case caps_motionSitting:
                return motionSitting;
        }
        return null;
    }

    @Override
    public boolean setCapsValue(int pIndex, Object... pArg) {
        switch (pIndex) {
            case caps_onGround:
                for (int li = 0; li < onGrounds.length && li < pArg.length; li++) {
                    onGrounds[li] = (Float) pArg[li];
                }
                return true;
            case caps_isRiding:
                isRiding = (Boolean) pArg[0];
                return true;
            case caps_isSneak:
                isSneak = (Boolean) pArg[0];
                return true;
            case caps_isWait:
                isWait = (Boolean) pArg[0];
                return true;
            case caps_isChild:
                isChild = (Boolean) pArg[0];
                return true;
            case caps_heldItemLeft:
                if (pArg[0] instanceof Float) {
                    heldItem[1] = (Float) pArg[0];
                } else {
                    heldItem[1] = 0.0F;
                }
                return true;
            case caps_heldItemRight:
                if (pArg[0] instanceof Float) {
                    heldItem[0] = (Float) pArg[0];
                } else {
                    heldItem[0] = 0.0F;
                }
                return true;
            case caps_aimedBow:
                aimedBow = (Boolean) pArg[0];
                return true;
            case caps_entityIdFactor:
                entityIdFactor = (Float) pArg[0];
                return true;
            case caps_ticksExisted:
                entityTicksExisted = (Integer) pArg[0];
                return true;
            case caps_ScaleFactor:
                scaleFactor = (Float) pArg[0];
                return true;
            case caps_dominantArm:
                dominantArm = (Integer) pArg[0];
                return true;
            //お座りモーション判定
            case caps_motionSitting:
                motionSitting = (Boolean) pArg[0];
                return true;
        }

        return false;
    }


    public static float mh_sqrt_float(float f) {
        return MathHelper.sqrt(f);
    }

    public static float mh_sqrt_double(double d) {
        return MathHelper.sqrt(d);
    }

    public static int mh_floor_float(float f) {
        return MathHelper.floor(f);
    }

    public static int mh_floor_double(double d) {
        return MathHelper.floor(d);
    }

    public static long mh_floor_double_long(double d) {
        return MathHelper.floor(d);
    }
}