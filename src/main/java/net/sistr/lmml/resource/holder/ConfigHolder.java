package net.sistr.lmml.resource.holder;

import java.util.Map;
import java.util.Optional;

public class ConfigHolder {

    private final String name;
    private final String fileName;

    private final Map<String, String> settings;

    public ConfigHolder(String configName, String fileName, Map<String, String> settings) {
        this.name = configName;
        this.fileName = fileName;
        this.settings = settings;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public Optional<String> getSoundFileName(String soundName) {
        return Optional.ofNullable(settings.get(soundName));
    }

}
