package net.sistr.littlemaidmodelloader.setup;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;
import net.sistr.littlemaidmodelloader.client.screen.MultiModelGUIUtil;
import net.sistr.littlemaidmodelloader.entity.MultiModelEntity;

import static net.sistr.littlemaidmodelloader.LMMLMod.MODID;

public class Registration {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(MODID, Registry.ENTITY_TYPE_KEY);

    public static void init() {
        ENTITIES.register();
    }

    public static final RegistrySupplier<EntityType<MultiModelEntity>> MULTI_MODEL_ENTITY =
            ENTITIES.register("multi_model_entity", () ->
                    EntityType.Builder.<MultiModelEntity>create(MultiModelEntity::new, SpawnGroup.MISC)
                            .setDimensions(0.5F, 1.35F)
                            .build("multi_model_entity"));
    public static final RegistrySupplier<EntityType<MultiModelGUIUtil.DummyModelEntity>> DUMMY_MODEL_ENTITY =
            ENTITIES.register("dummy_model_entity", () ->
                    EntityType.Builder.<MultiModelGUIUtil.DummyModelEntity>create(MultiModelGUIUtil.DummyModelEntity::new, SpawnGroup.MISC)
                            .setDimensions(0.5F, 1.35F)
                            .disableSummon()
                            .build("dummy_model_entity"));

}
