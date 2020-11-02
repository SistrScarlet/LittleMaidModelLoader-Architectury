package net.sistr.lmml.setup;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.sistr.lmml.entity.MultiModelEntity;

public class ModSetup {

    public static void init() {

        FabricDefaultAttributeRegistry.register(Registration.MULTI_MODEL_ENTITY, MultiModelEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(Registration.DUMMY_MODEL_ENTITY, MultiModelEntity.createMobAttributes());

        Registration.init();

    }

}
