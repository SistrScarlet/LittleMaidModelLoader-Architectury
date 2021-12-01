package net.sistr.littlemaidmodelloader.multimodel.model;

import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;

public class SmoothModelPartDelegate extends ModelPart {

    public SmoothModelPartDelegate(IMultiModel model) {
        super(model);
    }

    @Override
    public net.minecraft.client.model.ModelPart initModelPart(IMultiModel model) {
        return new SmoothModelPart(model.getTextureWidth(), model.getTextureHeight(), 0, 0);
    }

}
