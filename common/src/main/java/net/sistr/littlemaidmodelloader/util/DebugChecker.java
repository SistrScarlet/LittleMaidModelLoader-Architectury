package net.sistr.littlemaidmodelloader.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class DebugChecker {

    @ExpectPlatform
    public static boolean isDebug() {
        throw new AssertionError();
    }

}
