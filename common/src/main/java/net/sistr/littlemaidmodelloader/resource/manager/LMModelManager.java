package net.sistr.littlemaidmodelloader.resource.manager;

import net.sistr.littlemaidmodelloader.config.LMMLConfig;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;
import net.sistr.littlemaidmodelloader.maidmodel.ModelMultiBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LMModelManager {
    public static final LMModelManager INSTANCE = new LMModelManager();
    private static final Logger LOGGER = LogManager.getLogger();
    private IMultiModel defaultModel;
    private final Map<String, ModelHolder> models = new HashMap<>();

    public void addModel(String modelName, Class<? extends ModelMultiBase> modelClass) {
        try {
            Constructor<? extends ModelMultiBase> constructor = modelClass.getConstructor(float.class);
            ModelMultiBase skin = constructor.newInstance(0.0F);
            float[] size = skin.getArmorModelsSize();
            ModelMultiBase inner = constructor.newInstance(size[0]);
            ModelMultiBase outer = constructor.newInstance(size[1]);
            models.put(modelName.toLowerCase(), new ModelHolder(skin, inner, outer));
        } catch (Exception e) {
            LOGGER.debug("インスタンス化に失敗しました。抽象クラスまたは非対応のモデルである可能性があります。 : " + modelClass);
            e.printStackTrace();
            return;
        }
        if (LMMLConfig.isDebugMode()) LOGGER.debug("Loaded Model : " + modelClass);
    }

    public void addModel(String modelName, IMultiModel skin, IMultiModel inner, IMultiModel outer) {
        models.put(modelName.toLowerCase(), new ModelHolder(skin, inner, outer));
    }

    public boolean hasModel(String modelName) {
        return models.get(modelName.toLowerCase()) != null;
    }

    public Optional<IMultiModel> getModel(String modelName, IHasMultiModel.Layer layer) {
        ModelHolder modelHolder = models.get(modelName.toLowerCase());
        if (modelHolder == null) return Optional.empty();
        IMultiModel model = modelHolder.getModel(layer);
        return Optional.of(model);
    }

    public IMultiModel getOrDefaultModel(String modelName, IHasMultiModel.Layer layer) {
        ModelHolder modelHolder = models.get(modelName.toLowerCase());
        if (modelHolder == null) return getDefaultModel();
        return modelHolder.getModel(layer);
    }

    public void setDefaultModel(IMultiModel defaultModel) {
        this.defaultModel = defaultModel;
    }

    public IMultiModel getDefaultModel() {
        return defaultModel;
    }

    public static class ModelHolder {
        private final IMultiModel skin;
        private final IMultiModel inner;
        private final IMultiModel outer;

        public ModelHolder(IMultiModel skin, IMultiModel inner, IMultiModel outer) {
            this.skin = skin;
            this.inner = inner;
            this.outer = outer;
            if (skin == null || inner == null || outer == null) {
                throw new IllegalArgumentException("ModelHolderはnull不許容です");
            }
        }

        public IMultiModel getModel(IHasMultiModel.Layer layer) {
            switch (layer) {
                case SKIN:
                    return skin;
                case INNER:
                    return inner;
                case OUTER:
                    return outer;
                default:
                    throw new IllegalStateException();
            }
        }
    }
}
