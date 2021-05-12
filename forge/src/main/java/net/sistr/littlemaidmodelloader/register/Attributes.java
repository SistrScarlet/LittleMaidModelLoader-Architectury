package net.sistr.littlemaidmodelloader.register;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Attributes {
    private static final List<AttributeSet> attributes = new ArrayList<>();

    public static List<AttributeSet> getAttributes() {
        return attributes;
    }

    public static void add(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> attribute) {
        attributes.add(new AttributeSet(entityType, attribute));
    }

    public static class AttributeSet {
        private final EntityType<? extends LivingEntity> entityType;
        private final Supplier<DefaultAttributeContainer.Builder> attribute;

        public AttributeSet(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> attribute) {
            this.entityType = entityType;
            this.attribute = attribute;
        }

        public EntityType<? extends LivingEntity> getEntityType() {
            return entityType;
        }

        public Supplier<DefaultAttributeContainer.Builder> getAttribute() {
            return attribute;
        }
    }
}
