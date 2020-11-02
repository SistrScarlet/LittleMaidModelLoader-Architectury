package net.sistr.lmml.resource.util;

import net.sistr.lmml.entity.compound.IHasMultiModel;

import java.util.Arrays;
import java.util.Optional;
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
