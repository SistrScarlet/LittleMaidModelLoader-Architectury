package net.sistr.littlemaidmodelloader.register.forge;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.sistr.littlemaidmodelloader.register.Attributes;

import java.util.function.Supplier;

public class AttributeRegisterImpl {

    public static void register(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> attribute) {
        Attributes.add(entityType, attribute);
    }

}
