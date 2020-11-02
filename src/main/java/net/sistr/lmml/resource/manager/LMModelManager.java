package net.sistr.lmml.resource.manager;

import net.sistr.lmml.config.LMMLConfig;
import net.sistr.lmml.entity.compound.IHasMultiModel;
import net.sistr.lmml.maidmodel.ModelMultiBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LMModelManager {
    public static final LMModelManager INSTANCE = new LMModelManager();
    private static final Logger LOGGER = LogManager.getLogger();
    private ModelMultiBase defaultModel;
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

    public boolean hasModel(String modelName) {
        ModelMultiBase model = models.get(modelName.toLowerCase()).getModel(IHasMultiModel.Layer.INNER);
        return model != null;
    }

    public Optional<ModelMultiBase> getModel(String modelName, IHasMultiModel.Layer layer) {
        ModelHolder modelHolder = models.get(modelName.toLowerCase());
        if (modelHolder == null) {
            return Optional.empty();
        }
        ModelMultiBase model = modelHolder.getModel(layer);
        return Optional.of(model);
    }

    public void setDefaultModel(ModelMultiBase defaultModel) {
        this.defaultModel = defaultModel;
    }

    public ModelMultiBase getDefaultModel() {
        return defaultModel;
    }

    public static class ModelHolder {
        private final ModelMultiBase skin;
        private final ModelMultiBase inner;
        private final ModelMultiBase outer;

        public ModelHolder(ModelMultiBase skin, ModelMultiBase inner, ModelMultiBase outer) {
            this.skin = skin;
            this.inner = inner;
            this.outer = outer;
            if (skin == null || inner == null || outer == null) {
                throw new IllegalArgumentException("ModelHolderはnull不許容です");
            }
        }

        public ModelMultiBase getModel(IHasMultiModel.Layer layer) {
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
