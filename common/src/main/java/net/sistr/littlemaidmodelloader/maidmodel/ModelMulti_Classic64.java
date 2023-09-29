package net.sistr.littlemaidmodelloader.maidmodel;

import net.sistr.littlemaidmodelloader.maidmodel.compat.GLCompat;

public class ModelMulti_Classic64 extends ModelMultiBase {

    public ModelRenderer bipedHead;
    public ModelRenderer bipedHeadwear;
    public ModelRenderer bipedBody;
    public ModelRenderer bipedBodywear;
    public ModelRenderer bipedRightArm;
    public ModelRenderer bipedRightArmwear;
    public ModelRenderer bipedLeftArm;
    public ModelRenderer bipedLeftArmwear;
    public ModelRenderer bipedRightLeg;
    public ModelRenderer bipedRightLegwear;
    public ModelRenderer bipedLeftLeg;
    public ModelRenderer bipedLeftLegwear;
    public ModelRenderer bipedEars;
    public ModelRenderer bipedCloak;
    public ModelRenderer bipedTorso;
    public ModelRenderer bipedNeck;
    public ModelRenderer bipedPelvic;

    public ModelMulti_Classic64() {
        super();
    }

    public ModelMulti_Classic64(float psize) {
        super(psize, 0.0F, 64, 64);
    }

    public ModelMulti_Classic64(float psize, float pyoffset, int pTextureWidth, int pTextureHeight) {
        super(psize, pyoffset, pTextureWidth, pTextureHeight);
    }

