package net.sistr.lmml.client.resource.holder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.*;
import net.minecraft.util.Identifier;
import net.sistr.lmml.LittleMaidModelLoader;
import net.sistr.lmml.resource.util.ResourceHelper;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Environment(EnvType.CLIENT)
public class SoundHolder {
    private final String name;

    private final Map<String, WeightedSoundSet> sounds = new HashMap<>();

    public SoundHolder(String name) {
        this.name = name;
    }

    public void addSound(String fileName, Identifier resource) {
        WeightedSoundSet soundSet = sounds.computeIfAbsent(fileName.toLowerCase(), k ->
                new WeightedSoundSet(resource, resource.toString().replace("/", ".")));
        soundSet.add(new Sound(resource.toString(), 1F, 1F, 1, Sound.RegistrationType.FILE,
                false, false, 16) {
            @Override
            public Identifier getLocation() {
                return getIdentifier();
            }
        });
    }

    public String getName() {
        return name;
    }

    public Optional<WeightedSoundSet> getSoundSet(String fileName) {
        return Optional.ofNullable(sounds.get(fileName));
    }

}
