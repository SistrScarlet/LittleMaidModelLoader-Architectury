package net.sistr.littlemaidmodelloader.maidmodel;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMMatrixStack;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMVertexConsumer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Constructor;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelRenderer {

    //15~追加

    public float scale = 0.0625F;

    //互換性のための変数群
    //MultiModelRenderer -> ModelBaseSolo -> ココ
    //引数の不足を補う
    public static MatrixStack matrixStack;
    public static VertexConsumer buffer;
    public static int light;
    public static int overlay;
    public static float red;
    public static float green;
    public static float blue;
    public static float alpha;

    public static void setParam(MMMatrixStack matrixStack, MMVertexConsumer buffer, int light, int overlay,
                                float red, float green, float blue, float alpha) {
        ModelRenderer.matrixStack = matrixStack.getVanillaMatrixStack();
        ModelRenderer.buffer = buffer.getVanillaVertexConsumer();
        ModelRenderer.light = light;
        ModelRenderer.overlay = overlay;
        ModelRenderer.red = red;
        ModelRenderer.green = green;
        ModelRenderer.blue = blue;
        ModelRenderer.alpha = alpha;
    }

    //15~追加ココマデ

    // ModelRenderer互換変数群
    public float textureWidth;
    public float textureHeight;
    private int textureOffsetX;
    private int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    protected boolean compiled;
    protected int displayList;
    public boolean mirror;
    public boolean showModel;
    public boolean isHidden;
    /**
     * パーツの親子関係に左右されずに描画するかを決める。
     * アーマーの表示などに使う。
     */
    public boolean isRendering;
    public List<ModelBoxBase> cubeList;
    public List<ModelRenderer> childModels;
    public final String boxName;
    protected ModelBase baseModel;
    public ModelRenderer pearent;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public float scaleX;
    public float scaleY;
    public float scaleZ;


    public static final float radFactor = 180F / (float) Math.PI;
    public static final float degFactor = (float) Math.PI / 180F;

    public int rotatePriority;
    public static final int RotXYZ = 0;
    public static final int RotXZY = 1;
    public static final int RotYXZ = 2;
    public static final int RotYZX = 3;
    public static final int RotZXY = 4;
    public static final int RotZYX = 5;

    protected ItemStack itemstack;

    public boolean adjust;
    public FloatBuffer matrix;
    public boolean isInvertX;


    public ModelRenderer(ModelBase pModelBase, String pName) {
        textureWidth = 64.0F;
        textureHeight = 32.0F;
        compiled = false;
        displayList = 0;
        mirror = false;
        showModel = true;
        isHidden = false;
        isRendering = true;
        cubeList = new ArrayList<>();
        baseModel = pModelBase;
        pModelBase.boxList.add(this);
        boxName = pName;
        setTextureSize(pModelBase.textureWidth, pModelBase.textureHeight);

        rotatePriority = RotXYZ;
        itemstack = null;
        adjust = true;
        matrix = BufferUtils.createFloatBuffer(16);
        isInvertX = false;

        scaleX = 1.0F;
        scaleY = 1.0F;
        scaleZ = 1.0F;

        pearent = null;
    }

    public ModelRenderer(ModelBase pModelBase, int px, int py) {
        this(pModelBase, null);
        setTextureOffset(px, py);
    }

    public ModelRenderer(ModelBase pModelBase) {
        this(pModelBase, null);
    }

    public ModelRenderer(ModelBase pModelBase, int px, int py, float pScaleX, float pScaleY, float pScaleZ) {
        this(pModelBase, px, py);
        this.scaleX = pScaleX;
        this.scaleY = pScaleY;
        this.scaleZ = pScaleZ;
    }

    public ModelRenderer(ModelBase pModelBase, float pScaleX, float pScaleY, float pScaleZ) {
        this(pModelBase);
        this.scaleX = pScaleX;
        this.scaleY = pScaleY;
        this.scaleZ = pScaleZ;
    }

    // ModelRenderer互換関数群

    public void addChild(ModelRenderer pModelRenderer) {
        if (childModels == null) {
            childModels = new ArrayList<>();
        }
        childModels.add(pModelRenderer);
        pModelRenderer.pearent = this;
    }

    public ModelRenderer setTextureOffset(int pOffsetX, int pOffsetY) {
        textureOffsetX = pOffsetX;
        textureOffsetY = pOffsetY;
        return this;
    }

    public ModelRenderer addBox(String pName, float pX, float pY, float pZ,
                                int pWidth, int pHeight, int pDepth) {
        addParts(ModelBox.class, pName, pX, pY, pZ, pWidth, pHeight, pDepth, 0.0F);
        return this;
    }

    public ModelRenderer addBox(float pX, float pY, float pZ,
                                int pWidth, int pHeight, int pDepth) {
        addParts(ModelBox.class, pX, pY, pZ, pWidth, pHeight, pDepth, 0.0F);
        return this;
    }

    public ModelRenderer addBox(float pX, float pY, float pZ,
                                int pWidth, int pHeight, int pDepth, float pSizeAdjust) {
        addParts(ModelBox.class, pX, pY, pZ, pWidth, pHeight, pDepth, pSizeAdjust);
        return this;
    }

    public ModelRenderer setRotationPoint(float pX, float pY, float pZ) {
        rotationPointX = pX;
        rotationPointY = pY;
        rotationPointZ = pZ;
        return this;
    }

    public void render(float par1, boolean pIsRender) {
        GLCompat.modelRenderer = this;

        if (isHidden) return;
        if (!showModel) return;

        if (!compiled) {
            compileDisplayList(par1);
        }

        GLCompat.glPushMatrix();
        GLCompat.glTranslatef(offsetX, offsetY, offsetZ);

        if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F) {
            GLCompat.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
        }
        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F) {
            setRotation();
        }
        renderObject(par1, pIsRender);
        GLCompat.glPopMatrix();
    }

    public void render(float par1) {
        render(par1, true);
    }

    public void renderWithRotation(float par1) {
        if (isHidden) return;
        if (!showModel) return;

        if (!compiled) {
            compileDisplayList(par1);
        }

        GLCompat.glPushMatrix();
        GLCompat.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);

        setRotation();

        GLCompat.glCallList(displayList);
        GLCompat.glPopMatrix();
    }

    public void postRender(float par1) {
        if (isHidden) return;
        if (!showModel) return;

        if (!compiled) {
            compileDisplayList(par1);
        }

        if (pearent != null) {
            pearent.postRender(par1);
        }

        GLCompat.glTranslatef(offsetX, offsetY, offsetZ);

        if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F) {
            GLCompat.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
        }
        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F) {
            setRotation();
        }
    }

    protected void compileDisplayList(float par1) {
        /*displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        for (int i = 0; i < cubeList.size(); i++) {
            cubeList.get(i).render(tessellator, par1);
        }

        GL11.glEndList();*/
        compiled = true;

        scale = par1;
    }

    public ModelRenderer setTextureSize(int pWidth, int pHeight) {
        textureWidth = pWidth;
        textureHeight = pHeight;
        return this;
    }


    // 独自追加分

    /**
     * ModelBox継承の独自オブジェクト追加用
     */
    public ModelRenderer addCubeList(ModelBoxBase pModelBoxBase) {
        cubeList.add(pModelBoxBase);
        return this;
    }

    protected ModelBoxBase getModelBoxBase(Class<? extends ModelBoxBase> pModelBoxBase, Object... pArg) {
        try {
            Constructor<? extends ModelBoxBase> lconstructor =
                    pModelBoxBase.getConstructor(ModelRenderer.class, Object[].class);
            return lconstructor.newInstance(this, pArg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Object[] getArg(Object... pArg) {
        Object[] lobject = new Object[pArg.length + 2];
        lobject[0] = textureOffsetX;
        lobject[1] = textureOffsetY;
        System.arraycopy(pArg, 0, lobject, 2, pArg.length);
        return lobject;
    }

    public ModelRenderer addParts(Class<? extends ModelBoxBase> pModelBoxBase, String pName, Object... pArg) {
        pName = boxName + "." + pName;
        //TextureOffset ltextureoffset = baseModel.getTextureOffset(pName);
        //setTextureOffset(ltextureoffset.textureOffsetX, ltextureoffset.textureOffsetY);
        addCubeList(getModelBoxBase(pModelBoxBase, getArg(pArg)).setBoxName(pName));
        return this;
    }

    public ModelRenderer addParts(Class<? extends ModelBoxBase> pModelBoxBase, Object... pArg) {
        addCubeList(getModelBoxBase(pModelBoxBase, getArg(pArg)));
        return this;
    }

    /**
     * 自分でテクスチャの座標を指定する時に使います。
     * コンストラクタへそのまま値を渡します。
     */
    public ModelRenderer addPartsTexture(Class<? extends ModelBoxBase> pModelBoxBase, String pName, Object... pArg) {
        pName = boxName + "." + pName;
        addCubeList(getModelBoxBase(pModelBoxBase, pArg).setBoxName(pName));
        return this;
    }

    /**
     * 自分でテクスチャの座標を指定する時に使います。
     * コンストラクタへそのまま値を渡します。
     */
    public ModelRenderer addPartsTexture(Class<? extends ModelBoxBase> pModelBoxBase, Object... pArg) {
        addCubeList(getModelBoxBase(pModelBoxBase, pArg));
        return this;
    }


    public ModelRenderer addPlate(float pX, float pY, float pZ,
                                  int pWidth, int pHeight, int pFacePlane) {
        addParts(ModelPlate.class, pX, pY, pZ, pWidth, pHeight, pFacePlane, 0.0F);
        return this;
    }

    public ModelRenderer addPlate(float pX, float pY, float pZ,
                                  int pWidth, int pHeight, int pFacePlane, float pSizeAdjust) {
        addParts(ModelPlate.class, pX, pY, pZ, pWidth, pHeight, pFacePlane, pSizeAdjust);
        return this;
    }

    public ModelRenderer addPlate(String pName, float pX, float pY, float pZ,
                                  int pWidth, int pHeight, int pFacePlane) {
        addParts(ModelPlate.class, pName, pX, pY, pZ, pWidth, pHeight, pFacePlane, 0.0F);
        return this;
    }

    /**
     * 描画用のボックス、子供をクリアする
     */
    public void clearCubeList() {
        cubeList.clear();
        compiled = false;
        if (childModels != null) {
            childModels.clear();
        }
    }

    //虚無

    public boolean renderItems(ModelMultiBase pModelMulti, IModelCaps pEntityCaps, boolean pRealBlock, int pIndex) {
        return true;
    }

    public void renderItemsHead(ModelMultiBase pModelMulti, IModelCaps pEntityCaps) {
    }

    /**
     * 回転変換を行う順序を指定。
     *
     * @param pValue Rot???を指定する
     */
    public void setRotatePriority(int pValue) {
        rotatePriority = pValue;
    }

    /**
     * 内部実行用、座標変換部
     */
    protected void setRotation() {
        // 変換順位の設定
        switch (rotatePriority) {
            case RotXYZ:
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                break;
            case RotXZY:
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                break;
            case RotYXZ:
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                break;
            case RotYZX:
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                break;
            case RotZXY:
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                break;
            case RotZYX:
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                break;
        }
    }

    /**
     * 内部実行用、レンダリング部分。
     */
    protected void renderObject(float par1, boolean pRendering) {
        // レンダリング、あと子供も
        GLCompat.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrix);
        if (pRendering && isRendering) {
            GLCompat.glPushMatrix();
            GLCompat.glScalef(scaleX, scaleY, scaleZ);
            GLCompat.glCallList(displayList);
            GLCompat.glPopMatrix();
        }

        if (childModels != null) {
            for (ModelRenderer childModel : childModels) {
                childModel.render(par1, pRendering);
            }
        }
    }

    /**
     * パーツ描画時点のマトリクスを設定する。 これ以前に設定されたマトリクスは破棄される。
     */
    public ModelRenderer loadMatrix() {
        GLCompat.glLoadMatrix(matrix);
        if (isInvertX) {
            GLCompat.glScalef(-1F, 1F, 1F);
        }
        return this;
    }


    // ゲッター、セッター

    public boolean getMirror() {
        return mirror;
    }

    public ModelRenderer setMirror(boolean flag) {
        mirror = flag;
        return this;
    }

    public boolean getVisible() {
        return showModel;
    }

    public void setVisible(boolean flag) {
        showModel = flag;
    }

    // Deg付きは角度指定が度数法

    public float getRotateAngleX() {
        return rotateAngleX;
    }

    public float getRotateAngleDegX() {
        return rotateAngleX * radFactor;
    }

    public float setRotateAngleX(float value) {
        return rotateAngleX = value;
    }

    public float setRotateAngleDegX(float value) {
        return rotateAngleX = value * degFactor;
    }

    public float addRotateAngleX(float value) {
        return rotateAngleX += value;
    }

    public float addRotateAngleDegX(float value) {
        return rotateAngleX += value * degFactor;
    }

    public float getRotateAngleY() {
        return rotateAngleY;
    }

    public float getRotateAngleDegY() {
        return rotateAngleY * radFactor;
    }

    public float setRotateAngleY(float value) {
        return rotateAngleY = value;
    }

    public float setRotateAngleDegY(float value) {
        return rotateAngleY = value * degFactor;
    }

    public float addRotateAngleY(float value) {
        return rotateAngleY += value;
    }

    public float addRotateAngleDegY(float value) {
        return rotateAngleY += value * degFactor;
    }

    public float getRotateAngleZ() {
        return rotateAngleZ;
    }

    public float getRotateAngleDegZ() {
        return rotateAngleZ * radFactor;
    }

    public float setRotateAngleZ(float value) {
        return rotateAngleZ = value;
    }

    public float setRotateAngleDegZ(float value) {
        return rotateAngleZ = value * degFactor;
    }

    public float addRotateAngleZ(float value) {
        return rotateAngleZ += value;
    }

    public float addRotateAngleDegZ(float value) {
        return rotateAngleZ += value * degFactor;
    }

    public ModelRenderer setRotateAngle(float x, float y, float z) {
        rotateAngleX = x;
        rotateAngleY = y;
        rotateAngleZ = z;
        return this;
    }

    public ModelRenderer setRotateAngleDeg(float x, float y, float z) {
        rotateAngleX = x * degFactor;
        rotateAngleY = y * degFactor;
        rotateAngleZ = z * degFactor;
        return this;
    }

    public float getRotationPointX() {
        return rotationPointX;
    }

    public float setRotationPointX(float value) {
        return rotationPointX = value;
    }

    public float addRotationPointX(float value) {
        return rotationPointX += value;
    }

    public float getRotationPointY() {
        return rotationPointY;
    }

    public float setRotationPointY(float value) {
        return rotationPointY = value;
    }

    public float addRotationPointY(float value) {
        return rotationPointY += value;
    }

    public float getRotationPointZ() {
        return rotationPointZ;
    }

    public float setRotationPointZ(float value) {
        return rotationPointZ = value;
    }

    public float addRotationPointZ(float value) {
        return rotationPointZ += value;
    }

    public ModelRenderer setScale(float pX, float pY, float pZ) {
        scaleX = pX;
        scaleY = pY;
        scaleZ = pZ;
        return this;
    }

    public float setScaleX(float pValue) {
        return scaleX = pValue;
    }

    public float setScaleY(float pValue) {
        return scaleY = pValue;
    }

    public float setScaleZ(float pValue) {
        return scaleZ = pValue;
    }

}