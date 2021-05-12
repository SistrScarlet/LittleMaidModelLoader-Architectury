package net.sistr.littlemaidmodelloader.multimodel.model;


import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;

public class SmoothModelPartDelegate2 extends ModelPart {

    public SmoothModelPartDelegate2(IMultiModel model) {
        super(model);
    }

    @Override
    public net.minecraft.client.model.ModelPart initModelPart(IMultiModel model) {
        return new SmoothModelPart2(model.getTextureWidth(), model.getTextureHeight(), 0, 0);
    }

}
