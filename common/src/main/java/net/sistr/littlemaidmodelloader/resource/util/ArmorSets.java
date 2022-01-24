package net.sistr.littlemaidmodelloader.resource.util;

import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * アーマー4部位の保持クラス
 * */
public class ArmorSets<T> {
    private final Object[] parts = new Object[4];

    public void setArmor(T type, IHasMultiModel.Part part) {
        parts[part.getIndex()] = type;
    }

    @SuppressWarnings("unchecked")
    public Optional<T> getArmor(IHasMultiModel.Part part) {
        return Optional.ofNullable((T) parts[part.getIndex()]);
    }

    public <M> ArmorSets<M> convert(Function<T, M> converter) {
        ArmorSets<M> armorSets = new ArmorSets<>();
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            this.getArmor(part).ifPresent(type -> armorSets.setArmor(converter.apply(type), part));
        }
        return armorSets;
    }

    @SuppressWarnings("unchecked")
    public void foreach(BiConsumer<IHasMultiModel.Part, T> consumer) {
        T[] parts = (T[]) this.parts;
        if (parts[0] != null) consumer.accept(IHasMultiModel.Part.FEET, parts[0]);
        if (parts[1] != null) consumer.accept(IHasMultiModel.Part.LEGS, parts[1]);
        if (parts[2] != null) consumer.accept(IHasMultiModel.Part.BODY, parts[2]);
        if (parts[3] != null) consumer.accept(IHasMultiModel.Part.HEAD, parts[3]);
    }

    public void clear() {
        parts[0] = null;
        parts[1] = null;
        parts[2] = null;
        parts[3] = null;
    }

    public boolean isEmpty() {
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            if (this.getArmor(part).isPresent()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmorSets<?> that = (ArmorSets<?>) o;
        return Arrays.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(parts);
    }
}
