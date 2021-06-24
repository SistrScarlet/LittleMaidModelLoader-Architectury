package net.sistr.littlemaidmodelloader.multimodel.animation;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import net.sistr.littlemaidmodelloader.util.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class PartAnimation implements AngleSupplier {
    private final ImmutableList<Tuple<RangeChecker, AngleInterpolator>> list;

    public PartAnimation(List<Tuple<RangeChecker, AngleInterpolator>> list) {
        this.list = ImmutableList.copyOf(list);
    }

    public Angle getAngle(float percent) {
        for (Tuple<RangeChecker, AngleInterpolator> tuple : list) {
            RangeChecker rangeChecker = tuple.getA();
            if (rangeChecker.inRange(percent)) {
                return tuple.getB().getAngle(rangeChecker.conv(percent));
            }
        }
        throw new RuntimeException();
    }

    public static final class RangeChecker {
        private final float start;
        private final float end;

        public RangeChecker(float start, float end) {
            this.start = start;
            this.end = end;
        }

        public boolean inRange(float percent) {
            return start <= percent && percent < end;
        }

        public float conv(float percent) {
            return (percent - start) / (end - start);
        }

    }

    public static final class Builder {
        private final Float2ObjectArrayMap<Tuple<Angle, Interpolator>> map = new Float2ObjectArrayMap<>();
        private final float length;
        private Angle firstAngle;

        public Builder(float length) {
            this.length = length;
        }

        public Builder add(float time, Angle angle, Interpolator interpolator) {
            if (time == 0) firstAngle = angle;
            map.put(time, new Tuple<>(angle, interpolator));
            return this;
        }

        private List<Tuple<RangeChecker, AngleInterpolator>> buildList() {
            List<Float2ObjectArrayMap.Entry<Tuple<Angle, Interpolator>>> list = map.float2ObjectEntrySet().stream()
                    .sorted(Comparator.comparingDouble(Float2ObjectArrayMap.Entry::getFloatKey))
                    .collect(Collectors.toList());
            List<Tuple<RangeChecker, AngleInterpolator>> result = new ArrayList<>();
            float prevPercent = list.get(0).getFloatKey() / length;
            Tuple<Angle, Interpolator> prevTemp = list.get(0).getValue();
            for (Float2ObjectArrayMap.Entry<Tuple<Angle, Interpolator>> entry : list) {
                float time = entry.getFloatKey();
                if (time <= 0) continue;
                float percent = time / length;
                result.add(new Tuple<>(
                        new RangeChecker(prevPercent, percent),
                        new AngleInterpolator(prevTemp.getA(), entry.getValue().getA(), prevTemp.getB())));
                prevPercent = percent;
                prevTemp = entry.getValue();
            }
            return result;
        }

        /*public Animator build(Motion lastMotion) {
            map.put(length, new Taple<>(lastMotion, null));
            return new Animator(model, buildList());
        }*/

        public PartAnimation buildLoop() {
            map.put(length, new Tuple<>(firstAngle, null));
            return new PartAnimation(buildList());
        }
    }
}
