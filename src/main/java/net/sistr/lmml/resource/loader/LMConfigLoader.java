package net.sistr.lmml.resource.loader;

import net.sistr.lmml.config.LMMLConfig;
import net.sistr.lmml.resource.manager.LMConfigManager;
import net.sistr.lmml.resource.util.ResourceHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class LMConfigLoader implements LMLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LMConfigManager configManager;

    public LMConfigLoader(LMConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean canLoad(String path, Path homePath, InputStream inputStream, boolean isArchive) {
        return path.endsWith(".cfg") && ResourceHelper.getFirstParentName(path, homePath, isArchive).isPresent();
    }

    @Override
    public void load(String path, Path homePath, InputStream inputStream, boolean isArchive) {
        Map<String, String> settings = new HashMap<>();
        try {
            getTextStream(inputStream).forEach(s -> addSettings(settings, s));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String packName = ResourceHelper.getFirstParentName(path, homePath, isArchive).orElseThrow(() ->
                new IllegalArgumentException("引数が不正です。"));
        String parentName = ResourceHelper.getParentFolderName(path).orElse("");
        String fileName = getFileName(path);
        fileName = ResourceHelper.removeExtension(fileName);
        configManager.addConfig(packName, parentName, fileName, settings);
        if (LMMLConfig.isDebugMode())
            LOGGER.debug("Loaded Config : " + packName + "." + parentName + "." + fileName + " : Total " + settings.size());
    }

    public void addSettings(Map<String, String> settings, String text) {
        int firstComment = text.indexOf('#');
        if (firstComment != -1)
            text = text.substring(0, firstComment);
        int firstSplitter = text.indexOf('=');
        if (firstSplitter == -1) return;
        String firstText = text.substring(0, firstSplitter);
        String secondText = text.substring(firstSplitter + 1);
        settings.put(firstText.toLowerCase(), secondText.toLowerCase());
    }

    public Stream<String> getTextStream(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines();
    }

    public String getFileName(String path) {
        int lastSplitter = path.lastIndexOf('/');
        if (lastSplitter == -1) return path;
        return path.substring(lastSplitter + 1);
    }

}
