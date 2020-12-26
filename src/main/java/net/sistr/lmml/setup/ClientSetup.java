package net.sistr.lmml.setup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.sistr.lmml.SideChecker;
import net.sistr.lmml.client.renderer.MultiModelRenderer;
import net.sistr.lmml.entity.compound.IHasMultiModel;
import net.sistr.lmml.maidmodel.*;
import net.sistr.lmml.network.Networking;
import net.sistr.lmml.resource.loader.LMFileLoader;
import net.sistr.lmml.client.resource.loader.LMSoundLoader;
import net.sistr.lmml.client.resource.loader.LMTextureLoader;
import net.sistr.lmml.resource.loader.LMMultiModelLoader;
import net.sistr.lmml.resource.manager.LMModelManager;
import net.sistr.lmml.client.AddableResourcePackProvider;
import net.sistr.lmml.client.LMMLPackFinder;
import net.sistr.lmml.client.resource.manager.LMSoundManager;
import net.sistr.lmml.resource.manager.LMTextureManager;

//todo Forgeの方だと鯖蔵同時に動くし、これモデルとテクスチャの鯖蔵同期ダメにしてね？
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

        ((AddableResourcePackProvider)MinecraftClient.getInstance().getResourcePackManager())
                .addResourcePackProvider(new LMMLPackFinder());

        //モデルを読み込む
        LMModelManager modelManager = LMModelManager.INSTANCE;
        modelManager.addModel("Default", ModelLittleMaid_Orign.class);
        modelManager.addModel("SR2", ModelLittleMaid_SR2.class);
        modelManager.addModel("Aug", ModelLittleMaid_Aug.class);
        modelManager.addModel("Archetype", ModelLittleMaid_Archetype.class);
        modelManager.addModel("Steve", ModelMulti_Steve.class);
        modelManager.addModel("Stef", ModelMulti_Stef.class);
        modelManager.setDefaultModel(modelManager.getModel("Default", IHasMultiModel.Layer.SKIN)
                .orElseThrow(RuntimeException::new));

        fileLoader.load();

        Networking.INSTANCE.init();

        EntityRendererRegistry.INSTANCE.register(Registration.MULTI_MODEL_ENTITY, (a, b) -> new MultiModelRenderer<>(a));
        EntityRendererRegistry.INSTANCE.register(Registration.DUMMY_MODEL_ENTITY, (a, b) -> new MultiModelRenderer<>(a));
    }

}
