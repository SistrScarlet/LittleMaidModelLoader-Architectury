package net.sistr.littlemaidmodelloader.util;

import me.shedaniel.architectury.annotations.ExpectPlatform;

public class DebugChecker {

    @ExpectPlatform
    public static boolean isDebug() {
        throw new AssertionError();
    }

}
