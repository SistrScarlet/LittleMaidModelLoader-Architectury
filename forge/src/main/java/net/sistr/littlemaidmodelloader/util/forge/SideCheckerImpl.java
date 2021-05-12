package net.sistr.littlemaidmodelloader.util.forge;

import net.minecraftforge.fml.loading.FMLEnvironment;

public class SideCheckerImpl {

    public static boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    public static boolean isServer() {
        return FMLEnvironment.dist.isDedicatedServer();
    }

}
