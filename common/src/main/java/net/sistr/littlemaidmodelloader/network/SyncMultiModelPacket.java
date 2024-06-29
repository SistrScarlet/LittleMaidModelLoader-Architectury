package net.sistr.littlemaidmodelloader.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
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

public class SyncMultiModelPacket {
    public static final Identifier ID =
            Identifier.of(LMMLMod.MODID, "sync_multi_model");

    @Environment(EnvType.CLIENT)
    public static void sendC2SPacket(Entity entity, IHasMultiModel hasMultiModel, DynamicRegistryManager access) {
        var passedData = createC2SPacket(entity, hasMultiModel, access);
        NetworkManager.sendToServer(ID, passedData);
    }

    public static RegistryByteBuf createC2SPacket(Entity entity, IHasMultiModel hasMultiModel,
                                                  DynamicRegistryManager access) {
        var passedData = new RegistryByteBuf(Unpooled.buffer(), access);
        passedData.writeInt(entity.getId());
        passedData.writeString(hasMultiModel.getTextureHolder(Layer.SKIN, Part.HEAD)
                .getTextureName());
        for (Part part : Part.values()) {
            passedData.writeString(hasMultiModel.getTextureHolder(Layer.INNER, part).getTextureName());
        }
        passedData.writeEnumConstant(hasMultiModel.getColorMM());
        passedData.writeBoolean(hasMultiModel.isContractMM());
        return passedData;
    }

    public static void sendS2CPacket(Entity entity, IHasMultiModel hasMultiModel, DynamicRegistryManager access) {
        var passedData = createS2CPacket(entity, hasMultiModel, access);
        NetworkManager.sendToPlayers(PlayerList.tracking(entity), ID, passedData);
    }

    public static RegistryByteBuf createS2CPacket(Entity entity, IHasMultiModel hasMultiModel,
                                                  DynamicRegistryManager access) {
        var passedData = new RegistryByteBuf(Unpooled.buffer(), access);
        passedData.writeInt(entity.getId());
        passedData.writeString(hasMultiModel.getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
        for (Part part : Part.values()) {
            passedData.writeString(hasMultiModel.getTextureHolder(Layer.INNER, part).getTextureName());
        }
        passedData.writeEnumConstant(hasMultiModel.getColorMM());
        passedData.writeBoolean(hasMultiModel.isContractMM());
        return passedData;
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(PacketByteBuf buf, NetworkManager.PacketContext context) {
        int entityId = buf.readInt();
        String textureName = buf.readString();
        ArmorSets<String> armorTextureName = new ArmorSets<>();
        for (Part part : Part.values()) {
            armorTextureName.setArmor(buf.readString(), part);
        }
        TextureColors color = buf.readEnumConstant(TextureColors.class);
        boolean isContract = buf.readBoolean();
        context.queue(() ->
                applyMultiModelClient(entityId, isContract, color, textureName, armorTextureName));
    }

    //context.getTaskQueue().execute()の中では@Environmentの効力が及ばないため別メソッドに分離
    @Environment(EnvType.CLIENT)
    public static void applyMultiModelClient(int entityId, boolean isContract, TextureColors color,
                                             String textureName, ArmorSets<String> armorTextureName) {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return;
        Entity entity = world.getEntityById(entityId);
        if (!(entity instanceof IHasMultiModel multiModel)) return;
        multiModel.setContractMM(isContract);
        multiModel.setColorMM(color);
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
        textureManager.getTexture(textureName).filter(textureHolder ->
                        multiModel.isAllowChangeTexture(entity, textureHolder, Layer.SKIN, Part.HEAD))
                .ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, Layer.SKIN, Part.HEAD));
        for (Part part : Part.values()) {
            String armorName = armorTextureName.getArmor(part)
                    .orElseThrow(() -> new IllegalStateException("テクスチャが存在しません。"));
            textureManager.getTexture(armorName).filter(textureHolder ->
                            multiModel.isAllowChangeTexture(entity, textureHolder, Layer.INNER, part))
                    .ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, Layer.INNER, part));
        }
    }

    public static void receiveC2SPacket(PacketByteBuf buf, NetworkManager.PacketContext context) {
        int entityId = buf.readInt();
        String textureName = buf.readString();
        ArmorSets<String> armorTextureName = new ArmorSets<>();
        for (Part part : Part.values()) {
            armorTextureName.setArmor(buf.readString(), part);
        }
        TextureColors color = buf.readEnumConstant(TextureColors.class);
        boolean isContract = buf.readBoolean();
        context.queue(() ->
                applyMultiModelServer(context.getPlayer(), entityId, isContract, color, textureName, armorTextureName,
                        context.registryAccess()));
    }

    //クライアントに倣って分離
    public static void applyMultiModelServer(PlayerEntity player, int entityId, boolean isContract, TextureColors color,
                                             String textureName, ArmorSets<String> armorTextureName,
                                             DynamicRegistryManager access) {
        Entity entity = player.getWorld().getEntityById(entityId);
        if (!(entity instanceof IHasMultiModel multiModel)) return;
        multiModel.setContractMM(isContract);
        multiModel.setColorMM(color);
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
        textureManager.getTexture(textureName).filter(textureHolder ->
                        multiModel.isAllowChangeTexture(entity, textureHolder, Layer.SKIN, Part.HEAD))
                .ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, Layer.SKIN, Part.HEAD));
        for (Part part : Part.values()) {
            String armorName = armorTextureName.getArmor(part)
                    .orElseThrow(() -> new IllegalStateException("テクスチャが存在しません。"));
            textureManager.getTexture(armorName).filter(textureHolder ->
                            multiModel.isAllowChangeTexture(entity, textureHolder, Layer.INNER, part))
                    .ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, Layer.INNER, part));
        }
        sendS2CPacket(entity, multiModel, access);
    }

}
