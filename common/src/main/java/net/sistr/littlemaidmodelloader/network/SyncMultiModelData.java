package net.sistr.littlemaidmodelloader.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel.Layer;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel.Part;
import net.sistr.littlemaidmodelloader.resource.util.ArmorSets;
import net.sistr.littlemaidmodelloader.resource.util.TextureColors;

public record SyncMultiModelData(
        int entityId,
        String textureName,
        ArmorSets<String> armorTextureName,
        TextureColors color,
        boolean isContract) {

    public static final PacketCodec<RegistryByteBuf, SyncMultiModelData> CODEC =
            PacketCodec.of(
                    (data, buf) -> {
                        buf.writeInt(data.entityId);
                        buf.writeString(data.textureName);
                        for (Part part : Part.values()) {
                            buf.writeString(
                                    data.armorTextureName
                                            .getArmor(part)
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalStateException(
                                                                    "テクスチャが存在しません。")));
                        }
                        buf.writeEnumConstant(data.color);
                        buf.writeBoolean(data.isContract);
                    },
                    buf -> {
                        int entityId = buf.readInt();
                        String textureName = buf.readString();
                        ArmorSets<String> armorTextureName = new ArmorSets<>();
                        for (Part part : Part.values()) {
                            armorTextureName.setArmor(buf.readString(), part);
                        }
                        TextureColors color = buf.readEnumConstant(TextureColors.class);
                        boolean isContract = buf.readBoolean();
                        return new SyncMultiModelData(
                                entityId, textureName, armorTextureName, color, isContract);
                    });

    public static SyncMultiModelData of(Entity entity, IHasMultiModel hasMultiModel) {
        ArmorSets<String> armorTextureName = new ArmorSets<>();
        for (Part part : Part.values()) {
            armorTextureName.setArmor(
                    hasMultiModel.getTextureHolder(Layer.INNER, part).getTextureName(), part);
        }
        return new SyncMultiModelData(
                entity.getId(),
                hasMultiModel.getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName(),
                armorTextureName,
                hasMultiModel.getColorMM(),
                hasMultiModel.isContractMM());
    }
}
