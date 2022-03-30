package net.sistr.littlemaidmodelloader.resource.holder;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class ConfigHolder {

    private final String packName;
    private final String parentName;
    private final String fileName;

    private final Map<String, String> settings;

    public ConfigHolder(String packName, String parentName, String fileName, Map<String, String> settings) {
        this.packName = packName;
        this.parentName = parentName;
        this.fileName = fileName;
        this.settings = settings;
    }

    public String getName() {
        String name = "";
        if (!packName.isEmpty()) {
            name += packName + ".";
        }
        if (!parentName.isEmpty()) {
            name += parentName + ".";
        }
        name += fileName;
        return name;
    }

    public String getPackName() {
        return packName;
    }

    public String getParentName() {
        return parentName;
    }

    public String getFileName() {
        return fileName;
    }

    public Optional<String> getSoundFileName(String soundName) {
        return Optional.ofNullable(settings.get(soundName));
    }
}
