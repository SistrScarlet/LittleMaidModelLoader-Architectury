package net.sistr.littlemaidmodelloader.multimodel.layer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class MMMatrixStack {
    private final MatrixStack matrixStack;

    public MMMatrixStack(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    public MatrixStack getVanillaMatrixStack() {
        return matrixStack;
    }

    public void push() {
        matrixStack.push();
    }

    public void pop() {
        matrixStack.pop();
    }

    public void translate(double x, double y, double z) {
        matrixStack.translate(x, y, z);
    }

    public void scale(float x, float y, float z) {
        matrixStack.scale(x, y, z);
    }

    public void multiply(float x, float y, float z, float w) {
        matrixStack.multiply(new Quaternion(x, y, z, w));
    }

    public void rotateXRad(float rad) {
        matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(rad));
    }

    public void rotateXDeg(float deg) {
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(deg));
    }

    public void rotateYRad(float rad) {
        matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(rad));
    }

    public void rotateYDeg(float deg) {
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(deg));
    }

    public void rotateZRad(float rad) {
        matrixStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(rad));
    }

    public void rotateZDeg(float deg) {
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(deg));
    }

}
