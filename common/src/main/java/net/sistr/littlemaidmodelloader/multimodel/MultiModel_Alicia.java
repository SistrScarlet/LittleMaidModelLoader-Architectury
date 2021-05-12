// Made with Blockbench 3.8.3
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

package net.sistr.littlemaidmodelloader.multimodel;

import com.google.common.collect.Lists;
import net.sistr.littlemaidmodelloader.maidmodel.IModelCaps;
import net.sistr.littlemaidmodelloader.multimodel.animation.*;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMMatrixStack;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMPose;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMRenderContext;
import net.sistr.littlemaidmodelloader.multimodel.model.ModelPart;
import net.sistr.littlemaidmodelloader.multimodel.model.SmoothModelPartDelegate2;

import java.util.List;

public class MultiModel_Alicia implements IMultiModel {
    private final ModelPart body;
    private final ModelPart upperBody;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart headWearT;
    private final ModelPart headWearTM;
    private final ModelPart headWearM;
    private final ModelPart headWearBM;
    private final ModelPart headWearB;
    private final ModelPart headWearBB;
    private final ModelPart bellyU;
    private final ModelPart chest;
    private final ModelPart upperArmL;
    private final ModelPart lowerArmL;
    private final ModelPart upperArmR;
    private final ModelPart lowerArmR;
    private final ModelPart lowerBody;
    private final ModelPart bellyL;
    private final ModelPart hip;
    private final ModelPart legL;
    private final ModelPart upperLegL;
    private final ModelPart upperSkirtL;
    private final ModelPart lowerLegL;
    private final ModelPart lowerSkirtL;
    private final ModelPart legR;
    private final ModelPart upperLegR;
    private final ModelPart upperSkirtR;
    private final ModelPart lowerLegR;
    private final ModelPart lowerSkirtR;
    private final FourTwoLerp lerpTM;
    private final FourTwoLerp lerpM;
    private final FourTwoLerp lerpBM;
    private final FourTwoLerp lerpB;
    private final ModelAnimation wait;
    private final ModelAnimation walk;
    private final ModelAnimation sprint;
    private final ModelAnimation fall;
    private final ModelAnimation sneak;
    private final ModelAnimation swim;
    private final List<ModelPart> parts;

    public MultiModel_Alicia() {
        body = new ModelPart(this);
        body.setPivot(0.0F, 24.0F, -0.75F);


        upperBody = new ModelPart(this);
        upperBody.setPivot(0.0F, -28.25F, 1.25F);
        body.addChild(upperBody);


        neck = new ModelPart(this);
        neck.setPivot(0.0F, -10.25F, -0.6F);
        upperBody.addChild(neck);
        neck.setTextureOffset(24, 3).addCuboid(-1.5F, -1.3F, -1.5F, 3.0F, 2.0F, 3.0F, 0.0F, false);

        head = new ModelPart(this);
        head.setPivot(0.0F, -0.65F, 0.1F);
        neck.addChild(head);
        head.setTextureOffset(0, 0).addCuboid(-4.0F, -7.6F, -4.0F, 8.0F, 8.0F, 8.0F, -0.25F, false);

        headWearT = new SmoothModelPartDelegate2(this);
        headWearT.setPivot(0.0F, -7.6F, 0.0F);
        head.addChild(headWearT);
        headWearT.setTextureOffset(96, 0).addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        headWearTM = new SmoothModelPartDelegate2(this);
        headWearTM.setPivot(0.0F, 8.0F, 0.0F);
        headWearT.addChild(headWearTM);
        headWearTM.setTextureOffset(96, 16).addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        headWearM = new SmoothModelPartDelegate2(this);
        headWearM.setPivot(0.0F, 8.0F, 0.0F);
        headWearTM.addChild(headWearM);
        headWearM.setTextureOffset(96, 32).addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        headWearBM = new SmoothModelPartDelegate2(this);
        headWearBM.setPivot(0.0F, 8.0F, 0.0F);
        headWearM.addChild(headWearBM);
        headWearBM.setTextureOffset(96, 48).addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        headWearB = new SmoothModelPartDelegate2(this);
        headWearB.setPivot(0.0F, 8.0F, 0.0F);
        headWearBM.addChild(headWearB);
        headWearB.setTextureOffset(64, 0).addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        headWearBB = new SmoothModelPartDelegate2(this);
        headWearBB.setPivot(0.0F, 8.0F, 0.0F);
        headWearB.addChild(headWearBB);
        headWearBB.setTextureOffset(64, 16).addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 4.0F, 8.0F, 0.0F, false);

        bellyU = new ModelPart(this);
        bellyU.setPivot(0.0F, -4.5F, -3.0F);
        upperBody.addChild(bellyU);
        bellyU.setTextureOffset(22, 29).addCuboid(-3.0F, -1.15F, -0.25F, 6.0F, 6.0F, 4.0F, -0.25F, false);

