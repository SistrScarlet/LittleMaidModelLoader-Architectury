package net.sistr.littlemaidmodelloader.maidmodel;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMMatrixStack;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMVertexConsumer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Constructor;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

//描画周りに大幅な仕様変更がなされたため、互換性の喪失を伴う変更を加えた
//できる限りメソッドと変数を維持しているが、機能していないメソッドも多い
//不要または機能不全なメソッドのうち、privateまたはprotectedなメソッドや変数は全部コメントアウトしている
//旧バージョンで既にコメントアウト済みのものは削除している

//GL11.glCallList(displayList);はrenderに置き換えている
//元の仕様ではlistにキャッシュしてこのメソッドで呼び出していた(多分)
//現在の仕様ではIVertexBuilderに頂点を追加するだけで自動的にキャッシュされる(多分)
public class ModelRenderer {

    //15~追加

    //互換性のための変数群
    //MultiModelRenderer -> ModelBaseSolo -> ココ
    //引数の不足を補う
    public static MatrixStack matrixStack;
    public static VertexConsumer buffer;
    public static int packedLight;
    public static int packedOverlay;
    public static float red;
    public static float green;
    public static float blue;
    public static float alpha;

    public static void setParam(MMMatrixStack matrixStack, MMVertexConsumer buffer, int packedLight, int packedOverlay,
                                float red, float green, float blue, float alpha) {
        ModelRenderer.matrixStack = matrixStack.getVanillaMatrixStack();
        ModelRenderer.buffer = buffer.getVanillaVertexConsumer();
        ModelRenderer.packedLight = packedLight;
        ModelRenderer.packedOverlay = packedOverlay;
        ModelRenderer.red = red;
        ModelRenderer.green = green;
        ModelRenderer.blue = blue;
        ModelRenderer.alpha = alpha;

    }

