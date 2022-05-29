package net.sistr.littlemaidmodelloader.entrypoint.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import me.shedaniel.architectury.registry.entity.EntityRenderers;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderer;
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
    }

    public void modInit(FMLCommonSetupEvent event) {
        ModSetup.init();
    }

    public void clientInit(FMLClientSetupEvent event) {
        ClientSetup.init();
        EntityRenderers.register(Registration.MULTI_MODEL_ENTITY.get(), MultiModelRenderer::new);
        EntityRenderers.register(Registration.DUMMY_MODEL_ENTITY.get(), MultiModelRenderer::new);
    }

}
