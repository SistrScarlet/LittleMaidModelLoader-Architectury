package net.sistr.littlemaidmodelloader.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.sistr.littlemaidmodelloader.LMMLMod;

@Config(name = LMMLMod.MODID)
public class LMMLConfig implements ConfigData {

	//misc

	@ConfigEntry.Category("misc")
	private boolean debugMode;

	public boolean isDebugMode() {
		return this.debugMode;
	}

}
