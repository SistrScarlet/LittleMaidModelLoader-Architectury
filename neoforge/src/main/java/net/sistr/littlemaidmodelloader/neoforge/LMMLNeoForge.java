package net.sistr.littlemaidmodelloader.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.resource.ResourceType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderer;
import net.sistr.littlemaidmodelloader.client.resource.LMPackProvider;
import net.sistr.littlemaidmodelloader.config.LMMLConfig;
import net.sistr.littlemaidmodelloader.setup.ClientSetup;
import net.sistr.littlemaidmodelloader.setup.ModSetup;
import net.sistr.littlemaidmodelloader.setup.Registration;

@Mod(LMMLMod.MODID)
public class LMMLNeoForge {

    public LMMLNeoForge(IEventBus modBus, ModContainer container) {
        LMMLMod.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            container.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (mc, parent) -> AutoConfig.getConfigScreen(LMMLConfig.class, parent).get());
        }

        modBus.addListener(this::modInit);
        modBus.addListener(this::clientInit);
        modBus.addListener(this::rendererInit);
        modBus.addListener(this::packInit);
    }

    public void modInit(FMLCommonSetupEvent event) {
        ModSetup.init();
    }

    public void clientInit(FMLClientSetupEvent event) {
        ClientSetup.init();
    }

    public void rendererInit(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                Registration.MULTI_MODEL_ENTITY.get(), MultiModelRenderer::new);
        event.registerEntityRenderer(
                Registration.DUMMY_MODEL_ENTITY.get(), MultiModelRenderer::new);
    }

    public void packInit(AddPackFindersEvent event) {
        if (event.getPackType() == ResourceType.CLIENT_RESOURCES) {
            event.addRepositorySource(new LMPackProvider());
        }
    }
}
