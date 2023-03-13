package net.sistr.littlemaidmodelloader.client.resource.manager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.client.resource.LMSoundInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//todo MixモードとFillモード - リソースパックスクリーン改造
@Environment(EnvType.CLIENT)
public class LMSoundManager {
    public static final LMSoundManager INSTANCE = new LMSoundManager();
    private final Map<String, WeightedSoundSet> soundPaths = new HashMap<>();

    public void addSound(String packName, String parentName, String fileName, Identifier location) {
        WeightedSoundSet soundSet = soundPaths.computeIfAbsent(
                (packName + "." + parentName + "." + fileName).toLowerCase(),
                k -> new WeightedSoundSet(location, packName + "." + fileName));

        soundSet.add(new Sound(location.toString(), rand -> 1F, rand -> 1F, 1, Sound.RegistrationType.FILE,
                false, false, 16) {
            @Override
            public Identifier getLocation() {
                return getIdentifier();
            }
        });
    }

    public Optional<WeightedSoundSet> getSound(String soundFileLocation) {
        if (soundFileLocation.contains(":")) {
            return Optional.ofNullable(MinecraftClient.getInstance().getSoundManager()
                    .get(new Identifier(soundFileLocation.toLowerCase())));
        }

        WeightedSoundSet soundSet = soundPaths.get(soundFileLocation);
        return Optional.ofNullable(soundSet);
    }

    public void play(String soundFileName, SoundCategory soundCategory, double x, double y, double z) {
        getSound(soundFileName).ifPresent(soundSet -> MinecraftClient.getInstance().getSoundManager()
                .play(new LMSoundInstance(soundSet, soundCategory, LMMLMod.getConfig().getVoiceVolume(), x, y, z)));
    }
}
