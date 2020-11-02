package net.sistr.lmml;

import net.fabricmc.api.ModInitializer;
import net.sistr.lmml.resource.classloader.MultiModelClassLoader;
import net.sistr.lmml.resource.loader.*;
import net.sistr.lmml.resource.manager.LMConfigManager;
import net.sistr.lmml.resource.manager.LMModelManager;
import net.sistr.lmml.setup.ModSetup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

public class LittleMaidModelLoader implements ModInitializer {

    public static final String MODID = "littlemaidmodelloader";

    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        ModSetup.init();
    }

    public LittleMaidModelLoader() {
        LMFileLoader fileLoader = LMFileLoader.INSTANCE;
        fileLoader.addLoadFolderPath(Paths.get("LMMLResources").toAbsolutePath());
        LMModelManager modelManager = LMModelManager.INSTANCE;
        LMConfigManager configManager = LMConfigManager.INSTANCE;
        fileLoader.addLoader(new LMMultiModelLoader(modelManager,
                new MultiModelClassLoader(fileLoader.getFolderPaths())));
        fileLoader.addLoader(new LMConfigLoader(configManager));

    }

}
