package net.sistr.lmml.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sistr.lmml.LittleMaidModelLoader;
import net.sistr.lmml.entity.compound.IHasMultiModel;
import net.sistr.lmml.resource.manager.LMTextureManager;
import net.sistr.lmml.resource.util.ArmorSets;
import net.sistr.lmml.resource.util.TextureColors;

public class SyncMultiModelPacket {
    public static final Identifier ID =
            new Identifier(LittleMaidModelLoader.MODID, "sync_multi_model");

    @Environment(EnvType.CLIENT)
    public static void sendC2SPacket(Entity entity, IHasMultiModel hasMultiModel) {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeInt(entity.getEntityId());
        passedData.writeString(hasMultiModel.getTextureHolder(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .getTextureName());
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            passedData.writeString(hasMultiModel.getTextureHolder(IHasMultiModel.Layer.INNER, part).getTextureName());
        }
        passedData.writeEnumConstant(hasMultiModel.getColor());
        passedData.writeBoolean(hasMultiModel.isContract());
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, passedData);
    }

    public static void sendS2CPacket(Entity entity, IHasMultiModel hasMultiModel) {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeInt(entity.getEntityId());
        passedData.writeString(hasMultiModel.getTextureHolder(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .getTextureName());
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            passedData.writeString(hasMultiModel.getTextureHolder(IHasMultiModel.Layer.INNER, part).getTextureName());
        }
        passedData.writeEnumConstant(hasMultiModel.getColor());
        passedData.writeBoolean(hasMultiModel.isContract());
        PlayerStream.watching(entity).forEach(watchingPlayer ->
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(watchingPlayer, ID, passedData));
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(PacketContext context, PacketByteBuf attachedData) {
        int entityId = attachedData.readInt();
        String textureName = attachedData.readString();
        ArmorSets<String> armorTextureName = new ArmorSets<>();
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            armorTextureName.setArmor(attachedData.readString(), part);
        }
        TextureColors color = attachedData.readEnumConstant(TextureColors.class);
        boolean isContract = attachedData.readBoolean();
        context.getTaskQueue().execute(() -> {
            World world = getWorld();
            if (world == null) return;
            Entity entity = world.getEntityById(entityId);
            if (!(entity instanceof IHasMultiModel)) return;
            IHasMultiModel multiModel = (IHasMultiModel) entity;
            multiModel.setColor(color);
            multiModel.setContract(isContract);
            LMTextureManager textureManager = LMTextureManager.INSTANCE;
            textureManager.getTexture(textureName).filter(textureHolder ->
                    multiModel.isAllowChangeTexture(entity, textureHolder, IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD))
                    .ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD));
            for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
                String armorName = armorTextureName.getArmor(part)
                        .orElseThrow(() -> new IllegalStateException("テクスチャが存在しません。"));
                textureManager.getTexture(armorName).filter(textureHolder ->
                        multiModel.isAllowChangeTexture(entity, textureHolder, IHasMultiModel.Layer.INNER, part))
                        .ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, IHasMultiModel.Layer.INNER, part));
            }
        });
    }

    //context.getTaskQueue().execute()の中で@Environmentの効力が及ばないみたい
    @Environment(EnvType.CLIENT)
    private static World getWorld() {
        return MinecraftClient.getInstance().world;
    }

    public static void receiveC2SPacket(PacketContext packetContext, PacketByteBuf attachedData) {
        PlayerEntity player = packetContext.getPlayer();
        int entityId = attachedData.readInt();
        String textureName = attachedData.readString(32767);
        ArmorSets<String> armorTextureName = new ArmorSets<>();
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            armorTextureName.setArmor(attachedData.readString(32767), part);
        }
        TextureColors color = attachedData.readEnumConstant(TextureColors.class);
        boolean isContract = attachedData.readBoolean();
        packetContext.getTaskQueue().execute(() -> {
            Entity entity = player.world.getEntityById(entityId);
            if (!(entity instanceof IHasMultiModel)) return;
            IHasMultiModel multiModel = (IHasMultiModel) entity;
            multiModel.setColor(color);
            multiModel.setContract(isContract);
            LMTextureManager textureManager = LMTextureManager.INSTANCE;
            textureManager.getTexture(textureName).filter(textureHolder ->
                    multiModel.isAllowChangeTexture(entity, textureHolder, IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD))
                    .ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD));
            for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
                String armorName = armorTextureName.getArmor(part)
                        .orElseThrow(() -> new IllegalStateException("テクスチャが存在しません。"));
                textureManager.getTexture(armorName).filter(textureHolder ->
                        multiModel.isAllowChangeTexture(entity, textureHolder, IHasMultiModel.Layer.INNER, part))
                        .ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, IHasMultiModel.Layer.INNER, part));
            }
            sendS2CPacket(entity, multiModel);
        });
    }

}
