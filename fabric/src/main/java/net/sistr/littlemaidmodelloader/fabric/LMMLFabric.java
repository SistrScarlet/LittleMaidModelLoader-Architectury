package net.sistr.littlemaidmodelloader.fabric;

import net.fabricmc.api.ModInitializer;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.setup.ModSetup;

public class LMMLFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        LMMLMod.init();
        ModSetup.init();
    }
}
