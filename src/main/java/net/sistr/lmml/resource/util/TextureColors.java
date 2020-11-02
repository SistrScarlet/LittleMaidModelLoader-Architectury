package net.sistr.lmml.resource.util;

public enum TextureColors {
    WHITE(0, 0xFFFFFF),
    ORANGE(1, 0xD87F33),
    MAGENTA(2, 0xB24CD8),
    LIGHT_BLUE(3, 0x6699D8),
    YELLOW(4, 0xE5E533),
    LIME(5, 0x7FCC19),
    PINK(6, 0xF27FA5),
    GRAY(7, 0x4C4C4C),
    LIGHT_GRAY(8, 0x999999),
    CYAN(9, 0x4C7F99),
    PURPLE(10, 0x7F3FB2),
    BLUE(11, 0x334CB2),
    BROWN(12, 0x664C33),
    GREEN(13, 0x667F33),
    RED(14, 0x993333),
    BLACK(15, 0x191919);

    private final int index;
    private final int colorCode;

    TextureColors(int index, int colorCode) {
        this.index = index;
        this.colorCode = colorCode;
    }

    public int getIndex() {
        return index;
    }

    public int getColorCode() {
        return colorCode;
    }

    public static TextureColors getColor(int index) {
        for (TextureColors color : TextureColors.values()) {
            if (color.getIndex() == index) {
                return color;
            }
        }
        throw new IllegalArgumentException("0-15の範囲外の色は指定できません。");
    }
}
