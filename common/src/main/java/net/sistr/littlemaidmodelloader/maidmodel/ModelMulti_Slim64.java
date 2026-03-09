package net.sistr.littlemaidmodelloader.maidmodel;

public class ModelMulti_Slim64 extends ModelMulti_Classic64 {

    public ModelMulti_Slim64() {
        super();
    }

    public ModelMulti_Slim64(float psize) {
        super(psize);
    }

    public ModelMulti_Slim64(float psize, float pyoffset, int pTextureWidth, int pTextureHeight) {
        super(psize, pyoffset, pTextureWidth, pTextureHeight);
    }

    @Override
    public void initModel(float psize, float pyoffset) {
        super.initModel(psize, pyoffset);

        bipedRightArm = new ModelRenderer(this, 40, 16);
        bipedRightArm.addBox(-2.0f, -2.0f, -2.0f, 3, 12, 4, psize);
        bipedRightArm.setRotationPoint(-5.0f, 2.5f, 0.0f);
        bipedRightArmwear = new ModelRenderer(this, 40, 32);
        bipedRightArmwear.addBox(-2.0f, -2.0f, -2.0f, 3, 12, 4, psize + 0.25f);
        bipedRightArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
        bipedLeftArm = new ModelRenderer(this, 32, 48);
        bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 3, 12, 4, psize);
        bipedLeftArm.setRotationPoint(5.0f, 2.5f, 0.0f);
        bipedLeftArmwear = new ModelRenderer(this, 48, 48);
        bipedLeftArmwear.addBox(-1.0f, -2.0f, -2.0f, 3, 12, 4, psize + 0.25f);
        bipedLeftArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);

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
    }
}
