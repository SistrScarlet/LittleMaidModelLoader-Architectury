package net.sistr.littlemaidmodelloader.client.resource.loader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.client.resource.ResourceWrapper;
import net.sistr.littlemaidmodelloader.client.resource.manager.LMSoundManager;
import net.sistr.littlemaidmodelloader.resource.loader.LMLoader;
import net.sistr.littlemaidmodelloader.resource.util.ResourceHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;

//サーバーでは読み込む必要が無いため読み込まない
@Environment(EnvType.CLIENT)
public class LMSoundLoader implements LMLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LMSoundManager soundManager;

    public LMSoundLoader(LMSoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public boolean canLoad(String path, Path folderPath, InputStream inputStream, boolean isArchive) {
        return path.endsWith(".ogg") && ResourceHelper.getParentFolderName(path, isArchive).isPresent();
    }

    @Override
    public void load(String path, Path folderPath, InputStream inputStream, boolean isArchive) {
        String packName = ResourceHelper.getFirstParentName(path, folderPath, isArchive).orElse("");
        String parent = ResourceHelper.getParentFolderName(path, isArchive).orElse("");
        String fileName = ResourceHelper.getFileName(path, isArchive);
        Identifier location = ResourceHelper.getLocation("sounds", packName, fileName);
        fileName = ResourceHelper.removeExtension(fileName);
        fileName = ResourceHelper.removeNameLastIndex(fileName);
        soundManager.addSound(packName, parent, fileName, location);
        ResourceWrapper.addResourcePath(location, path, folderPath, isArchive);
        if (LMMLMod.getConfig().isDebugMode()) LOGGER.debug("Loaded Sound : " + packName + " : " + fileName);
    }

}
