package net.sistr.lmml.client.resource.manager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.sistr.lmml.client.LMSoundInstance;
import net.sistr.lmml.client.resource.holder.SoundHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//todo MixモードとFillモード - リソースパックスクリーン改造
@Environment(EnvType.CLIENT)
public class LMSoundManager {
    public static final LMSoundManager INSTANCE = new LMSoundManager();
    private final Map<String, SoundHolder> soundPaths = new HashMap<>();

    public void addSound(String packName, String fileName, Identifier location) {
        SoundHolder soundHolder = soundPaths.computeIfAbsent(packName.toLowerCase(), k -> new SoundHolder(packName));
        soundHolder.addSound(fileName, location);
    }

    public Optional<WeightedSoundSet> getSound(String soundFileName) {
        int lastSplitter = soundFileName.lastIndexOf(".");
        if (lastSplitter == -1) {
            return Optional.empty();
        }
        String fileName = soundFileName.substring(lastSplitter + 1);
        String packName = soundFileName.substring(0, lastSplitter);
        lastSplitter = packName.lastIndexOf(".");
        if (lastSplitter != -1) {
            packName = packName.substring(lastSplitter + 1);
        }
        SoundHolder soundHolder = soundPaths.get(packName);
        if (soundHolder == null) {
            return Optional.empty();
        }
        return soundHolder.getSoundSet(fileName);
    }

    public void play(String soundFileName, SoundCategory soundCategory,
                     float volume, float pitch, double x, double y, double z) {
        getSound(soundFileName).ifPresent(soundSet -> MinecraftClient.getInstance().getSoundManager()
                        .play(new LMSoundInstance(soundSet, soundCategory, volume, pitch, x, y, z)));
    }
}
