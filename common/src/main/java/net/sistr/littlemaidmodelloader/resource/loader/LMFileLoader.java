package net.sistr.littlemaidmodelloader.resource.loader;

import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class LMFileLoader {
    public static final LMFileLoader INSTANCE = new LMFileLoader();
    private static final Logger LOGGER = LogManager.getLogger();
    private final ArrayList<LMLoader> loaders = new ArrayList<>();
    private final ArrayList<Path> folderPaths = new ArrayList<>();

    public void addLoader(LMLoader loader) {
        loaders.add(loader);
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
                Stream<Path> stream = Files.walk(folderPath);
                stream.filter(path -> !Files.isDirectory(path))
                        .forEach(path -> fileLoad(folderPath, path));
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        LOGGER.debug("Loading end : " + ((end - start) / (1000D * 1000D)) + "ms");
    }

    private void fileLoad(Path folderPath, Path path) {
        if (isArchive(path)) {
            loadArchive(folderPath, path);
        } else {
            loadFile(folderPath, path);
        }
    }

    public void loadArchive(Path folderPath, Path path) {
        if (!loadArchive(folderPath, path, StandardCharsets.UTF_8)
                && Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS) {
            LOGGER.info("MS932でリトライします。 : " + path);
            if (loadArchive(folderPath, path, Charset.forName("MS932"))) {
                LOGGER.info("読み込みに成功。");
            } else {
                LOGGER.error("読み込みに失敗。");
            }
        }
    }

    private boolean loadArchive(Path folderPath, Path path, Charset charset) {
        boolean result = true;
        try (ZipInputStream zipStream = new ZipInputStream(Files.newInputStream(path), charset)) {
            while (true) {
                ZipEntry entry = zipStream.getNextEntry();
                if (entry == null) {
                    break;
                }
                loaders.stream().filter(loader -> loader.canLoad(entry.getName(), path, zipStream, true))
                        .forEach(loader -> loader.load(entry.getName(), path, zipStream, true));
            }
        } catch (ZipException e) {
            LOGGER.error("Zipの読み込み中にエラーが発生。" +
                    "ファイル名に日本語などが入っている可能性があります。" +
                    "Error while loading Zip, " +
                    "possibly due to Japanese characters in the file name. : " + path);
            result = false;
        } catch (IllegalArgumentException e) {
            if (e.getCause() instanceof MalformedInputException) {
                LOGGER.error("Zipの読み込み中にエラーが発生。" +
                        "Zip内のファイル名に日本語などが入っている可能性があります。" +
                        "Error while loading Zip, " +
                        "possibly due to Japanese characters in the file name in the Zip. : " + path);
            } else {
                LOGGER.error("不明なエラーによりZipが読み込めません。" +
                        "Unknown error prevents Zip from loading. : " + path);
            }
            result = false;
        } catch (Exception e) {
            LOGGER.error("不明なエラーによりZipが読み込めません。" +
                    "Unknown error prevents Zip from loading. : " + path);
            result = false;
        }
        return result;
    }

    public void loadFile(Path folderPath, Path path) {
        String relPath = path.toString().replace(folderPath.toString(), "");
        try (InputStream inputStream = Files.newInputStream(path)) {
            loaders.stream().filter(loader -> loader.canLoad(relPath, folderPath, inputStream, false))
                    .forEach(loader -> loader.load(relPath, folderPath, inputStream, false));
        } catch (Exception e) {
            LOGGER.error("Error! : " + e.getMessage() + " : " + path);
        }
    }

    public boolean isArchive(Path path) {
        return path.toString().endsWith("zip") || path.toString().endsWith("jar");
    }

}
