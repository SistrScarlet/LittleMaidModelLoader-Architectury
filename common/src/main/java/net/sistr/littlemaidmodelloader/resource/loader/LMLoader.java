package net.sistr.littlemaidmodelloader.resource.loader;

import java.io.InputStream;
import java.nio.file.Path;

public interface LMLoader {

    boolean canLoad(String path, Path folderPath, InputStream inputStream, boolean isArchive);

    void load(String path, Path folderPath, InputStream inputStream, boolean isArchive);

}
