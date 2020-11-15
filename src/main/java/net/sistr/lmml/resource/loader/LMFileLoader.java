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
        LOGGER.debug("Loading end : " + ((end - start) / (1000D * 1000D)) + "ms");
    }

    private void fileLoad(Path folderPath, Path path) {
        if (isArchive(path)) {
            ZipInputStream zipStream;
            try {
                zipStream = new ZipInputStream(Files.newInputStream(path));
            } catch (Exception e) {
                LOGGER.debug("Can't load Zip! : " + path);
                e.printStackTrace();
                return;
            }
            ZipEntry entry;
            try {
                while ((entry = zipStream.getNextEntry()) != null) {
                    ZipEntry finalEntry = entry;
                    loaders.stream().filter(loader -> loader.canLoad(finalEntry.getName(), path, zipStream, true))
                            .forEach(loader -> loader.load(finalEntry.getName(), path, zipStream, true));
                }
            } catch (Exception e) {
                LOGGER.debug("Zipの読み込み中にエラーが発生。ファイルネームに日本語などが入ってると読み込めません。" +
                        "An error occurs while reading a Zip file. If the file name contains Japanese characters, " +
                        "it cannot be read. : " + path);
                e.printStackTrace();
            }
        } else {
            try {
                String relPath = path.toString().replace(folderPath.toString(), "");
                InputStream inputStream = Files.newInputStream(path);
                loaders.stream().filter(loader -> loader.canLoad(relPath, folderPath, inputStream, false))
                        .forEach(loader -> loader.load(relPath, folderPath, inputStream, false));
            } catch (Exception e) {
                LOGGER.debug("Error! : " + e.getMessage() + " : " + path);
            }
        }
    }

    private boolean isArchive(Path path) {
        return path.toString().endsWith("zip") || path.toString().endsWith("jar");
    }

}
