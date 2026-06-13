package net.sistr.littlemaidmodelloader.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel.Layer;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel.Part;
import net.sistr.littlemaidmodelloader.resource.manager.LMTextureManager;
import net.sistr.littlemaidmodelloader.resource.util.ArmorSets;
import net.sistr.littlemaidmodelloader.resource.util.TextureColors;

public record SyncMultiModelC2SPacket(SyncMultiModelData data) implements CustomPayload {
    public static final CustomPayload.Id<SyncMultiModelC2SPacket> ID =
            new CustomPayload.Id<>(Identifier.of(LMMLMod.MODID, "sync_multi_model_c2s"));

    public static final PacketCodec<RegistryByteBuf, SyncMultiModelC2SPacket> CODEC =
            PacketCodec.of(
                    (packet, buf) -> SyncMultiModelData.CODEC.encode(buf, packet.data()),
                    buf -> new SyncMultiModelC2SPacket(SyncMultiModelData.CODEC.decode(buf)));

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(
            SyncMultiModelC2SPacket payload, NetworkManager.PacketContext context) {
        SyncMultiModelData data = payload.data();
        context.queue(
                () ->
                        applyMultiModelServer(
                                context.getPlayer(),
                                data.entityId(),
                                data.isContract(),
                                data.color(),
                                data.textureName(),
                                data.armorTextureName()));
    }

    public static void applyMultiModelServer(
            PlayerEntity player,
            int entityId,
            boolean isContract,
            TextureColors color,
            String textureName,
            ArmorSets<String> armorTextureName) {
        Entity entity = player.getWorld().getEntityById(entityId);
        if (!(entity instanceof IHasMultiModel multiModel)) return;
        multiModel.setContractMM(isContract);
        multiModel.setColorMM(color);
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
        textureManager
                .getTexture(textureName)
                .filter(
                        textureHolder ->
                                multiModel.isAllowChangeTexture(
                                        entity, textureHolder, Layer.SKIN, Part.HEAD))
                .ifPresent(
                        textureHolder ->
                                multiModel.setTextureHolder(textureHolder, Layer.SKIN, Part.HEAD));
        for (Part part : Part.values()) {
            String armorName =
                    armorTextureName
                            .getArmor(part)
                            .orElseThrow(() -> new IllegalStateException("テクスチャが存在しません。"));
            textureManager
                    .getTexture(armorName)
                    .filter(
                            textureHolder ->
                                    multiModel.isAllowChangeTexture(
                                            entity, textureHolder, Layer.INNER, part))
                    .ifPresent(
                            textureHolder ->
                                    multiModel.setTextureHolder(textureHolder, Layer.INNER, part));
        }
        SyncMultiModelPacket.sendS2CPacket(entity, multiModel);
    }
}
