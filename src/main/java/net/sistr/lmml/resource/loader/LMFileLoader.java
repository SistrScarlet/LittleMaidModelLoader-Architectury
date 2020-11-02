package net.sistr.lmml.resource.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LMFileLoader {
    public static final LMFileLoader INSTANCE = new LMFileLoader();
    private static final Logger LOGGER = LogManager.getLogger();
    private final ArrayList<LMLoader> loaders = new ArrayList<>();
    private final ArrayList<Path> folderPaths = new ArrayList<>();

    public void addLoader(LMLoader processor) {
        loaders.add(processor);
    }

    public void addLoadFolderPath(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        folderPaths.add(path);
    }

    public ArrayList<Path> getFolderPaths() {
        return folderPaths;
    }

    public void load() {
        long start = System.nanoTime();
        LOGGER.debug("Loading start");
        folderPaths.forEach(folderPath -> {
            try {
                if (Files.notExists(folderPath)) {
                    Files.createDirectory(folderPath);
                }
                Files.walk(folderPath)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> fileLoad(folderPath, path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        LOGGER.debug("Loading end : " + ((double) (end - start) / 1000000D) + "ms");
    }

    //todo ファイル内ファイルの読み込み
    private void fileLoad(Path folderPath, Path path) {
        if (isArchive(path)) {
            ZipInputStream zipStream;
            try {
                zipStream = new ZipInputStream(Files.newInputStream(path));
            } catch (Exception e) {
                return;
            }
            while (true) {
                ZipEntry entry;
                try {
                    entry = zipStream.getNextEntry();
                } catch (Exception e) {
                    return;
                }
                if (entry == null) {
                    break;
                }
                loaders.stream().filter(loader -> loader.canLoad(entry.getName(), path, zipStream, true))
                        .forEach(loader -> loader.load(entry.getName(), path, zipStream, true));
            }
        } else {
            try {
                String relPath = path.toString().replace(folderPath.toString(), "");
                InputStream inputStream = Files.newInputStream(path);
                loaders.stream().filter(loader -> loader.canLoad(relPath, folderPath, inputStream, false))
                        .forEach(loader -> loader.load(relPath, folderPath, inputStream, false));
            } catch (Exception ignore) {

            }
        }
    }

    private boolean isArchive(Path path) {
        return path.toString().endsWith("zip") || path.toString().endsWith("jar");
    }

}
