package net.sistr.littlemaidmodelloader.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FolderPathsImpl {

    public static Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

}
