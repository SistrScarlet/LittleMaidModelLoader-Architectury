package net.sistr.lmml.resource.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum TextureIndexes {
    NONE(-1, -1),
    COLOR(0, 0xF),
    ARMOR_1_ALIAS(0x11, 0x11),
    ARMOR_2_ALIAS(0x12, 0x12),
    WILD_ALIAS(0x10, 0x10),
    DEFAULT_CONTRACT_LIGHT(0x13, 0x13),
    DEFAULT_WILD_LIGHT(0x14, 0x14),
    GUI(0x20, 0x20),
    COLOR_WILD(0x30, 0x3F),
    ARMOR_1_DAMAGED(0x40, 0x49),
    ARMOR_2_DAMAGED(0x50, 0x59),
    COLOR_CONTRACT_LIGHT(0x60, 0x6F),
    COLOR_WILD_LIGHT(0x70, 0x7F),
    ARMOR_1_DAMAGED_LIGHT(0x80, 0x89),
    ARMOR_2_DAMAGED_LIGHT(0x90, 0x99);

    private static final Logger LOGGER = LogManager.getLogger();
    private final int indexMin;
    private final int indexMax;

    TextureIndexes(int indexMin, int indexMax) {
        this.indexMin = indexMin;
        this.indexMax = indexMax;
    }

    public boolean isArmor() {
        switch (this) {
            case ARMOR_1_ALIAS:
            case ARMOR_2_ALIAS:
            case ARMOR_1_DAMAGED:
            case ARMOR_2_DAMAGED:
            case ARMOR_1_DAMAGED_LIGHT:
            case ARMOR_2_DAMAGED_LIGHT:
                return true;
            default:
                return false;
        }
    }

    public int getIndexMin() {
        return indexMin;
    }

    public int getIndexMax() {
        return indexMax;
    }

    public static TextureIndexes getTextureIndexes(int index) {
        for (TextureIndexes textureIndex : TextureIndexes.values()) {
            if (textureIndex.getIndexMin() <= index && index <= textureIndex.getIndexMax()) {
                return textureIndex;
            }
        }
        LOGGER.warn("インデックスが存在しません。 : " + index);
        return NONE;
    }
}
