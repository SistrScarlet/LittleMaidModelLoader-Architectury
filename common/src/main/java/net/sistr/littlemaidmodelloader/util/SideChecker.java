package net.sistr.littlemaidmodelloader.util;


import dev.architectury.injectables.annotations.ExpectPlatform;

public class SideChecker {

    @ExpectPlatform
    public static boolean isClient() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isServer() {
        throw new AssertionError();
    }

}
