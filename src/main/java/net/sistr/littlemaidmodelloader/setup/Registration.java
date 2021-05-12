package net.sistr.littlemaidmodelloader.setup;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.sistr.littlemaidmodelloader.client.ModelSelectScreen;
import net.sistr.littlemaidmodelloader.entity.MultiModelEntity;

import static net.sistr.littlemaidmodelloader.LittleMaidModelLoader.MODID;

public class Registration {

    public static void init() {
    }

    public static final EntityType<MultiModelEntity> MULTI_MODEL_ENTITY = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MODID, "multi_model_entity"),
            FabricEntityTypeBuilder.<MultiModelEntity>create(SpawnGroup.MISC, MultiModelEntity::new)
                    .dimensions(EntityDimensions.changing(0.5F, 1.35F))
                    .build()
    );
    public static final EntityType<ModelSelectScreen.DummyModelEntity> DUMMY_MODEL_ENTITY =
            Registry.register(
                    Registry.ENTITY_TYPE,
                    new Identifier(MODID, "dummy_model_entity"),
                    FabricEntityTypeBuilder.<ModelSelectScreen.DummyModelEntity>create(SpawnGroup.MISC,
                            ModelSelectScreen.DummyModelEntity::new)
                            .dimensions(EntityDimensions.changing(0.5F, 1.35F))
                            .disableSummon().build()
            );

}
