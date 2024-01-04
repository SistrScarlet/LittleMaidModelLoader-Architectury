package net.sistr.littlemaidmodelloader.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderer;
import net.sistr.littlemaidmodelloader.client.resource.LMPackProvider;
import net.sistr.littlemaidmodelloader.config.LMMLConfig;
import net.sistr.littlemaidmodelloader.setup.ClientSetup;
import net.sistr.littlemaidmodelloader.setup.ModSetup;
import net.sistr.littlemaidmodelloader.setup.Registration;

@Mod(LMMLMod.MODID)
public class LMMLForge {

    public LMMLForge() {
        EventBuses.registerModEventBus(LMMLMod.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        LMMLMod.init();

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
                () -> (client, parent) ->
                        AutoConfig.getConfigScreen(LMMLConfig.class, parent).get());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);

        //このタイミングでないと遅すぎる
        if (FMLEnvironment.dist.isClient()) {
            packInit();
        }
    }

    public void modInit(FMLCommonSetupEvent event) {
        ModSetup.init();
    }

    public void clientInit(FMLClientSetupEvent event) {
        ClientSetup.init();
        rendererInit();
    }

    public void rendererInit() {
        RenderingRegistry.registerEntityRenderingHandler(Registration.MULTI_MODEL_ENTITY.get(), MultiModelRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.DUMMY_MODEL_ENTITY.get(), MultiModelRenderer::new);
    }

    public void packInit() {
        MinecraftClient.getInstance().getResourcePackManager().addPackFinder(new LMPackProvider());
    }

}
