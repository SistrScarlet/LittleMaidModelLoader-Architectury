package net.sistr.littlemaidmodelloader;

import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.entity.EntityAttributes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.sistr.littlemaidmodelloader.client.resource.loader.LMSoundLoader;
import net.sistr.littlemaidmodelloader.client.resource.loader.LMTextureLoader;
import net.sistr.littlemaidmodelloader.client.resource.manager.LMSoundManager;
import net.sistr.littlemaidmodelloader.entity.MultiModelEntity;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.maidmodel.*;
import net.sistr.littlemaidmodelloader.resource.classloader.MultiModelClassLoader;
import net.sistr.littlemaidmodelloader.resource.loader.LMConfigLoader;
import net.sistr.littlemaidmodelloader.resource.loader.LMFileLoader;
import net.sistr.littlemaidmodelloader.resource.loader.LMMultiModelLoader;
import net.sistr.littlemaidmodelloader.resource.manager.LMConfigManager;
import net.sistr.littlemaidmodelloader.resource.manager.LMModelManager;
import net.sistr.littlemaidmodelloader.resource.manager.LMTextureManager;
import net.sistr.littlemaidmodelloader.setup.Registration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

public class LittleMaidModelLoader {

    public static final String MODID = "littlemaidmodelloader";

    public static final Logger LOGGER = LogManager.getLogger();

    public static void init() {
        initFileLoader();
        initModelLoader();
        if (Platform.getEnv() == EnvType.CLIENT) {
            initTextureLoader();
            initSoundLoader();
        }
        Registration.init();
        registerAttribute();
    }

    public static void initFileLoader() {
        LMFileLoader fileLoader = LMFileLoader.INSTANCE;
        fileLoader.addLoadFolderPath(Paths.get(Platform.getGameFolder().toString(), "LMMLResources"));
        fileLoader.addLoader(new LMMultiModelLoader(LMModelManager.INSTANCE, new MultiModelClassLoader(fileLoader.getFolderPaths())));
        fileLoader.addLoader(new LMConfigLoader(LMConfigManager.INSTANCE));
    }

    public static void initModelLoader() {
        //モデルを読み込む
        LMModelManager modelManager = LMModelManager.INSTANCE;
        modelManager.addModel("Default", ModelLittleMaid_Orign.class);
        modelManager.addModel("SR2", ModelLittleMaid_SR2.class);
        modelManager.addModel("Aug", ModelLittleMaid_Aug.class);
        modelManager.addModel("Archetype", ModelLittleMaid_Archetype.class);
        modelManager.addModel("Steve", ModelMulti_Steve.class);
        modelManager.addModel("Stef", ModelMulti_Stef.class);
        //modelManager.addModel("Alicia", new MultiModel_Alicia(), new MultiModel_Alicia(), new MultiModel_Alicia());
        modelManager.addModel("Beverly7", ModelLittleMaid_Beverly7.class);
        modelManager.addModel("Chloe2", ModelLittleMaid_Chloe2.class);
        modelManager.addModel("Elsa5", ModelLittleMaid_Elsa5.class);
        modelManager.setDefaultModel(modelManager.getModel("Default", IHasMultiModel.Layer.SKIN)
                .orElseThrow(RuntimeException::new));
    }

    @Environment(EnvType.CLIENT)
    public static void initTextureLoader() {
        LMFileLoader fileLoader = LMFileLoader.INSTANCE;
        LMTextureLoader textureProcessor = new LMTextureLoader(LMTextureManager.INSTANCE);
        textureProcessor.addPathConverter("assets/", "");
        textureProcessor.addPathConverter("mob/", "minecraft/textures/entity/");
        fileLoader.addLoader(textureProcessor);
    }

    @Environment(EnvType.CLIENT)
    public static void initSoundLoader() {
        LMFileLoader.INSTANCE.addLoader(new LMSoundLoader(LMSoundManager.INSTANCE));
    }

    public static void registerAttribute() {
        EntityAttributes.register(() -> Registration.MULTI_MODEL_ENTITY_BEFORE, MultiModelEntity::createMobAttributes);
        EntityAttributes.register(() -> Registration.DUMMY_MODEL_ENTITY_BEFORE, MultiModelEntity::createMobAttributes);
    }

}
