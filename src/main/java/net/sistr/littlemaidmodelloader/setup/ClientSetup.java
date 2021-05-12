package net.sistr.littlemaidmodelloader.setup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.sistr.littlemaidmodelloader.SideChecker;
import net.sistr.littlemaidmodelloader.client.AddableResourcePackProvider;
import net.sistr.littlemaidmodelloader.client.LMPackProvider;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderer;
import net.sistr.littlemaidmodelloader.client.resource.loader.LMSoundLoader;
import net.sistr.littlemaidmodelloader.client.resource.loader.LMTextureLoader;
import net.sistr.littlemaidmodelloader.client.resource.manager.LMSoundManager;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.maidmodel.*;
import net.sistr.littlemaidmodelloader.multimodel.MultiModel_Alicia;
import net.sistr.littlemaidmodelloader.network.Networking;
import net.sistr.littlemaidmodelloader.resource.loader.LMFileLoader;
import net.sistr.littlemaidmodelloader.resource.manager.LMModelManager;
import net.sistr.littlemaidmodelloader.resource.manager.LMTextureManager;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SideChecker.init(SideChecker.Side.CLIENT);

        LMFileLoader fileLoader = LMFileLoader.INSTANCE;
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
        LMSoundManager soundManager = LMSoundManager.INSTANCE;
        LMTextureLoader textureProcessor = new LMTextureLoader(textureManager);
        textureProcessor.addPathConverter("assets/", "");
        textureProcessor.addPathConverter("mob/", "minecraft/textures/entity/");
        fileLoader.addLoader(textureProcessor);
        fileLoader.addLoader(new LMSoundLoader(soundManager));

        ((AddableResourcePackProvider) MinecraftClient.getInstance().getResourcePackManager())
                .addResourcePackProvider(new LMPackProvider());

        //モデルを読み込む
        LMModelManager modelManager = LMModelManager.INSTANCE;
        modelManager.addModel("Default", ModelLittleMaid_Orign.class);
        modelManager.addModel("SR2", ModelLittleMaid_SR2.class);
        modelManager.addModel("Aug", ModelLittleMaid_Aug.class);
        modelManager.addModel("Archetype", ModelLittleMaid_Archetype.class);
        modelManager.addModel("Steve", ModelMulti_Steve.class);
        modelManager.addModel("Stef", ModelMulti_Stef.class);
        modelManager.addModel("Alicia", new MultiModel_Alicia(), new MultiModel_Alicia(), new MultiModel_Alicia());
        modelManager.setDefaultModel(modelManager.getModel("Default", IHasMultiModel.Layer.SKIN)
                .orElseThrow(RuntimeException::new));

        fileLoader.load();

        Networking.INSTANCE.init();

        EntityRendererRegistry.INSTANCE.register(Registration.MULTI_MODEL_ENTITY, (a, b) -> new MultiModelRenderer<>(a));
        EntityRendererRegistry.INSTANCE.register(Registration.DUMMY_MODEL_ENTITY, (a, b) -> new MultiModelRenderer<>(a));
    }

}
