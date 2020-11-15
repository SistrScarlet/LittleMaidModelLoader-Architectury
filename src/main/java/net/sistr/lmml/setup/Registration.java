package net.sistr.lmml.setup;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.sistr.lmml.client.ModelSelectScreen;
import net.sistr.lmml.entity.MultiModelEntity;

import static net.sistr.lmml.LittleMaidModelLoader.MODID;

public class Registration {

    public static void init() {
    }

    public static final EntityType<MultiModelEntity> MULTI_MODEL_ENTITY = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MODID, "multi_model_entity"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, MultiModelEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8F, 1.2F)).build()
    );
    public static final EntityType<ModelSelectScreen.DummyModelEntity> DUMMY_MODEL_ENTITY =
            Registry.register(
                    Registry.ENTITY_TYPE,
                    new Identifier(MODID, "dummy_model_entity"),
                    FabricEntityTypeBuilder.<ModelSelectScreen.DummyModelEntity>create(SpawnGroup.MISC,
                            ModelSelectScreen.DummyModelEntity::new)
                            .dimensions(EntityDimensions.fixed(0.8F, 1.2F))
                            .disableSummon().build()
            );

}