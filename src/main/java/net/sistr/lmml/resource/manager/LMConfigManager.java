package net.sistr.lmml.resource.manager;

import net.sistr.lmml.resource.holder.ConfigHolder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class LMConfigManager {
    public static final LMConfigManager INSTANCE = new LMConfigManager();
    public static final ConfigHolder EMPTY_CONFIG;
    private final Map<String, ConfigHolder> configs = new HashMap<>();

    public void addConfig(String packName, String parentName, String fileName, Map<String, String> settings) {
        String configName = packName + "." + parentName + "." + fileName;
        configs.put(configName.toLowerCase(), new ConfigHolder(configName, fileName, settings));
    }

    public Optional<ConfigHolder> getConfig(String configName) {
        return Optional.ofNullable(configs.get(configName.toLowerCase()));
    }

    public Optional<ConfigHolder> getTextureSoundConfig(String texturePackName) {
        return configs.values().stream().filter(configHolder ->
                configHolder.getFileName().toLowerCase().equals(texturePackName.toLowerCase()))
                .findAny();
    }

    public ConfigHolder getAnyConfig() {
        return configs.values().stream().filter(configHolder ->
                configHolder.getFileName().toLowerCase().equals("littlemaidmob"))
                .min(Comparator.comparingInt(a -> ThreadLocalRandom.current().nextInt()))
                .orElse(LMConfigManager.EMPTY_CONFIG);
    }

    static {
        EMPTY_CONFIG =
                new ConfigHolder("", "", null) {
                    @Override
                    public Optional<String> getSoundFileName(String soundName) {
                        return Optional.empty();
                    }
                };
    }

}
