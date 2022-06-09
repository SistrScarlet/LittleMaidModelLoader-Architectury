package net.sistr.littlemaidmodelloader.entity.compound;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;
import net.sistr.littlemaidmodelloader.client.resource.manager.LMSoundManager;
import net.sistr.littlemaidmodelloader.network.LMSoundPacket;
import net.sistr.littlemaidmodelloader.resource.holder.ConfigHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMConfigManager;

import java.util.function.Supplier;

public class SoundPlayableCompound implements SoundPlayable {
    private final Entity entity;
    private final Supplier<String> packName;
    private ConfigHolder configHolder;

    public SoundPlayableCompound(Entity entity, Supplier<String> packName) {
        this.entity = entity;
        this.packName = packName;
        update();
    }

    public void update() {
        LMConfigManager configManager = LMConfigManager.INSTANCE;
        configHolder = configManager.getTextureSoundConfig(getPackName())
                .orElse(configManager.getAnyConfig());
    }

    public String getPackName() {
        return packName.get();
    }

    public void setConfigHolder(ConfigHolder configHolder) {
        this.configHolder = configHolder;
    }

    public ConfigHolder getConfigHolder() {
        return this.configHolder;
    }

    @Override
    public void play(String soundName) {
        //todo 音声周りの仕様を1.19に合わせる
        if (entity.world.isClient) {
            configHolder.getSoundFileName(soundName.toLowerCase())
                    .ifPresent(soundFileName ->
                            LMSoundManager.INSTANCE.play(soundFileName, entity.getSoundCategory(),
                                    1F, 1F, Random.create(RandomSeed.getSeed()), entity.getX(), entity.getEyeY(), entity.getZ()));
        } else {
            LMSoundPacket.sendS2CPacket(entity, soundName);
        }
    }

}
