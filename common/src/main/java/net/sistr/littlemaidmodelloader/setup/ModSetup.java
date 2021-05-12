package net.sistr.littlemaidmodelloader.setup;

import net.sistr.littlemaidmodelloader.network.Networking;
import net.sistr.littlemaidmodelloader.resource.loader.LMFileLoader;

public class ModSetup {

    public static void init() {
        LMFileLoader.INSTANCE.load();
        Networking.INSTANCE.init();
    }

}
