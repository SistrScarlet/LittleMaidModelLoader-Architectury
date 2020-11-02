package net.sistr.lmml.resource.util;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Optional;

public class ColorConverter {

    public static Optional<TextureColors> convertDyeColor(Item item) {
        if (item == Items.WHITE_DYE) return Optional.of(TextureColors.WHITE);
        if (item == Items.ORANGE_DYE) return Optional.of(TextureColors.ORANGE);
        if (item == Items.MAGENTA_DYE) return Optional.of(TextureColors.MAGENTA);
        if (item == Items.LIGHT_BLUE_DYE) return Optional.of(TextureColors.LIGHT_BLUE);
        if (item == Items.YELLOW_DYE) return Optional.of(TextureColors.YELLOW);
        if (item == Items.LIME_DYE) return Optional.of(TextureColors.LIME);
        if (item == Items.PINK_DYE) return Optional.of(TextureColors.PINK);
        if (item == Items.GRAY_DYE) return Optional.of(TextureColors.GRAY);
        if (item == Items.LIGHT_GRAY_DYE) return Optional.of(TextureColors.LIGHT_GRAY);
        if (item == Items.CYAN_DYE) return Optional.of(TextureColors.CYAN);
        if (item == Items.PURPLE_DYE) return Optional.of(TextureColors.PURPLE);
        if (item == Items.BLUE_DYE) return Optional.of(TextureColors.BLUE);
        if (item == Items.BROWN_DYE) return Optional.of(TextureColors.BROWN);
        if (item == Items.GREEN_DYE) return Optional.of(TextureColors.GREEN);
        if (item == Items.RED_DYE) return Optional.of(TextureColors.RED);
        if (item == Items.BLACK_DYE) return Optional.of(TextureColors.BLACK);
        return Optional.empty();
    }

}
