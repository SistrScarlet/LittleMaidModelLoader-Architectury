package net.sistr.littlemaidmodelloader.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LMSoundInstance implements SoundInstance {
    private final WeightedSoundSet soundSet;
    private final Sound sound;
    private final Identifier id;
    private final SoundCategory category;
    private final float volume;
    private final double x;
    private final double y;
    private final double z;

    public LMSoundInstance(WeightedSoundSet soundSet, SoundCategory category,
                           float volume, double x, double y, double z) {
        this.soundSet = soundSet;
        this.sound = soundSet.getSound();
        this.id = sound.getIdentifier();
        this.category = category;
        this.volume = volume;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public WeightedSoundSet getSoundSet(SoundManager soundManager) {
        return this.soundSet;
    }

    @Override
    public Sound getSound() {
        return this.sound;
    }

    @Override
    public SoundCategory getCategory() {
        return category;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public int getRepeatDelay() {
        return 0;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public float getPitch() {
        return 1f;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public AttenuationType getAttenuationType() {
        return AttenuationType.LINEAR;
    }
}
