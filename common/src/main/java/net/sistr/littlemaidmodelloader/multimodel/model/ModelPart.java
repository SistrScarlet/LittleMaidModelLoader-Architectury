package net.sistr.littlemaidmodelloader.multimodel.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMMatrixStack;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMVertexConsumer;

@Environment(EnvType.CLIENT)
public class ModelPart {
    protected final net.minecraft.client.model.ModelPart modelPart;
    protected ModelPart parent;

    public ModelPart(IMultiModel model) {
        modelPart = initModelPart(model);
    }

    //追加

    public net.minecraft.client.model.ModelPart initModelPart(IMultiModel model) {
        return new net.minecraft.client.model.ModelPart(model.getTextureWidth(), model.getTextureHeight(), 0, 0);
    }

    public void setRotation(float pitch, float yaw, float roll) {
        this.modelPart.pitch = pitch;
        this.modelPart.yaw = yaw;
        this.modelPart.roll = roll;
    }

    public void addRotation(float pitch, float yaw, float roll) {
        this.modelPart.pitch += pitch;
        this.modelPart.yaw += yaw;
        this.modelPart.roll += roll;
    }

    public void setVisible(boolean visible) {
        this.modelPart.visible = visible;
    }

    public void aRotate(MMMatrixStack matrixStack) {
        if (this.parent != null) {
            this.parent.aRotate(matrixStack);
        }

        this.rotate(matrixStack);
    }

    //Delegate

    public void addChild(ModelPart part) {
        this.modelPart.addChild(part.modelPart);
        part.parent = this;
    }

    public ModelPart setTextureOffset(int textureOffsetU, int textureOffsetV) {
        this.modelPart.setTextureOffset(textureOffsetU, textureOffsetV);
        return this;
    }

    public void addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extra, boolean mirror) {
        this.modelPart.addCuboid(x, y, z, sizeX, sizeY, sizeZ, extra, mirror);
    }

    public void setPivot(float x, float y, float z) {
        this.modelPart.setPivot(x, y, z);
    }

    public void rotate(MMMatrixStack matrixStack) {
        this.modelPart.rotate(matrixStack.getVanillaMatrixStack());
    }

    public void render(MMMatrixStack matrices, MMVertexConsumer vertices, int light, int overlay) {
        this.modelPart.render(matrices.getVanillaMatrixStack(), vertices.getVanillaVertexConsumer(),
                light, overlay);
    }

    public void render(MMMatrixStack matrices, MMVertexConsumer vertices, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        this.modelPart.render(matrices.getVanillaMatrixStack(), vertices.getVanillaVertexConsumer(),
                light, overlay, red, green, blue, alpha);
    }

}
