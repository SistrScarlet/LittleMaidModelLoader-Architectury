package net.sistr.lmml.entity.compound;

import net.minecraft.entity.Entity;
import net.sistr.lmml.network.LMSoundPacket;
import net.sistr.lmml.network.Networking;
import net.sistr.lmml.resource.holder.ConfigHolder;
import net.sistr.lmml.resource.manager.LMConfigManager;
import net.sistr.lmml.client.resource.manager.LMSoundManager;

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
        configHolder = configManager.getTextureSoundConfig(getPackName()).orElse(
                configManager.getAnyConfig());
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
        if (entity.world.isClient) {
            configHolder.getSoundFileName(soundName.toLowerCase()).ifPresent(soundFileName ->
                    LMSoundManager.INSTANCE.play(soundFileName, entity.getSoundCategory(),
                                    1F, 1F, entity.getX(), entity.getEyeY(), entity.getZ()));
        } else {
            LMSoundPacket.sendS2CPacket(entity, soundName);
        }
    }

}
