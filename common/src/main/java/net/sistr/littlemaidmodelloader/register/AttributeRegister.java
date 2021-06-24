package net.sistr.littlemaidmodelloader.register;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

import java.util.function.Supplier;

public class AttributeRegister {

    @ExpectPlatform
    public static void register(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> attribute) {
        throw new AssertionError();
    }

}
