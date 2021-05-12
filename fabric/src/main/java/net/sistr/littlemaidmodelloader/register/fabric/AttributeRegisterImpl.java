package net.sistr.littlemaidmodelloader.register.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

import java.util.function.Supplier;

public class AttributeRegisterImpl {

    public static void register(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> attribute) {
        FabricDefaultAttributeRegistry.register(entityType, attribute.get());
    }

}
