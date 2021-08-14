package net.sistr.littlemaidmodelloader.util;

import me.shedaniel.architectury.annotations.ExpectPlatform;

public class LoaderChecker {

    @ExpectPlatform
    public static Loader getLoader() {
        throw new AssertionError();
    }

    public enum Loader {
        Fabric,
        Forge
    }

}