        chest = new ModelPart(this);
        chest.setPivot(0.0F, 0.0F, -0.5F);
        bellyU.addChild(chest);
        setRotationAngle(chest, -0.0873F, 0.0F, 0.0F);
        chest.setTextureOffset(0, 29).addCuboid(-3.5F, -6.0F, 0.0F, 7.0F, 6.0F, 4.0F, 0.0F, false);

        upperArmL = new ModelPart(this);
        upperArmL.setPivot(3.25F, -4.25F, 2.75F);
        chest.addChild(upperArmL);
        setRotationAngle(upperArmL, 0.0873F, 0.0F, -0.0873F);
        upperArmL.setTextureOffset(0, 16).addCuboid(0.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, 0.0F, false);

        lowerArmL = new ModelPart(this);
        lowerArmL.setPivot(1.0F, 8.5F, 0.0F);
        upperArmL.addChild(lowerArmL);
        setRotationAngle(lowerArmL, -0.0436F, 0.0F, -0.0436F);
        lowerArmL.setTextureOffset(20, 16).addCuboid(-1.0F, -0.5F, -1.5F, 2.0F, 10.0F, 3.0F, 0.0F, false);

        upperArmR = new ModelPart(this);
        upperArmR.setPivot(-3.25F, -4.25F, 2.75F);
        chest.addChild(upperArmR);
        setRotationAngle(upperArmR, 0.0873F, 0.0F, 0.0873F);
        upperArmR.setTextureOffset(10, 16).addCuboid(-2.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, 0.0F, false);

        lowerArmR = new ModelPart(this);
        lowerArmR.setPivot(-1.0F, 8.5F, 0.0F);
        upperArmR.addChild(lowerArmR);
        setRotationAngle(lowerArmR, -0.0436F, 0.0F, 0.0436F);
        lowerArmR.setTextureOffset(30, 16).addCuboid(-1.0F, -0.5F, -1.5F, 2.0F, 10.0F, 3.0F, 0.0F, false);

        lowerBody = new ModelPart(this);
        lowerBody.setPivot(0.0F, -28.25F, 1.25F);
        body.addChild(lowerBody);


        bellyL = new ModelPart(this);
        bellyL.setPivot(0.0F, 0.0F, -3.15F);
        lowerBody.addChild(bellyL);
        setRotationAngle(bellyL, 0.0218F, 0.0F, 0.0F);
        bellyL.setTextureOffset(24, 39).addCuboid(-3.5F, 0.0F, 0.0F, 7.0F, 2.0F, 4.0F, 0.0F, false);

        hip = new ModelPart(this);
        hip.setPivot(0.0F, 2.0F, 2.0F);
        bellyL.addChild(hip);
        hip.setTextureOffset(0, 41).addCuboid(-4.0F, 0.25F, -1.75F, 8.0F, 4.0F, 4.0F, 0.25F, false);

        legL = new ModelPart(this);
        legL.setPivot(0.0F, 4.25F, -0.9F);
        lowerBody.addChild(legL);
        setRotationAngle(legL, 0.0F, 0.0F, 0.0436F);


        upperLegL = new ModelPart(this);
        upperLegL.setPivot(2.25F, 1.0F, 0.5F);
        legL.addChild(upperLegL);
        upperLegL.setTextureOffset(0, 49).addCuboid(-1.5F, -1.0F, -2.0F, 3.0F, 11.0F, 4.0F, 0.25F, false);

        upperSkirtL = new ModelPart(this);
        upperSkirtL.setPivot(0.0F, 0.0F, 0.0F);
        upperLegL.addChild(upperSkirtL);
        upperSkirtL.setTextureOffset(56, 31).addCuboid(-1.5F, -1.0F, -2.0F, 3.0F, 11.0F, 4.0F, 0.75F, false);

        lowerLegL = new ModelPart(this);
        lowerLegL.setPivot(2.25F, 10.75F, 0.5F);
        legL.addChild(lowerLegL);
        setRotationAngle(lowerLegL, 0.0436F, 0.0F, -0.0873F);
        lowerLegL.setTextureOffset(28, 46).addCuboid(-1.5F, -0.5F, -2.5F, 3.0F, 14.0F, 4.0F, 0.0F, false);

        lowerSkirtL = new ModelPart(this);
        lowerSkirtL.setPivot(0.0F, 0.0F, 0.0F);
        lowerLegL.addChild(lowerSkirtL);
        lowerSkirtL.setTextureOffset(56, 46).addCuboid(-1.5F, -0.5F, -2.5F, 3.0F, 14.0F, 4.0F, 0.75F, false);

        legR = new ModelPart(this);
        legR.setPivot(-2.25F, 5.25F, -0.4F);
        lowerBody.addChild(legR);
        setRotationAngle(legR, -0.1309F, 0.0F, -0.0436F);


        upperLegR = new ModelPart(this);
        upperLegR.setPivot(0.0F, 0.0F, 0.0F);
        legR.addChild(upperLegR);
        upperLegR.setTextureOffset(14, 49).addCuboid(-1.5F, -1.0F, -2.0F, 3.0F, 11.0F, 4.0F, 0.25F, false);

