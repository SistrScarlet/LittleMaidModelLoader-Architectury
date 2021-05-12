package net.sistr.littlemaidmodelloader.multimodel.animation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.sistr.littlemaidmodelloader.multimodel.model.ModelPart;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class ModelAnimation implements ModelAngleSupplier {
    public final ImmutableMap<ModelPart, PartAnimation> map;
    public final ImmutableSet<ModelPart> set;
    public final float length;

    public ModelAnimation(float length, Map<ModelPart, PartAnimation> map) {
        this.map = ImmutableMap.copyOf(map);
        this.set = ImmutableSet.copyOf(map.keySet());
        this.length = length;
    }

    @Override
    public IAngleList getAngles(float animationProgress) {
        return new AngleList(this, animationProgress);
    }

    public Angle getAngle(ModelPart model, float animationProgress) {
        float percent = (animationProgress % length) / length;
        return map.get(model).getAngle(percent);
    }

    public static final class Builder {
        private final HashMap<ModelPart, PartAnimation.Builder> animatorMap = new HashMap<>();
        private final float length;

        public Builder(float length) {
            this.length = length;
        }

        public Builder noMove(float pitch, float yaw, float roll, ModelPart model) {
            add(0, new Angle(pitch, yaw, roll), model, f -> f);
            return this;
        }

        public Builder noMove(Angle angle, ModelPart model) {
            add(0, angle, model, f -> f);
            return this;
        }

        public Builder first(float pitch, float yaw, float roll, ModelPart model, Interpolator interpolator) {
            add(0, new Angle(pitch, yaw, roll), model, interpolator);
            return this;
        }

        public Builder first(Angle angle, ModelPart model, Interpolator interpolator) {
            add(0, angle, model, interpolator);
            return this;
        }

        public Builder add(float time, float pitch, float yaw, float roll, ModelPart model) {
            add(time, new Angle(pitch, yaw, roll), model, f -> f);
            return this;
        }

        public Builder add(float time, Angle angle, ModelPart model) {
            add(time, angle, model, f -> f);
            return this;
        }

        public Builder add(float time, float pitch, float yaw, float roll, ModelPart model, Interpolator interpolator) {
            add(time, new Angle(pitch, yaw, roll), model, interpolator);
            return this;
        }

        public Builder add(float time, Angle angle, ModelPart model, Interpolator interpolator) {
            animatorMap.computeIfAbsent(model, m -> new PartAnimation.Builder(length))
                    .add(time, angle, interpolator);
            return this;
        }

        public ModelAnimation build() {
            return new ModelAnimation(length, animatorMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().buildLoop())));
        }
    }
}
