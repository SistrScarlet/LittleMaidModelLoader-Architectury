package net.sistr.littlemaidmodelloader.network;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.sistr.littlemaidmodelloader.util.PlayerList;

public record SyncMultiModelPacket(
        int entityId,
        String textureName,
        ArmorSets<String> armorTextureName,
        TextureColors color,
        boolean isContract)
        implements CustomPayload {
    public static final CustomPayload.Id<SyncMultiModelPacket> ID =
            new CustomPayload.Id<>(Identifier.of(LMMLMod.MODID, "sync_multi_model"));

    public static final PacketCodec<RegistryByteBuf, SyncMultiModelPacket> CODEC =
            PacketCodec.of(
                    (packet, buf) -> {
                        buf.writeInt(packet.entityId);
                        buf.writeString(packet.textureName);
                        for (Part part : Part.values()) {
                            buf.writeString(
                                    packet.armorTextureName
                                            .getArmor(part)
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalStateException(
                                                                    "テクスチャが存在しません。")));
                        }
                        buf.writeEnumConstant(packet.color);
                        buf.writeBoolean(packet.isContract);
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
                        return new SyncMultiModelPacket(
                                entityId, textureName, armorTextureName, color, isContract);
                    });

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static SyncMultiModelPacket of(Entity entity, IHasMultiModel hasMultiModel) {
        ArmorSets<String> armorTextureName = new ArmorSets<>();
        for (Part part : Part.values()) {
            armorTextureName.setArmor(
                    hasMultiModel.getTextureHolder(Layer.INNER, part).getTextureName(), part);
        }
        return new SyncMultiModelPacket(
                entity.getId(),
                hasMultiModel.getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName(),
                armorTextureName,
                hasMultiModel.getColorMM(),
                hasMultiModel.isContractMM());
    }

    @Environment(EnvType.CLIENT)
    public static void sendC2SPacket(Entity entity, IHasMultiModel hasMultiModel) {
        NetworkManager.sendToServer(of(entity, hasMultiModel));
    }

    public static void sendS2CPacket(Entity entity, IHasMultiModel hasMultiModel) {
        NetworkManager.sendToPlayers(PlayerList.tracking(entity), of(entity, hasMultiModel));
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(
            SyncMultiModelPacket payload, NetworkManager.PacketContext context) {
        context.queue(
                () ->
                        applyMultiModelClient(
                                payload.entityId(),
                                payload.isContract(),
                                payload.color(),
                                payload.textureName(),
                                payload.armorTextureName()));
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

    public static void receiveC2SPacket(
            SyncMultiModelPacket payload, NetworkManager.PacketContext context) {
        context.queue(
                () ->
                        applyMultiModelServer(
                                context.getPlayer(),
                                payload.entityId(),
                                payload.isContract(),
                                payload.color(),
                                payload.textureName(),
                                payload.armorTextureName()));
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
        sendS2CPacket(entity, multiModel);
    }
}
