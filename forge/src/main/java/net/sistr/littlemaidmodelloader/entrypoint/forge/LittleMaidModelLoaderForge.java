package net.sistr.littlemaidmodelloader.entrypoint.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sistr.littlemaidmodelloader.LittleMaidModelLoader;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderer;
import net.sistr.littlemaidmodelloader.setup.ClientSetup;
import net.sistr.littlemaidmodelloader.setup.ModSetup;
import net.sistr.littlemaidmodelloader.setup.Registration;

@Mod(LittleMaidModelLoader.MODID)
public class LittleMaidModelLoaderForge {

    public LittleMaidModelLoaderForge() {
        EventBuses.registerModEventBus(LittleMaidModelLoader.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        LittleMaidModelLoader.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::rendererInit);
    }

    public void modInit(FMLCommonSetupEvent event) {
        ModSetup.init();
    }

    public void clientInit(FMLClientSetupEvent event) {
        ClientSetup.init();
    }

    public void rendererInit(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.MULTI_MODEL_ENTITY.get(), MultiModelRenderer::new);
        event.registerEntityRenderer(Registration.DUMMY_MODEL_ENTITY.get(), MultiModelRenderer::new);
    }

}
