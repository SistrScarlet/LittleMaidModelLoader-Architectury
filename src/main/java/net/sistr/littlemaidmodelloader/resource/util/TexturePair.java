package net.sistr.littlemaidmodelloader.resource.util;

import net.minecraft.util.Identifier;

import java.util.Objects;

public class TexturePair {
    private final Identifier texture;
    private final Identifier lightTexture;

    public TexturePair(Identifier texture, Identifier lightTexture) {
        this.texture = texture;
        this.lightTexture = lightTexture;
    }

    public Identifier getTexture(boolean isLight) {
        return isLight ? lightTexture : texture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TexturePair that = (TexturePair) o;
        return Objects.equals(texture, that.texture) &&
                Objects.equals(lightTexture, that.lightTexture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texture, lightTexture);
    }

}
