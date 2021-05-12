package net.sistr.littlemaidmodelloader.entity.compound;

import net.sistr.littlemaidmodelloader.resource.holder.ConfigHolder;

public interface SoundPlayable {

    void play(String soundName);

    void setConfigHolder(ConfigHolder configHolder);

    ConfigHolder getConfigHolder();

}