    @Override
    public void initModel(float psize, float pyoffset) {
        bipedCloak = new ModelRenderer(this, 0, 0);
        bipedCloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, psize);
        bipedEars = new ModelRenderer(this, 24, 0);
        bipedEars.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, psize);

        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, psize);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHeadwear = new ModelRenderer(this, 32, 0);
        bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, psize + 0.5F);
        bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);

        bipedBody = new ModelRenderer(this, 16, 16);
        bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, psize);
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedBodywear = new ModelRenderer(this, 16, 32);
        bipedBodywear.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, psize + 0.25f);
        bipedBodywear.setRotationPoint(0.0F, 0.0F, 0.0F);

        bipedRightArm = new ModelRenderer(this, 40, 16);
        bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, psize);
        bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        bipedRightArmwear = new ModelRenderer(this, 40, 32);
        bipedRightArmwear.addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, psize + 0.25f);
        bipedRightArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
        bipedLeftArm = new ModelRenderer(this, 32, 48);
        bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, psize);
        bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        bipedLeftArmwear = new ModelRenderer(this, 48, 48);
        bipedLeftArmwear.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, psize + 0.25f);
        bipedLeftArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);

        bipedRightLeg = new ModelRenderer(this, 0, 16);
        bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, psize);
        bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        bipedRightLegwear = new ModelRenderer(this, 0, 32);
        bipedRightLegwear.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, psize + 0.25f);
        bipedRightLegwear.setRotationPoint(0.0f, 0.0f, 0.0f);
        bipedLeftLeg = new ModelRenderer(this, 16, 48);
        bipedLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, psize);
        bipedLeftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        bipedLeftLegwear = new ModelRenderer(this, 0, 48);
        bipedLeftLegwear.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, psize + 0.25f);
        bipedLeftLegwear.setRotationPoint(0.0f, 0.0f, 0.0f);

        HeadMount.setRotationPoint(0.0F, -4.0F, 0.0F);
        HeadTop.setRotationPoint(0.0F, -12.0F, 0.0F);
        Arms[0] = new ModelRenderer(this);
        Arms[0].setRotationPoint(-1.5F, 7.2F, -1F);
        Arms[1] = new ModelRenderer(this);
        Arms[1].setRotationPoint(1.5F, 7.2F, -1F);

        bipedTorso = new ModelRenderer(this);
        bipedNeck = new ModelRenderer(this);
        bipedPelvic = new ModelRenderer(this);

        mainFrame = new ModelRenderer(this);
        mainFrame.setRotationPoint(0F, pyoffset, 0F);
        mainFrame.addChild(bipedTorso);
        bipedTorso.addChild(bipedNeck);
        bipedTorso.addChild(bipedPelvic);
        bipedTorso.addChild(bipedBody);
        bipedBody.addChild(bipedBodywear);
        bipedNeck.addChild(bipedHead);
        bipedHead.addChild(bipedHeadwear);
        bipedHead.addChild(bipedEars);
        bipedHead.addChild(HeadMount);
        bipedHead.addChild(HeadTop);
        bipedNeck.addChild(bipedRightArm);
        bipedRightArm.addChild(bipedRightArmwear);
        bipedNeck.addChild(bipedLeftArm);
        bipedLeftArm.addChild(bipedLeftArmwear);
        bipedPelvic.addChild(bipedRightLeg);
        bipedRightLeg.addChild(bipedRightLegwear);
        bipedPelvic.addChild(bipedLeftLeg);
        bipedLeftLeg.addChild(bipedLeftLegwear);
        bipedRightArm.addChild(Arms[0]);
        bipedLeftArm.addChild(Arms[1]);
        bipedBody.addChild(bipedCloak);

        bipedEars.showModel = false;
        bipedCloak.showModel = false;

    }

    @Override
    public void render(IModelCaps pEntityCaps, float par2, float par3, float ticksExisted,
                       float pheadYaw, float pheadPitch, float par7, boolean pIsRender) {
        setRotationAngles(par2, par3, ticksExisted, pheadYaw, pheadPitch, par7, pEntityCaps);
        mainFrame.render(par7, pIsRender);
    }

    public void setDefaultPause(float par1, float par2, float pTicksExisted,
                                float pHeadYaw, float pHeadPitch, float par6, IModelCaps pEntityCaps) {
        // 初期姿勢
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F).setRotateAngleDeg(pHeadPitch, pHeadYaw, 0.0F);
        bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedTorso.setRotationPoint(0.0F, 0.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedNeck.setRotationPoint(0.0F, 0.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedPelvic.setRotationPoint(0.0F, 0.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
    }

    @Override
    public void setRotationAngles(float par1, float par2, float pTicksExisted,
                                  float pHeadYaw, float pHeadPitch, float par6, IModelCaps pEntityCaps) {
        setDefaultPause(par1, par2, pTicksExisted, pHeadYaw, pHeadPitch, par6, pEntityCaps);

        // 腕ふり、腿上げ
        float lf1 = mh_cos(par1 * 0.6662F);
        float lf2 = mh_cos(par1 * 0.6662F + PI);
        this.bipedRightArm.rotateAngleX = lf2 * 2.0F * par2 * 0.5F;
        this.bipedLeftArm.rotateAngleX = lf1 * 2.0F * par2 * 0.5F;
        this.bipedRightLeg.rotateAngleX = lf1 * 1.4F * par2;
        this.bipedLeftLeg.rotateAngleX = lf2 * 1.4F * par2;


        if (isRiding) {
            bipedRightArm.addRotateAngleDegX(-36.0F);
            bipedLeftArm.addRotateAngleDegX(-36.0F);
            bipedRightLeg.addRotateAngleDegX(-72.0F);
            bipedLeftLeg.addRotateAngleDegX(-72.0F);
            bipedRightLeg.addRotateAngleDegY(18.0F);
            bipedLeftLeg.addRotateAngleDegY(-18.0F);
        }

        if (heldItem[0] > 0) {
            bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F;
            bipedRightArm.addRotateAngleDegX(-18.0F * heldItem[0]);
        }
        if (heldItem[1] > 0) {
            bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F;
            bipedLeftArm.addRotateAngleDegX(-18.0F * heldItem[1]);
        }

        float lf;
        if ((onGrounds[0] > -9990F || onGrounds[1] > -9990F) && !aimedBow) {
            // 腕振り
            lf = (float) Math.PI * 2.0F;
            lf1 = mh_sin(mh_sqrt(onGrounds[0]) * lf);
            lf2 = mh_sin(mh_sqrt(onGrounds[1]) * lf);
            bipedTorso.rotateAngleY = (lf1 - lf2) * 0.2F;

            // R
            if (onGrounds[0] > 0F) {
                lf = 1.0F - onGrounds[0];
                lf *= lf;
                lf *= lf;
                lf = 1.0F - lf;
                lf1 = mh_sin(lf * (float) Math.PI);
                lf2 = mh_sin(onGrounds[0] * (float) Math.PI) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
                bipedRightArm.addRotateAngleX(-lf1 * 1.2F - lf2);
                bipedRightArm.addRotateAngleY(bipedTorso.rotateAngleY * 2.0F);
                bipedRightArm.setRotateAngleZ(mh_sin(onGrounds[0] * 3.141593F) * -0.4F);
            } else {
                bipedRightArm.rotateAngleX += bipedTorso.rotateAngleY;
            }
            // L
            if (onGrounds[1] > 0F) {
                lf = 1.0F - onGrounds[1];
                lf *= lf;
                lf *= lf;
                lf = 1.0F - lf;
                lf1 = mh_sin(lf * (float) Math.PI);
                lf2 = mh_sin(onGrounds[1] * (float) Math.PI) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
                bipedLeftArm.addRotateAngleX(-lf1 * 1.2F - lf2);
                bipedLeftArm.addRotateAngleY(bipedTorso.rotateAngleY * 2.0F);
                bipedLeftArm.setRotateAngleZ(mh_sin(onGrounds[1] * 3.141593F) * 0.4F);
            } else {
                bipedLeftArm.rotateAngleX += bipedTorso.rotateAngleY;
            }
        }

        if (isSneak) {
            // しゃがみ
            bipedBody.rotationPointY = 2.0F;
            bipedTorso.rotateAngleX += 0.5F;
            bipedHead.rotationPointY += 1.0F;
            bipedNeck.rotateAngleX -= 0.5F;
            bipedRightArm.rotateAngleX += 0.4F;
            bipedLeftArm.rotateAngleX += 0.4F;
            bipedRightLeg.rotateAngleX -= 0.5F;
            bipedLeftLeg.rotateAngleX -= 0.5F;
            bipedRightLeg.setRotationPoint(-1.9F, 9.8F, -0.8F);
            bipedLeftLeg.setRotationPoint(1.9F, 9.8F, -0.8F);
            // 高さ調整
            bipedTorso.rotationPointY += 1.2F;
        }

        if (aimedBow) {
            lf1 = 0.0F;
            lf2 = 0.0F;
            bipedRightArm.rotateAngleZ = 0.0F;
            bipedLeftArm.rotateAngleZ = 0.0F;
            lf = 0.1F - lf1 * 0.6F;
            bipedRightArm.rotateAngleY = -lf + bipedHead.rotateAngleY;
            bipedLeftArm.rotateAngleY = lf + bipedHead.rotateAngleY;
            lf = (float) Math.PI * 0.5F;
            bipedRightArm.rotateAngleX = -lf + bipedHead.rotateAngleX;
            bipedLeftArm.rotateAngleX = -lf + bipedHead.rotateAngleX;
            bipedRightArm.rotateAngleX -= lf1 * 1.2F - lf2 * 0.4F;
            bipedLeftArm.rotateAngleX -= lf1 * 1.2F - lf2 * 0.4F;
            if (ModelCapsHelper.getCapsValueInt(pEntityCaps, caps_dominantArm) == 0) {
                bipedLeftArm.rotateAngleY += 0.4F;
            } else {
                bipedRightArm.rotateAngleY += 0.4F;
            }
        }

        // 腕の揺らぎ
        lf = mh_cos(pTicksExisted * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleZ += lf;
        this.bipedLeftArm.rotateAngleZ -= lf;
        lf = mh_sin(pTicksExisted * 0.067F) * 0.05F;
        this.bipedRightArm.rotateAngleX += lf;
        this.bipedLeftArm.rotateAngleX -= lf;

    }

    @Override
    public void renderItems(IModelCaps pEntityCaps) {
        // 手持ちの表示
        //GL11.glPushMatrix();
        GLCompat.glPushMatrix();

        // R
        Arms[0].loadMatrix();
//		GL11.glTranslatef(0F, 0.05F, -0.05F);
        Arms[0].renderItems(this, pEntityCaps, false, 0);
        // L
        Arms[1].loadMatrix();
//		GL11.glTranslatef(0F, 0.05F, -0.05F);
        Arms[1].renderItems(this, pEntityCaps, false, 1);
        // 頭部装飾品
        boolean lplanter = ModelCapsHelper.getCapsValueBoolean(pEntityCaps, caps_isPlanter);
        if (ModelCapsHelper.getCapsValueBoolean(pEntityCaps, caps_isCamouflage) || lplanter) {
            if (lplanter) {
                HeadTop.loadMatrix().renderItemsHead(this, pEntityCaps);
            } else {
                HeadMount.loadMatrix().renderItemsHead(this, pEntityCaps);
            }
        }
        //GL11.glPopMatrix();
        GLCompat.glPopMatrix();
    }

    @Override
    public void renderFirstPersonHand(IModelCaps pEntityCaps) {
        // お手手の描画
        float var2 = 1.0F;
        GLCompat.glColor3f(var2, var2, var2);
        onGrounds[0] = onGrounds[1] = 0.0F;
        setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, pEntityCaps);
        bipedRightArm.render(0.0625F);
    }

    @Override
    public float[] getArmorModelsSize() {
        return new float[]{0.5F, 1.0F};
    }

    @Override
    public float getHeight() {
        return 1.8F;
    }

    @Override
    public float getWidth() {
        return 0.6F;
    }

    @Override
    public float getyOffset() {
        return 1.62F;
    }

    @Override
    public float getMountedYOffset() {
        return 0.6f;
    }

    @Override
    public boolean isItemHolder() {
        return true;
    }

    @Override
    public float getLeashOffset(IModelCaps pEntityCaps) {
        return 0.2F;
    }

    @Override
    public int showArmorParts(int parts, int index) {
        if (index == 0) {
            bipedHead.isRendering = parts == 3;
            bipedHeadwear.isRendering = parts == 3;
            bipedBody.isRendering = parts == 1;
            bipedBodywear.isRendering = parts == 1;
            bipedRightArm.isRendering = parts == 2;
            bipedRightArmwear.isRendering = parts == 2;
            bipedLeftArm.isRendering = parts == 2;
            bipedLeftArmwear.isRendering = parts == 2;
            bipedRightLeg.isRendering = parts == 1;
            bipedRightLegwear.isRendering = parts == 1;
            bipedLeftLeg.isRendering = parts == 1;
            bipedLeftLegwear.isRendering = parts == 1;
        } else {
            bipedHead.isRendering = parts == 3;
            bipedHeadwear.isRendering = parts == 3;
            bipedBody.isRendering = parts == 2;
            bipedBodywear.isRendering = parts == 2;
            bipedRightArm.isRendering = parts == 2;
            bipedRightArmwear.isRendering = parts == 2;
            bipedLeftArm.isRendering = parts == 2;
            bipedLeftArmwear.isRendering = parts == 2;
            bipedRightLeg.isRendering = parts == 0;
            bipedRightLegwear.isRendering = parts == 0;
            bipedLeftLeg.isRendering = parts == 0;
            bipedLeftLegwear.isRendering = parts == 0;
        }
        return -1;
    }

}
