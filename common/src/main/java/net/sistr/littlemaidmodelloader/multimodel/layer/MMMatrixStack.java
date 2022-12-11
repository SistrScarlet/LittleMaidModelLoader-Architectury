package net.sistr.littlemaidmodelloader.multimodel.layer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

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
        matrixStack.multiply(new Quaternionf(x, y, z, w));
    }

    public void rotateXRad(float rad) {
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(rad));
    }

    public void rotateXDeg(float deg) {
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(deg));
    }

    public void rotateYRad(float rad) {
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(rad));
    }

    public void rotateYDeg(float deg) {
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(deg));
    }

    public void rotateZRad(float rad) {
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(rad));
    }

    public void rotateZDeg(float deg) {
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(deg));
    }

}
