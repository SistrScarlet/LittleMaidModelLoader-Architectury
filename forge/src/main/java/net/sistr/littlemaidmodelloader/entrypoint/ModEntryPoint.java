package net.sistr.littlemaidmodelloader.entrypoint;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sistr.littlemaidmodelloader.LittleMaidModelLoader;
import net.sistr.littlemaidmodelloader.register.Attributes;
import net.sistr.littlemaidmodelloader.setup.ClientSetup;
import net.sistr.littlemaidmodelloader.setup.ModSetup;

@Mod(LittleMaidModelLoader.MODID)
public class ModEntryPoint {

    public ModEntryPoint() {
        EventBuses.registerModEventBus(LittleMaidModelLoader.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::attributeRegister);
        LittleMaidModelLoader.init();
    }

    public void modInit(FMLCommonSetupEvent event) {
        ModSetup.init();
    }

    public void clientInit(FMLClientSetupEvent event) {
        ClientSetup.init();
    }

    public void attributeRegister(EntityAttributeCreationEvent event) {
        Attributes.getAttributes().forEach(set -> event.put(set.getEntityType(), set.getAttribute().get().build()));
    }

}
