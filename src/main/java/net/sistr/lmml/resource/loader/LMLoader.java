package net.sistr.lmml.resource.loader;

import java.io.InputStream;
import java.nio.file.Path;

public interface LMLoader {

    boolean canLoad(String path, Path homePath, InputStream inputStream, boolean isArchive);

    void load(String path, Path homePath, InputStream inputStream, boolean isArchive);

}