    //15のModelRendererからコピペしたメソッドたち
    public void render(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn) {
        this.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (isHidden) return;
        if (!showModel) return;

        matrixStackIn.push();
        matrixStackIn.translate(offsetX, offsetY, offsetZ);

        matrixStackIn.translate(this.rotationPointX / 16.0F, this.rotationPointY / 16.0F, this.rotationPointZ / 16.0F);
        setRotation();

        matrixStackIn.push();
        matrixStackIn.scale(scaleX, scaleY, scaleZ);
        this.doRender(matrixStackIn.peek(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();

        if (this.childModels != null) {
            for (ModelRenderer modelrenderer : this.childModels) {
                modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }

        matrixStackIn.pop();
    }

    private void doRender(MatrixStack.Entry matrixEntryIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixEntryIn.getModel();
        Matrix3f matrix3f = matrixEntryIn.getNormal();

        for (ModelBoxBase modelBoxBase : this.cubeList) {
            for (ModelBoxBase.TexturedQuad quad : modelBoxBase.quadList) {
                //本来であればnormalはコンストラクタで必ず指定されるため、コピーだけで済むが、
                //互換性のためにnormalを@Nullableにしているため、ここで計算する
                if (quad.normal == null) {
                    Vector3f n1 = quad.vertexPositions[0].position.copy();
                    Vector3f n2 = quad.vertexPositions[2].position.copy();
                    n1.subtract(quad.vertexPositions[1].position);
                    n2.subtract(quad.vertexPositions[1].position);
                    n2.cross(n1);
                    n2.normalize();
                    quad.normal = n2;
                }
                Vector3f normal = quad.normal.copy();
                normal.transform(matrix3f);
                float normalX = normal.getX();
                float normalY = normal.getY();
                float normalZ = normal.getZ();

                for (int i = 0; i < 4; ++i) {
                    ModelBoxBase.PositionTextureVertex vertex = quad.vertexPositions[i];
                    float f3 = vertex.position.getX() / 16.0F;
                    float f4 = vertex.position.getY() / 16.0F;
                    float f5 = vertex.position.getZ() / 16.0F;
                    Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
                    vector4f.transform(matrix4f);
                    bufferIn.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(),
                            red, green, blue, alpha, vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn,
                            normalX, normalY, normalZ);
                }
            }
        }

    }

    //互換性の維持のため、旧renderメソッドを上書きしている。
    //もちろんちゃんと動く
    //GL11.glCallList(displayList);での呼び出し時にも代わりに呼び出す
    public void render(float par1, boolean pIsRender) {
        if (!pIsRender || !isRendering) {
            return;
        }
        this.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(float par1) {
        this.render(par1, true);
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
    //protected boolean compiled;
    //protected int displayList;
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
    //protected ModelBase baseModel;
    public ModelRenderer pearent;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public float scaleX;
    public float scaleY;
    public float scaleZ;

    public static final float radFactor = 180F / (float) Math.PI;
    public static final float degFactor = (float) Math.PI / 180F;

    // SmartMovingに合わせるために名称の変更があるかもしれません。
    public int rotatePriority;
    public static final int RotXYZ = 0;
    public static final int RotXZY = 1;
    public static final int RotYXZ = 2;
    public static final int RotYZX = 3;
    public static final int RotZXY = 4;
    public static final int RotZYX = 5;

    //protected ItemStack itemstack;

    public boolean adjust;
    public FloatBuffer matrix;
    public boolean isInvertX;

    public ModelRenderer(ModelBase pModelBase, String pName) {
        textureWidth = 64.0F;
        textureHeight = 32.0F;
        //compiled = false;
        //displayList = 0;
        mirror = false;
        showModel = true;
        isHidden = false;
        isRendering = true;
        cubeList = new ArrayList<>();
        //baseModel = pModelBase;
        pModelBase.boxList.add(this);
        boxName = pName;
        setTextureSize(pModelBase.textureWidth, pModelBase.textureHeight);

        rotatePriority = RotXYZ;
        //itemstack = ItemStack.EMPTY;
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

    //動作保証なし
    @Deprecated
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

    //動作保証無し
    @Deprecated
    public void renderWithRotation(float par1) {
        if (isHidden) return;
        if (!showModel) return;

        //if (!compiled) {
        //    compileDisplayList(par1);
        //}

        matrixStack.push();
        matrixStack.translate(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);

        setRotation();

        //GL11.glCallList(displayList);
        render(par1);
        matrixStack.pop();
    }

    //動作保証無し
    @Deprecated
    public void postRender(float par1) {
        if (isHidden) return;
        if (!showModel) return;

        //if (!compiled) {
        //    compileDisplayList(par1);
        //}

        if (pearent != null) {
            pearent.postRender(par1);
        }

        matrixStack.translate(offsetX, offsetY, offsetZ);

        if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F) {
            matrixStack.translate(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
        }
        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F) {
            setRotation();
        }
    }

    /*
    protected void compileDisplayList(float par1) {
        displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.getInstance();

        for (ModelBoxBase modelBoxBase : cubeList) {
            modelBoxBase.render(tessellator, par1);
        }

        GL11.glEndList();
        compiled = true;
    }
     */

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

    //動作保証無し
    @Deprecated
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

    //動作保証なし
    @Deprecated
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
        //compiled = false;
        if (childModels != null) {
            childModels.clear();
        }
    }

    //ここら辺のメソッドは、アイテム保持のレイヤー化により元から削除予定だったらしい
    //動作保証無し
    @Deprecated
    public boolean renderItems(ModelMultiBase pModelMulti, IModelCaps pEntityCaps, boolean pRealBlock, int pIndex) {
        //ItemStack[] litemstacks = (ItemStack[]) ModelCapsHelper.getCapsValue(pEntityCaps, caps_Items);
        //if (litemstacks == null) return false;
        //UseAction[] lactions = (UseAction[]) ModelCapsHelper.getCapsValue(pEntityCaps, caps_Actions);
        //LivingEntity lentity = (LivingEntity) pEntityCaps.getCapsValue(caps_Entity);

        //renderItems(lentity, pModelMulti.render, pRealBlock, lactions[pIndex], litemstacks[pIndex]);
        return true;
    }

    //動作保証無し
    @Deprecated
    public void renderItemsHead(ModelMultiBase pModelMulti, IModelCaps pEntityCaps) {
        //ItemStack lis = (ItemStack) pEntityCaps.getCapsValue(caps_HeadMount);
        //LivingEntity lentity = (LivingEntity) pEntityCaps.getCapsValue(caps_Entity);

        //renderItems(lentity, pModelMulti.render, true, null, lis);
    }

    /*
    protected void renderItems(LivingEntity pEntityLiving, EntityRenderer<?> pRender,
                               boolean pRealBlock, UseAction pAction, ItemStack pItemStack) {
        itemstack = pItemStack;
        renderItems(pEntityLiving, pRender, pRealBlock, pAction);
    }

    protected void renderItems(LivingEntity pEntityLiving, EntityRenderer<?> pRender, boolean pRealBlock, UseAction pAction) {
        if (itemstack.isEmpty()) return;

        // アイテムのレンダリング
        GL11.glPushMatrix();
        Item litem = itemstack.getItem();

        // アイテムの種類による表示位置の補正
        if (adjust) {
            // GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

            if (pRealBlock && (litem instanceof BlockItem) && !(litem instanceof SkullItem)) {
                float f2 = 0.625F;
                GL11.glScalef(f2, -f2, -f2);
                GL11.glRotatef(270F, 0F, 1F, 0);
            } else if (pRealBlock && (litem instanceof SkullItem)) {
                float f2 = 1.0625F;
                GL11.glScalef(f2, -f2, -f2);
            } else {
                float var6;
				if (litem instanceof BowItem) {
                    var6 = 0.625F;
                    GL11.glTranslatef(-0.05F, 0.125F, 0.3125F);
                    GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var6, -var6, var6);
                    GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                } else if (litem.isFull3D()) {
                    var6 = 0.625F;

                    if (pAction == UseAction.BLOCK) {
                        GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                    }

                    GL11.glTranslatef(0.0F, 0.1875F, 0.1F);
                    GL11.glScalef(var6, -var6, var6);
                    GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    var6 = 0.375F;
                    GL11.glTranslatef(0.15F, 0.15F, -0.05F);
                    // GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                    GL11.glScalef(var6, var6, var6);
                    GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                }
            }
        }

        // 描画
        if (pRealBlock && litem instanceof SkullItem) {
            String lsowner = "";
            if (itemstack.hasTag() && itemstack.getTag().contains("SkullOwner")) {
                lsowner = itemstack.getTag().getString("SkullOwner");
            }
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            //RendererHelper.renderSkeletonHead(TileEntitySkullRenderer.instance, -0.5F, 0.0F, -0.5F, 1, 180.0F, itemstack.getItemDamage(), lsowner);
        } else if (pRealBlock && litem instanceof BlockItem) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            int var4 = (int) pEntityLiving.getBrightness();
            int var5 = var4 % 65536;
            int var6 = var4 / 65536;
            //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var5 / 1.0F, var6 / 1.0F);
            GL11.glEnable(GL11.GL_CULL_FACE);
            renderBlock(itemstack);

            GL11.glDisable(GL11.GL_CULL_FACE);
        }

        GL11.glPopMatrix();
    }

    @Deprecated
    private void renderBlock(ItemStack par2ItemStack)
    {
    }
    */

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
                    matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                break;
            case RotXZY:
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                break;
            case RotYXZ:
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                break;
            case RotYZX:
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                break;
            case RotZXY:
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                break;
            case RotZYX:
                if (rotateAngleX != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(rotateAngleX));
                }
                if (rotateAngleY != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(rotateAngleY));
                }
                if (rotateAngleZ != 0.0F) {
                    matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(rotateAngleZ));
                }
                break;
        }
    }

    /*
    /**
     * 内部実行用、レンダリング部分。
    protected void renderObject(float par1, boolean pRendering) {
        // レンダリング、あと子供も
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, matrix);
        if (pRendering && isRendering) {
            GL11.glPushMatrix();
            GL11.glScalef(scaleX, scaleY, scaleZ);
            //GL11.glCallList(displayList);
            render(par1, pRendering);
            GL11.glPopMatrix();
        }

        if (childModels != null) {
            for (ModelRenderer childModel : childModels) {
                childModel.render(par1, pRendering);
            }
        }
    }
    */

    /**
     * パーツ描画時点のマトリクスを設定する。 これ以前に設定されたマトリクスは破棄される。
     */
    //よくわかってない故matrixStackへの置き換えすらしていない
    //動作保証なし
    @Deprecated
    public ModelRenderer loadMatrix() {
        GL11.glLoadMatrixf(matrix);
        if (isInvertX) {
            GL11.glScalef(-1F, 1F, 1F);
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