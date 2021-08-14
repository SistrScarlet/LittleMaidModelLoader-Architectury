package net.sistr.littlemaidmodelloader.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class DebugCheckerImpl {
    public static boolean isDebug() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
