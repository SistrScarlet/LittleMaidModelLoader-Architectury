package net.sistr.littlemaidmodelloader.util;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FolderPaths {

    @ExpectPlatform
    public static Path getGameDir() {
        throw new AssertionError();
    }

}
