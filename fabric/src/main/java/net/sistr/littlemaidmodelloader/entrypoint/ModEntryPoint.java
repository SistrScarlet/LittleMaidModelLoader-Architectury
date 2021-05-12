package net.sistr.littlemaidmodelloader.entrypoint;

import net.fabricmc.api.ModInitializer;
import net.sistr.littlemaidmodelloader.LittleMaidModelLoader;
import net.sistr.littlemaidmodelloader.setup.ModSetup;

public class ModEntryPoint implements ModInitializer {

    public ModEntryPoint() {
        LittleMaidModelLoader.init();
    }

    @Override
    public void onInitialize() {
        ModSetup.init();
    }
}
