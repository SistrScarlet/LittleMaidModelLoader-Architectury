package net.sistr.littlemaidmodelloader.util;


import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class FolderPaths {

    @ExpectPlatform
    public static Path getGameDir() {
        throw new AssertionError();
    }

}