        upperSkirtR = new ModelPart(this);
        upperSkirtR.setPivot(4.5F, 0.0F, 0.0F);
        upperLegR.addChild(upperSkirtR);
        upperSkirtR.setTextureOffset(70, 31).addCuboid(-6.0F, -1.0F, -2.0F, 3.0F, 11.0F, 4.0F, 0.75F, false);

        lowerLegR = new ModelPart(this);
        lowerLegR.setPivot(0.0F, 9.75F, 0.0F);
        legR.addChild(lowerLegR);
        setRotationAngle(lowerLegR, 0.1309F, 0.0F, 0.0873F);
        lowerLegR.setTextureOffset(42, 46).addCuboid(-1.5F, -0.5F, -2.5F, 3.0F, 14.0F, 4.0F, 0.0F, false);

        lowerSkirtR = new ModelPart(this);
        lowerSkirtR.setPivot(4.5F, 0.0F, 0.0F);
        lowerLegR.addChild(lowerSkirtR);
        lowerSkirtR.setTextureOffset(70, 46).addCuboid(-6.0F, -0.5F, -2.5F, 3.0F, 14.0F, 4.0F, 0.75F, false);

        Bezier inOut = new Bezier(0.3F, 0.0F, 0.7F, 1.0F);

        wait = createWaitAnimationBuilder(60, inOut).build();
        walk = createWalkAnimationBuilder(14, inOut).build();
        sprint = createSprintAnimationBuilder(12, inOut).build();
        fall = createFallAnimationBuilder(60, inOut).build();
        sneak = createSneakAnimationBuilder(60, inOut).build();
        swim = createSwimAnimationBuilder(60, inOut).build();

        lerpTM = new FourTwoLerp(
                new Angle(15, 15, 2.5F),
                new Angle(25, 0, 0), new Angle(-90, 0, 0),
                new Angle(30, 20, 10),
                new Angle(-80, 0, -15));
        lerpM = new FourTwoLerp(
                new Angle(-15, 10, -2.5F),
                new Angle(5, 0, 0), new Angle(-5, 0, 0),
                new Angle(5, 10, 7.5F),
                new Angle(-10, 15, 0));
        lerpBM = new FourTwoLerp(
                new Angle(0, 10, 10F),
                new Angle(-7.5F, 0, 0), new Angle(10, 0, 0),
                new Angle(-10, 5, -2.5F),
                new Angle(-2.5F, 15, 0));
        lerpB = new FourTwoLerp(
                new Angle(0, 5, -10F),
                new Angle(7.5F, 0, 0), new Angle(-5, 0, 0),
                new Angle(7.5F, -5, 0),
                new Angle(7.5F, 5, 0));

