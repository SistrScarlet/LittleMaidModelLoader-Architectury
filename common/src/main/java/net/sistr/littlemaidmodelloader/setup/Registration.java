package net.sistr.littlemaidmodelloader.setup;

import me.shedaniel.architectury.registry.DeferredRegister;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;
import net.sistr.littlemaidmodelloader.client.screen.ModelSelectScreen;
import net.sistr.littlemaidmodelloader.entity.MultiModelEntity;

import static net.sistr.littlemaidmodelloader.LittleMaidModelLoader.MODID;

public class Registration {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(MODID, Registry.ENTITY_TYPE_KEY);

    public static void init() {
        ENTITIES.register();
    }

    public static final EntityType<MultiModelEntity> MULTI_MODEL_ENTITY_BEFORE =
            EntityType.Builder.<MultiModelEntity>create(MultiModelEntity::new, SpawnGroup.MISC)
                    .setDimensions(0.5F, 1.35F)
                    .build("");
    public static final RegistrySupplier<EntityType<MultiModelEntity>> MULTI_MODEL_ENTITY =
            ENTITIES.register("multi_model_entity", () -> MULTI_MODEL_ENTITY_BEFORE);
    public static final EntityType<ModelSelectScreen.DummyModelEntity> DUMMY_MODEL_ENTITY_BEFORE =
            EntityType.Builder.<ModelSelectScreen.DummyModelEntity>create(ModelSelectScreen.DummyModelEntity::new, SpawnGroup.MISC)
                    .setDimensions(0.5F, 1.35F)
                    .disableSummon()
                    .build("");
    public static final RegistrySupplier<EntityType<ModelSelectScreen.DummyModelEntity>> DUMMY_MODEL_ENTITY =
            ENTITIES.register("dummy_model_entity", () -> DUMMY_MODEL_ENTITY_BEFORE);

}
