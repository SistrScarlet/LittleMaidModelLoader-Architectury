package net.sistr.littlemaidmodelloader.neoforge;

import net.neoforged.fml.common.Mod;
import net.sistr.littlemaidmodelloader.LMMLMod;

@Mod(LMMLMod.MODID)
public final class LMMLNeoForge {
    public LMMLNeoForge() {
        // Run our common setup.
        LMMLMod.init();
    }
}