        parts = Lists.newArrayList(
                body, upperBody,
                neck, head,
                headWearT, headWearTM, headWearM, headWearBM, headWearB, headWearBB,
                upperArmL, lowerArmL,
                upperArmR, lowerArmR,
                lowerBody,
                legL, lowerLegL,
                legR, lowerLegR);
    }

    private static void setRotationAngle(ModelPart bone, float pitch, float yaw, float roll) {
        bone.setRotation(pitch, yaw, roll);
    }

    protected ModelAnimation.Builder createWaitAnimationBuilder(float time, Interpolator i) {
        float half = time / 2F;
        return new ModelAnimation.Builder(time)
                .noMove(0.0F, 0.0F, 0.0F, body)
                .first(0.0F, 0.0F, 0.0F, upperBody, i)
                .noMove(0.0F, 0.0F, 0.0F, neck)
                .noMove(0.0F, 0.0F, 0.0F, head)
                .noMove(0F, 0, 0, headWearT)
                .first(0F, -2.5F, 2.5F, headWearTM, i)
                .first(0F, 2.5F, -2.5F, headWearM, i)
                .first(0F, -2.5F, 2.5F, headWearBM, i)
                .first(0F, 2.5F, -2.5F, headWearB, i)
                .first(2.5F, -2.5F, 2.5F, headWearBB, i)
                .first(5, 0, -5, upperArmL, i)
                .noMove(-2.5F, 0, -2.5F, lowerArmL)
                .first(5, 0, 5, upperArmR, i)
                .noMove(-2.5F, 0, 2.5F, lowerArmR)
                .noMove(0.0F, 0.0F, 0.0F, lowerBody)
                .noMove(0.0F, 0.0F, 2.5F, legL)
                .noMove(2.5F, 0.0F, -5.0F, lowerLegL)
                .noMove(-7.5F, 0.0F, -2.5F, legR)
                .noMove(7.5F, 0.0F, 5.0F, lowerLegR)

                .add(half, 0.0F + 1F, 0.0F, 0.0F, upperBody, i)
                .add(half, 1, -2.5F - 1, 2.5F + 1, headWearTM, i)
                .add(half, 1, 2.5F + 1, -2.5F - 1, headWearM, i)
                .add(half, 1, -2.5F - 1, 2.5F + 1, headWearBM, i)
                .add(half, 1, 2.5F + 1, -2.5F - 1, headWearB, i)
                .add(half, 2.5F, -2.5F - 1, 2.5F + 1, headWearBB, i)
                .add(half, 5 - 1F, 0, -5 - 1F, upperArmL, i)
                .add(half, 5 - 1F, 0, 5 + 1F, upperArmR, i);
    }

    protected ModelAnimation.Builder createWalkAnimationBuilder(int time, Interpolator i) {
        float a = time / 4F;
        float b = a * 2F;
        float c = a * 3F;
        return new ModelAnimation.Builder(time)
                .noMove(0.0F, 0.0F, 0.0F, body)
                .noMove(0.0F, 0.0F, 0.0F, neck)
                .noMove(0.0F, 0.0F, 0.0F, head)
                .noMove(0F, 2.5F, -2.5F, headWearT)
                .noMove(0F, -2.5F, 2.5F, headWearTM)
                .noMove(0F, 2.5F, -2.5F, headWearM)
                .noMove(0F, -2.5F, 2.5F, headWearBM)
                .noMove(0F, 2.5F, -2.5F, headWearB)
                .noMove(2.5F, -2.5F, 2.5F, headWearBB)
                .first(0.0F, 5.0F, 0.0F, upperBody, i)
                .first(-20, 0, -5, upperArmL, i)
                .first(-10.0F, 0, -2.5F, lowerArmL, i)
                .first(20, 0, 5, upperArmR, i)
                .first(0.0F, 0, 2.5F, lowerArmR, i)
                .first(0.0F, -5.0F, 0F, lowerBody, i)
                .first(20.0F, 0.0F, 2.5F, legL, i)
                .first(7.5F, 0.0F, -5.0F, lowerLegL, i)
                .first(-20.0F, 0.0F, -2.5F, legR, i)
                .first(2.5F, 0.0F, 5.0F, lowerLegR, i)

                .add(a, 0.0F, 2.5F, 0.0F, upperBody)
                .add(a, 10.0F, 0, -5, upperArmL)
                .add(a, -2.5F, 0, -2.5F, lowerArmL)
                .add(a, 2.5F, 0, 5, upperArmR)
                .add(a, -2.5F, 0, 2.5F, lowerArmR)
                .add(a, 0.0F, -2.5F, -2.5F, lowerBody)
                .add(a, -10, 0.0F, 2.5F, legL)
                .add(a, 25F, 0.0F, -5.0F, lowerLegL)
                .add(a, 10.0F, 0.0F, -2.5F, legR)
                .add(a, 0F, 0.0F, 5.0F, lowerLegR)

                .add(b, 0.0F, -5.0F, 0.0F, upperBody, i)
                .add(b, 20, 0, -5, upperArmL, i)
                .add(b, 0.0F, 0, -2.5F, lowerArmL, i)
                .add(b, -20, 0, 5, upperArmR, i)
                .add(b, -10.0F, 0, 2.5F, lowerArmR, i)
                .add(b, 0.0F, 5.0F, 0F, lowerBody, i)
                .add(b, -20.0F, 0.0F, 2.5F, legL, i)
                .add(b, 2.5F, 0.0F, -5.0F, lowerLegL, i)
                .add(b, 20.0F, 0.0F, -2.5F, legR, i)
                .add(b, 7.5F, 0.0F, 5.0F, lowerLegR, i)

                .add(c, 0.0F, -2.5F, 0.0F, upperBody)
                .add(c, 2.5F, 0, -5, upperArmL)
                .add(c, -2.5F, 0, -2.5F, lowerArmL)
                .add(c, 10.0F, 0, 5, upperArmR)
                .add(c, -2.5F, 0, 2.5F, lowerArmR)
                .add(c, 0.0F, 2.5F, -2.5F, lowerBody)
                .add(c, 10.0F, 0.0F, 2.5F, legL)
                .add(c, 0F, 0.0F, -5.0F, lowerLegL)
                .add(c, -10, 0.0F, -2.5F, legR)
                .add(c, 25F, 0.0F, 5.0F, lowerLegR);
    }

    protected ModelAnimation.Builder createSprintAnimationBuilder(float time, Interpolator i) {
        float a = 3.5F / 12F * time;
        float b = 2.5F / 12F * time;
        float one = a;
        float two = a + b;
        float three = a + b + a;
        return new ModelAnimation.Builder(time)
                .noMove(0.0F, 0.0F, 0.0F, body)
                .noMove(0.0F, 0.0F, 0.0F, neck)
                .noMove(0.0F, 0.0F, 0.0F, head)
                .noMove(0F, 2.5F, -2.5F, headWearT)
                .noMove(0F, -2.5F, 2.5F, headWearTM)
                .noMove(0F, 2.5F, -2.5F, headWearM)
                .noMove(0F, -2.5F, 2.5F, headWearBM)
                .noMove(0F, 2.5F, -2.5F, headWearB)
                .noMove(2.5F, -2.5F, 2.5F, headWearBB)
                .first(15.0F, -10.0F, 0.0F, upperBody, i)
                .first(60, 0, -5, upperArmL, i)
                .first(-20F, 0, -2.5F, lowerArmL, i)
                .first(-60, 0, 5, upperArmR, i)
                .first(-75F, 0, 2.5F, lowerArmR, i)
                .first(10.0F, 10.0F, 0.0F, lowerBody, i)
                .first(-75.0F, 0.0F, 2.5F, legL, i)
                .first(90F, 0.0F, -5.0F, lowerLegL, i)
                .first(30F, 0.0F, -2.5F, legR, i)
                .first(40F, 0.0F, 5.0F, lowerLegR, i)

                .add(one, 15.0F, -5.0F, 0.0F, upperBody, i)
                .add(one, 30, 0, -5, upperArmL, i)
                .add(one, -40F, 0, -2.5F, lowerArmL, i)
                .add(one, -15, 0, 5, upperArmR, i)
                .add(one, -30F, 0, 2.5F, lowerArmR, i)
                .add(one, 10.0F, 5.0F, 0.0F, lowerBody, i)
                .add(one, -30.0F, 0.0F, 2.5F, legL, i)
                .add(one, 30F, 0.0F, -5.0F, lowerLegL, i)
                .add(one, 0F, 0.0F, -2.5F, legR, i)
                .add(one, 40F, 0.0F, 5.0F, lowerLegR, i)

                .add(two, 15.0F, 10.0F, 0.0F, upperBody, i)
                .add(two, -60, 0, -5, upperArmL, i)
                .add(two, -75F, 0, -2.5F, lowerArmL, i)
                .add(two, 60, 0, 5, upperArmR, i)
                .add(two, -20F, 0, 2.5F, lowerArmR, i)
                .add(two, 10.0F, -10.0F, 0.0F, lowerBody, i)
                .add(two, 30F, 0.0F, 2.5F, legL, i)
                .add(two, 40F, 0.0F, -5.0F, lowerLegL, i)
                .add(two, -75.0F, 0.0F, -2.5F, legR, i)
                .add(two, 90F, 0.0F, 5.0F, lowerLegR, i)

                .add(three, 15.0F, 5.0F, 0.0F, upperBody, i)
                .add(three, -15, 0, -5, upperArmL, i)
                .add(three, -30F, 0, -2.5F, lowerArmL, i)
                .add(three, 30, 0, 5, upperArmR, i)
                .add(three, -40F, 0, 2.5F, lowerArmR, i)
                .add(three, 10.0F, -5.0F, 0.0F, lowerBody, i)
                .add(three, 0F, 0.0F, 2.5F, legL, i)
                .add(three, 40F, 0.0F, -5.0F, lowerLegL, i)
                .add(three, -30.0F, 0.0F, -2.5F, legR, i)
                .add(three, 30F, 0.0F, 5.0F, lowerLegR, i);
    }

    protected ModelAnimation.Builder createFallAnimationBuilder(int time, Interpolator i) {
        return new ModelAnimation.Builder(time * 2)
                .noMove(0.0F, 0.0F, 0.0F, body)
                .first(20.0F, 0.0F, 0.0F, upperBody, i)
                .noMove(0.0F, 0.0F, 0.0F, neck)
                .noMove(0.0F, 0.0F, 0.0F, head)
                .noMove(0F, 0, 0, headWearT)
                .first(0F, -2.5F, 2.5F, headWearTM, i)
                .first(0F, 2.5F, -2.5F, headWearM, i)
                .first(0F, -2.5F, 2.5F, headWearBM, i)
                .first(0F, 2.5F, -2.5F, headWearB, i)
                .first(2.5F, -2.5F, 2.5F, headWearBB, i)
                .first(-15F, 0, -20F, upperArmL, i)
                .noMove(-35F, 0, -2.5F, lowerArmL)
                .first(-12.5F, 0, 25, upperArmR, i)
                .noMove(-30F, 0, 2.5F, lowerArmR)
                .noMove(5F, 0.0F, 0.0F, lowerBody)
                .noMove(-40.0F, 0.0F, 2.5F, legL)
                .noMove(47.5F, 0.0F, -5.0F, lowerLegL)
                .noMove(-60F, 0.0F, -2.5F, legR)
                .noMove(87.5F, 0.0F, 5.0F, lowerLegR)

                .add(time, 20.0F, 0.0F, 0.0F, upperBody, i)
                .add(time, 1, -2.5F - 1, 2.5F + 1, headWearTM, i)
                .add(time, 1, 2.5F + 1, -2.5F - 1, headWearM, i)
                .add(time, 1, -2.5F - 1, 2.5F + 1, headWearBM, i)
                .add(time, 1, 2.5F + 1, -2.5F - 1, headWearB, i)
                .add(time, 2.5F, -2.5F - 1, 2.5F + 1, headWearBB, i)
                .add(time, -15F, 0, -20F, upperArmL, i)
                .add(time, -12.5F, 0, 25, upperArmR, i);
    }

    private ModelAnimation.Builder createSneakAnimationBuilder(int time, Interpolator i) {
        float half = time / 2F;
        return new ModelAnimation.Builder(time)
                .noMove(0.0F, 0.0F, 0.0F, body)
                .first(15.0F, 0.0F, 0.0F, upperBody, i)
                .noMove(0.0F, 0.0F, 0.0F, neck)
                .noMove(-15.0F, 0.0F, 0.0F, head)
                .noMove(0F, 0, 0, headWearT)
                .first(0F, -2.5F, 2.5F, headWearTM, i)
                .first(0F, 2.5F, -2.5F, headWearM, i)
                .first(0F, -2.5F, 2.5F, headWearBM, i)
                .first(0F, 2.5F, -2.5F, headWearB, i)
                .first(2.5F, -2.5F, 2.5F, headWearBB, i)
                //.first(-15, -10, -7.5F, upperArmL, i)
                .noMove(-50, -20, 0, upperArmL)
                .noMove(-40, 45, 17.5F, lowerArmL)
                .noMove(-10, 0, 10, upperArmR)
                .noMove(-5, 0, -2.5F, lowerArmR)
                .noMove(7.5F, 0.0F, 0.0F, lowerBody)
                .noMove(-120.0F, 0.0F, 2.5F, legL)
                .noMove(115F, 0.0F, -5.0F, lowerLegL)
                .noMove(-100F, 0.0F, -2.5F, legR)
                .noMove(145F, 0.0F, 5.0F, lowerLegR)

                .add(half, 15.0F + 1F, 0.0F, 0.0F, upperBody, i)
                .add(half, 1, -2.5F - 1, 2.5F + 1, headWearTM, i)
                .add(half, 1, 2.5F + 1, -2.5F - 1, headWearM, i)
                .add(half, 1, -2.5F - 1, 2.5F + 1, headWearBM, i)
                .add(half, 1, 2.5F + 1, -2.5F - 1, headWearB, i)
                .add(half, 2.5F, -2.5F - 1, 2.5F + 1, headWearBB, i);
    }

    private ModelAnimation.Builder createSneakWalkAnimationBuilder(int time, Interpolator i) {
        float half = time / 2F;
        return new ModelAnimation.Builder(time)
                .noMove(0.0F, 0.0F, 0.0F, body)
                .first(25.0F, 0.0F, 0.0F, upperBody, i)
                .noMove(0.0F, 0.0F, 0.0F, neck)
                .noMove(-15.0F, 0.0F, 0.0F, head)
                .noMove(0F, 0, 0, headWearT)
                .first(0F, -2.5F, 2.5F, headWearTM, i)
                .first(0F, 2.5F, -2.5F, headWearM, i)
                .first(0F, -2.5F, 2.5F, headWearBM, i)
                .first(0F, 2.5F, -2.5F, headWearB, i)
                .first(2.5F, -2.5F, 2.5F, headWearBB, i)
                .first(-10, 0, -10, upperArmL, i)
                .first(-10, 0, 2.5F, lowerArmL, i)
                .first(-10, 0, 10, upperArmR, i)
                .first(-10, 0, -2.5F, lowerArmR, i)
                .first(7.5F, 0.0F, 0.0F, lowerBody, i)
                .first(-120.0F, 0.0F, 2.5F, legL, i)
                .first(115F, 0.0F, -5.0F, lowerLegL, i)
                .first(-100F, 0.0F, -2.5F, legR, i)
                .first(145F, 0.0F, 5.0F, lowerLegR, i)

                .add(half, 25.0F, 0.0F, 0.0F, upperBody, i)
                .add(half, 1, -2.5F - 1, 2.5F + 1, headWearTM, i)
                .add(half, 1, 2.5F + 1, -2.5F - 1, headWearM, i)
                .add(half, 1, -2.5F - 1, 2.5F + 1, headWearBM, i)
                .add(half, 1, 2.5F + 1, -2.5F - 1, headWearB, i)
                .add(half, 2.5F, -2.5F - 1, 2.5F + 1, headWearBB, i)
                .add(half, -10, 0, -10, upperArmL, i)
                .add(half, -10, 0, 2.5F, lowerArmL, i)
                .add(half, -10, 0, 10, upperArmR, i)
                .add(half, -10, 0, -2.5F, lowerArmR, i)
                .add(half, 7.5F, 0.0F, 0.0F, lowerBody, i)
                .add(half, -100F, 0.0F, -2.5F, legL, i)
                .add(half, 145F, 0.0F, 5.0F, lowerLegL, i)
                .add(half, -120.0F, 0.0F, 2.5F, legR, i)
                .add(half, 115F, 0.0F, -5.0F, lowerLegR, i);
    }

    private ModelAnimation.Builder createSwimAnimationBuilder(float time, Interpolator i) {
        float oneQuarter = time / 4F;
        float twoQuarter = oneQuarter * 2F;
        float threeQuarter = oneQuarter * 3F;
        return new ModelAnimation.Builder(time)
                .noMove(0.0F, 0.0F, 0.0F, body)
                .first(0.0F, -10.0F, 0.0F, upperBody, i)
                .noMove(0.0F, 0.0F, 0.0F, neck)
                .noMove(0.0F, 0.0F, 0.0F, head)
                .noMove(0F, 0, 0, headWearT)
                .first(0F, -2.5F, 2.5F, headWearTM, i)
                .first(0F, 2.5F, -2.5F, headWearM, i)
                .first(0F, -2.5F, 2.5F, headWearBM, i)
                .first(0F, 2.5F, -2.5F, headWearB, i)
                .first(2.5F, -2.5F, 2.5F, headWearBB, i)
                .first(5, 0, -5, upperArmL, i)
                .noMove(-2.5F, 0, -2.5F, lowerArmL)
                .first(5, 0, 5, upperArmR, i)
                .noMove(-2.5F, 0, 2.5F, lowerArmR)
                .noMove(0.0F, 10.0F, 0.0F, lowerBody)
                .noMove(-10F, 0.0F, 2.5F, legL)
                .noMove(0F, 0.0F, -5.0F, lowerLegL)
                .noMove(10F, 0.0F, -2.5F, legR)
                .noMove(0F, 0.0F, 5.0F, lowerLegR)

                .add(twoQuarter, 0.0F, 10F, 0.0F, upperBody, i)
                .add(twoQuarter, 0F, -10F, 0.0F, lowerBody, i)
                .add(twoQuarter, 10.0F, 0F, 2.5F, legL, i)
                .add(twoQuarter, -10.0F, 0F, -2.5F, legR, i);
    }

    @Override
    public void setupTransform(IModelCaps caps, MMMatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        float leaning = IMultiModel.lerp(tickDelta,
                (float) caps.getCapsValue(IModelCaps.caps_lastLeaningPitch),
                (float) caps.getCapsValue(IModelCaps.caps_leaningPitch));
        matrices.rotateXDeg(leaning * -90F);
        if (0 < leaning) {
            float pitch = IMultiModel.lerp(tickDelta,
                    (float) caps.getCapsValue(IModelCaps.caps_rotationPitch),
                    (float) caps.getCapsValue(IModelCaps.caps_rotationPitch));
            matrices.rotateXDeg((-pitch / 2F) * leaning);
            matrices.translate(0, -leaning * getEyeHeight(caps, MMPose.STANDING), 0);
        }
        boolean isSneak = (boolean) caps.getCapsValue(IModelCaps.caps_isPoseCrouching);
        if (isSneak) {
            matrices.translate(0, -0.85F, 0);
        }
    }

    @Override
    public void animateModel(IModelCaps caps, float limbAngle, float limbDistance, float tickDelta) {

    }

    @Override
    public void setAngles(IModelCaps caps, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean isSneak = (boolean) caps.getCapsValue(IModelCaps.caps_isPoseCrouching);
        IAngleList angleList;
        if (isSneak) {
            float sneakLimbDistance = 0.2584113F;
            float sneakDiagonalLimbDistance = 0.366325F;
            float sneakWalkPercent = IMultiModel.clamp(limbDistance / sneakLimbDistance, 0, 1);
            IAngleList sneakList = createSneakAnimationBuilder(60, new Bezier(0.3F, 0.0F, 0.7F, 1.0F))
                    .build().getAngles(animationProgress);
            IAngleList sneakWalkList = createSneakWalkAnimationBuilder(8, new Bezier(0.3F, 0.0F, 0.7F, 1.0F))
                    .build().getAngles(limbAngle);
            angleList = new CompositeAngleList(sneakList, sneakWalkList, sneakWalkPercent);
        } else {

            float walkLimbDistance = 0.8634361F;
            float sprintLimbDistance = 1F;
            float walkPercent = IMultiModel.clamp(limbDistance / walkLimbDistance, 0, 1);
            float sprintPercent = IMultiModel.clamp(
                    (limbDistance - walkLimbDistance) / (sprintLimbDistance - walkLimbDistance), 0, 1);

            boolean isOnGround = (boolean) caps.getCapsValue(IModelCaps.caps_onGround);
            float gravity = 0.0784F;
            float yMotion = (float) (double) caps.getCapsValue(IModelCaps.caps_motionY) + gravity;
            float jumpPercent = isOnGround ? 0 : IMultiModel.clamp(-yMotion, 0, 1);

            IAngleList waitList = wait.getAngles(animationProgress);
            IAngleList walkList = walk.getAngles(limbAngle / walkLimbDistance);
            IAngleList sprintList = sprint.getAngles(limbAngle / sprintLimbDistance);
            IAngleList jumpList = fall.getAngles(animationProgress);
            angleList = new CompositeAngleList(waitList, walkList, walkPercent);
            angleList = new CompositeAngleList(angleList, sprintList, sprintPercent);
            angleList = new CompositeAngleList(angleList, jumpList, jumpPercent);

            float leaning = IMultiModel.lerp(animationProgress % 1F,
                    (float) caps.getCapsValue(IModelCaps.caps_lastLeaningPitch),
                    (float) caps.getCapsValue(IModelCaps.caps_leaningPitch));
            if (0 < leaning) {
                IAngleList swimList = swim.getAngles(animationProgress);
                angleList = new CompositeAngleList(angleList, swimList, leaning);
            }

            float swingPercent = (float) caps.getCapsValue(IModelCaps.caps_isSwingInProgress);
            if (0 < swingPercent) {

            }

        }

        IAngleList.apply(angleList, parts);

        float leaning = IMultiModel.lerp(animationProgress % 1F,
                (float) caps.getCapsValue(IModelCaps.caps_lastLeaningPitch),
                (float) caps.getCapsValue(IModelCaps.caps_leaningPitch));
        if (0 < leaning) {
            headPitch = (headPitch / 2F - 30F) * leaning;
        }

        addDeg(head, IMultiModel.clamp(headPitch, -27.5F, 90), IMultiModel.clamp(headYaw, -50, 50), 0);

        float yaw = IMultiModel.clamp(headYaw / 50F, -1, 1);
        float pitch = headPitch < 0 ? Math.max(headPitch / 27.5F, -1F) : Math.min(headPitch / 90F, 1F);
        addDeg(headWearTM, lerpTM.lerp(yaw, pitch));
        addDeg(headWearM, lerpM.lerp(yaw, pitch));
        addDeg(headWearBM, lerpBM.lerp(yaw, pitch));
        addDeg(headWearB, lerpB.lerp(yaw, pitch));
        /*
        float wobbling = IMultiModel.sin(animationProgress * 0.05F);
        if ((Boolean) caps.getCapsValue(IModelCaps.caps_isRiding)) {
            setDeg(upperBody, 10 + wobbling, 0, 0);
            setDeg(neck, -8 - wobbling * 0.5F, 0, 0);
            setDeg(upperArmL, -60 - wobbling, 0, -20);
            setDeg(lowerArmL, -7.5F, -17.5F, 47.5F - wobbling);
            setDeg(upperArmR, -60 - wobbling, 0, 20);
            setDeg(lowerArmR, -7.5F, 17.5F, -47.5F + wobbling);
            setDeg(lowerBody, -15, 0, 0);
            setDeg(legL, -120, 0, 0);
            setDeg(legR, -120, 0, 0);
            setDeg(lowerLegL, 120, 0, 0);
            setDeg(lowerLegR, 120, 0, 0);
        }*/
    }

    @Override
    public void render(MMRenderContext context) {
        context.render(body::render);
    }

    @Override
    public void adjustHandItem(MMMatrixStack matrices, boolean isLeft) {
        if (isLeft) {
            lowerArmL.aRotate(matrices);
            matrices.translate(0, 0.5, 0);
        } else {
            lowerArmR.aRotate(matrices);
            matrices.translate(0, 0.5, 0);
        }
    }

    @Override
    public int getTextureWidth() {
        return 128;
    }

    @Override
    public int getTextureHeight() {
        return 64;
    }

    @Override
    public float getInnerArmorSize() {
        return 0.1F;
    }

    @Override
    public float getOuterArmorSize() {
        return 0.5F;
    }

    @Override
    public float getWidth(IModelCaps caps, MMPose pose) {
        return 0.6F;
    }

    @Override
    public float getHeight(IModelCaps caps, MMPose pose) {
        if (pose == MMPose.SWIMMING) return 0.8F;
        if (pose == MMPose.CROUCHING) return 1.95F;
        return 2.9F;
    }

    @Override
    public float getEyeHeight(IModelCaps caps, MMPose pose) {
        return getHeight(caps, pose) * 0.9F;
    }

    @Override
    public float getyOffset(IModelCaps caps) {
        return getHeight(caps, MMPose.STANDING) * 0.7F;
    }

    @Override
    public float getMountedYOffset(IModelCaps caps) {
        return getHeight(caps, MMPose.STANDING);
    }

    @Override
    public float getLeashOffset(IModelCaps caps) {
        return 2.2F;
    }

    @Override
    public void showAllParts(IModelCaps caps) {
        neck.setVisible(true);
        bellyU.setVisible(true);
        bellyL.setVisible(true);
        upperLegL.setVisible(true);
        upperLegR.setVisible(true);
        lowerLegL.setVisible(true);
        lowerLegR.setVisible(true);
    }

    @Override
    public int showArmorParts(int parts, int index) {
        boolean head = parts == 3;
        neck.setVisible(head);
        boolean chest = parts == 2;
        bellyU.setVisible(chest);
        boolean leg = parts == 1;
        bellyL.setVisible(leg);
        upperLegL.setVisible(leg);
        upperLegR.setVisible(leg);
        boolean foot = parts == 0;
        lowerLegL.setVisible(foot);
        lowerLegR.setVisible(foot);
        return -1;
    }

    private static void addDeg(ModelPart bone, Angle angle) {
        addDeg(bone, angle.getPitch(), angle.getYaw(), angle.getRoll());
    }

    private static void addDeg(ModelPart bone, float pitch, float yaw, float roll) {
        float rad = (float) (Math.PI / 180);
        bone.addRotation(pitch * rad, yaw * rad, roll * rad);
    }

}