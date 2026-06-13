package net.sistr.littlemaidmodelloader.network;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel.Layer;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel.Part;
import net.sistr.littlemaidmodelloader.resource.manager.LMTextureManager;
import net.sistr.littlemaidmodelloader.resource.util.ArmorSets;
import net.sistr.littlemaidmodelloader.resource.util.TextureColors;

public record SyncMultiModelS2CPacket(SyncMultiModelData data) implements CustomPayload {
    public static final CustomPayload.Id<SyncMultiModelS2CPacket> ID =
            new CustomPayload.Id<>(Identifier.of(LMMLMod.MODID, "sync_multi_model_s2c"));

    public static final PacketCodec<RegistryByteBuf, SyncMultiModelS2CPacket> CODEC =
            PacketCodec.of(
                    (packet, buf) -> SyncMultiModelData.CODEC.encode(buf, packet.data()),
                    buf -> new SyncMultiModelS2CPacket(SyncMultiModelData.CODEC.decode(buf)));

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Environment(EnvType.CLIENT)
    public static void receive(
            SyncMultiModelS2CPacket payload, NetworkManager.PacketContext context) {
        SyncMultiModelData data = payload.data();
        context.queue(
                () ->
                        applyMultiModelClient(
                                data.entityId(),
                                data.isContract(),
                                data.color(),
                                data.textureName(),
                                data.armorTextureName()));
    }

    @Environment(EnvType.CLIENT)
    public static void applyMultiModelClient(
            int entityId,
            boolean isContract,
            TextureColors color,
            String textureName,
            ArmorSets<String> armorTextureName) {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return;
        Entity entity = world.getEntityById(entityId);
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
    }
}
