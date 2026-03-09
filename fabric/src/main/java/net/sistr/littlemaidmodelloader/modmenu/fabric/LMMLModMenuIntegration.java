package net.sistr.littlemaidmodelloader.modmenu.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.sistr.littlemaidmodelloader.config.LMMLConfig;

public class LMMLModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(LMMLConfig.class, parent).get();
    }
}
