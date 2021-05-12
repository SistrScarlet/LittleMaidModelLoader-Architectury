package net.sistr.littlemaidmodelloader.util.forge;


import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class FolderPathsImpl {

    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

}
