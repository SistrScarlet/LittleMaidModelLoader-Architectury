package net.sistr.littlemaidmodelloader.resource.util;

import com.google.common.collect.Lists;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.multimodel.IMultiModel;
import net.sistr.littlemaidmodelloader.resource.holder.TextureHolder;

import java.lang.ref.WeakReference;
import java.util.*;

//防具の1部位のデータ保持用クラス
public class ArmorPart {
    private final TexturePair innerTex;
    private final TexturePair outerTex;
    private final IMultiModel innerModel;
    private final IMultiModel outerModel;

    public ArmorPart(Identifier innerTex, Identifier innerTexLight,
                     Identifier outerTex, Identifier outerTexLight,
                     IMultiModel innerModel, IMultiModel outerModel) {
        this.innerTex = new TexturePair(innerTex, innerTexLight);
        this.outerTex = new TexturePair(outerTex, outerTexLight);
        this.innerModel = innerModel;
        this.outerModel = outerModel;
    }

    public Identifier getTexture(IHasMultiModel.Layer layer, boolean isLight) {
        if (!layer.isArmor()) {
            throw new IllegalArgumentException("取得できません。");
        }
        if (layer == IHasMultiModel.Layer.INNER) {
            return innerTex.getTexture(isLight);
        } else {
            return outerTex.getTexture(isLight);
        }
    }

    public IMultiModel getModel(IHasMultiModel.Layer layer) {
        if (!layer.isArmor()) {
            throw new IllegalArgumentException("取得できません。");
        }
        if (layer == IHasMultiModel.Layer.INNER) {
            return innerModel;
        } else {
            return outerModel;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmorPart that = (ArmorPart) o;
        return Objects.equals(innerTex, that.innerTex) &&
                Objects.equals(outerTex, that.outerTex) &&
                Objects.equals(innerModel, that.innerModel) &&
                Objects.equals(outerModel, that.outerModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerTex, outerTex, innerModel, outerModel);
    }

    public static final class Builder {
        private static final Map<TextureHolder, List<WeakReference<ArmorPart>>> REFERENCES = new HashMap<>();
        private Identifier innerTex;
        private Identifier innerTexLight;
        private Identifier outerTex;
        private Identifier outerTexLight;
        private IMultiModel innerModel;
        private IMultiModel outerModel;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder innerTex(Identifier innerTex) {
            this.innerTex = innerTex;
            return this;
        }

        public Builder innerTexLight(Identifier innerTexLight) {
            this.innerTexLight = innerTexLight;
            return this;
        }

        public Builder outerTex(Identifier outerTex) {
            this.outerTex = outerTex;
            return this;
        }

        public Builder outerTexLight(Identifier outerTexLight) {
            this.outerTexLight = outerTexLight;
            return this;
        }

        public Builder innerModel(IMultiModel innerModel) {
            this.innerModel = innerModel;
            return this;
        }

        public Builder outerModel(IMultiModel outerModel) {
            this.outerModel = outerModel;
            return this;
        }

        public ArmorPart build() {
            return new ArmorPart(innerTex, innerTexLight, outerTex, outerTexLight, innerModel, outerModel);
            //getNewDataAndCache(textureHolder, innerTex, innerTexLight, outerTex, outerTexLight, innerModel, outerModel);
        }

        //参照されてる=他のメイドが使用してる防具パーツモデルは使いまわす
        //メモリを削減できたとしても負荷的にはどうなのか…早すぎる最適化な気もする
        private static ArmorPart getNewDataAndCache(TextureHolder textureHolder,
                                                    Identifier innerTex, Identifier innerTexLight,
                                                    Identifier outerTex, Identifier outerTexLight,
                                                    IMultiModel innerModel, IMultiModel outerModel) {
            refreshReferences();
            List<WeakReference<ArmorPart>> references = REFERENCES.get(textureHolder);
            if (references != null) {
                Iterator<WeakReference<ArmorPart>> iterator = references.iterator();
                while (iterator.hasNext()) {
                    WeakReference<ArmorPart> reference = iterator.next();
                    ArmorPart weakData = reference.get();
                    if (weakData == null) {
                        iterator.remove();
                    } else if (weakData.getTexture(IHasMultiModel.Layer.INNER, false) == innerTex) {
                        return weakData;
                    }
                }
            }
            ArmorPart data = new ArmorPart(
                    innerTex, innerTexLight, outerTex, outerTexLight, innerModel, outerModel);
            REFERENCES.put(textureHolder, Lists.newArrayList(new WeakReference<>(data)));
            return data;
        }

        private static void refreshReferences() {
            REFERENCES.forEach((texturePackage, references) -> {
                references.removeIf(reference -> reference.get() == null);
                if (references.size() <= 0) {
                    REFERENCES.remove(texturePackage);
                }
            });

        }

    }
}
