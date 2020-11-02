package net.sistr.lmml.entity.compound;

import net.sistr.lmml.resource.holder.ConfigHolder;

public interface SoundPlayable {

    void play(String soundName);

    void setConfigHolder(ConfigHolder configHolder);

    ConfigHolder getConfigHolder();

}
