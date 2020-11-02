package net.sistr.lmml.resource.holder;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.sistr.lmml.entity.compound.IHasMultiModel;
import net.sistr.lmml.resource.util.TextureColors;
import net.sistr.lmml.resource.util.TextureIndexes;

import java.util.*;
import java.util.stream.Collectors;

public class TextureHolder {
    private final String name;

    private final String modelName;

    private final Map<Integer, Identifier> textures = new HashMap<>();

    private final Map<String, Map<Integer, Identifier>> armors = new HashMap<>();

    public TextureHolder(String name, String modelName) {
        this.name = name;
        this.modelName = modelName;
    }

    public String getTextureName() {
        return name;
    }

    public String getModelName() {
        return modelName;
    }

    public void addTexture(int index, Identifier texturePath) {
        textures.put(index, texturePath);
    }

    public void addArmorTexture(String armorType, int index, Identifier texturePath) {
        Map<Integer, Identifier> armorMap = armors.computeIfAbsent(armorType.toLowerCase(), k -> new HashMap<>());
        armorMap.put(index, texturePath);
    }

    public Optional<Identifier> getTexture(TextureColors color, boolean isContract, boolean isLight) {
        int index = color.getIndex();
        if (isLight) {
            index += isContract ? TextureIndexes.COLOR_WILD_LIGHT.getIndexMin() : TextureIndexes.COLOR_CONTRACT_LIGHT.getIndexMin();
        } else if (!isContract) {
            index += TextureIndexes.COLOR_WILD.getIndexMin();
        }
        return Optional.ofNullable(textures.get(index));
    }

    public Optional<Identifier> getArmorTexture(IHasMultiModel.Layer layer, String armorName, float damagePercent, boolean isLight) {
        int index;
        switch (layer) {
            case INNER:
                index = (isLight ? TextureIndexes.ARMOR_1_DAMAGED_LIGHT : TextureIndexes.ARMOR_1_DAMAGED).getIndexMin();
                break;
            case OUTER:
                index = (isLight ? TextureIndexes.ARMOR_2_DAMAGED_LIGHT : TextureIndexes.ARMOR_2_DAMAGED).getIndexMin();
                break;
            default:
                throw new IllegalArgumentException("それは防具ではないかnullである");
        }
        int damageIndex = MathHelper.clamp((int) (damagePercent * 10F - 1F), 0, 9);
        Map<Integer, Identifier> armorTextures = armors.get(armorName.toLowerCase());
        if (armorTextures != null && !armorTextures.isEmpty()) {
            //あればそのままのテクスチャを返す
            Identifier armorTexture = armorTextures.get(index + damageIndex);
            if (armorTexture != null) {
                return Optional.of(armorTexture);
            }
            //ないならそれ以前のものを返す
            for (int i = 1; i <= damageIndex; i++) {
                Identifier temp = armorTextures.get(index + damageIndex - i);
                if (temp != null) {
                    return Optional.of(temp);
                }
            }
            //それもないならエイリアスを返す
            if (!isLight) {
                Identifier location;
                if (layer == IHasMultiModel.Layer.INNER) {
                    location = armorTextures.get(TextureIndexes.ARMOR_1_ALIAS.getIndexMin());
                } else {
                    location = armorTextures.get(TextureIndexes.ARMOR_2_ALIAS.getIndexMin());
                }
                if (location != null) {
                    return Optional.of(location);
                }
            }
        }
        //それでもないならデフォルトで再試行
        if (!armorName.toLowerCase().equals("default")) {
            return getArmorTexture(layer, "default", damagePercent, isLight);
        }
        //全く無いならnullを返す
        return Optional.empty();
    }

    public Collection<String> getArmorNames() {
        return armors.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    public boolean hasSkinTexture(boolean isContract) {
        for (TextureColors color : TextureColors.values()) {
            if (getTexture(color, isContract, false).isPresent()
                    || getTexture(color, isContract, true).isPresent())
                return true;
        }
        return false;
    }

    public boolean hasArmorTexture() {
        return !armors.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextureHolder that = (TextureHolder) o;
        return name.equals(that.name) &&
                modelName.equals(that.modelName) &&
                textures.equals(that.textures) &&
                armors.equals(that.armors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, modelName, textures, armors);
    }

}
