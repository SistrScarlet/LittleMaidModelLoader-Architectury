package net.sistr.littlemaidmodelloader.util;

import me.shedaniel.architectury.annotations.ExpectPlatform;

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
