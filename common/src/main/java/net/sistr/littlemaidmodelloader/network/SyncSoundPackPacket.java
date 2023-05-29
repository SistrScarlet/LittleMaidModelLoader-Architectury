package net.sistr.littlemaidmodelloader.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.SoundPlayable;
import net.sistr.littlemaidmodelloader.resource.holder.ConfigHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMConfigManager;
import net.sistr.littlemaidmodelloader.util.PlayerList;

public class SyncSoundPackPacket {
    public static final Identifier ID =
            new Identifier(LMMLMod.MODID, "sync_sound_pack");

    @Environment(EnvType.CLIENT)
    public static void sendC2SPacket(Entity entity, ConfigHolder configHolder) {
        PacketByteBuf passedData = createC2SPacket(entity, configHolder);
        NetworkManager.sendToServer(ID, passedData);
    }

    public static PacketByteBuf createC2SPacket(Entity entity, ConfigHolder configHolder) {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeInt(entity.getId());
        passedData.writeString(configHolder.getName());
        return passedData;
    }

    public static void sendS2CPacket(Entity entity, ConfigHolder configHolder) {
        PacketByteBuf passedData = createS2CPacket(entity, configHolder);
        NetworkManager.sendToPlayers(PlayerList.tracking(entity), ID, passedData);
    }

    public static PacketByteBuf createS2CPacket(Entity entity, ConfigHolder configHolder) {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeInt(entity.getId());
        passedData.writeString(configHolder.getName());
        return passedData;
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(PacketByteBuf buf, NetworkManager.PacketContext context) {
        int entityId = buf.readInt();
        String soundPackName = buf.readString();
        context.queue(() ->
                applyMultiModelClient(entityId, soundPackName));
    }

    //context.getTaskQueue().execute()の中では@Environmentの効力が及ばないため別メソッドに分離
    @Environment(EnvType.CLIENT)
    public static void applyMultiModelClient(int entityId, String soundPackName) {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return;
        Entity entity = world.getEntityById(entityId);
        if (!(entity instanceof SoundPlayable soundPlayable)) return;
        ConfigHolder configHolder = LMConfigManager.INSTANCE.getConfig(soundPackName).orElse(LMConfigManager.EMPTY_CONFIG);
        soundPlayable.setConfigHolder(configHolder);
    }

    public static void receiveC2SPacket(PacketByteBuf buf, NetworkManager.PacketContext context) {
        int entityId = buf.readInt();
        String soundPackName = buf.readString();
        context.queue(() ->
                applyMultiModelServer(context.getPlayer(), entityId, soundPackName));
    }

    //クライアントに倣って分離
    public static void applyMultiModelServer(PlayerEntity player, int entityId, String soundPackName) {
        Entity entity = player.getWorld().getEntityById(entityId);
        if (!(entity instanceof SoundPlayable soundPlayable)) return;
        ConfigHolder configHolder = LMConfigManager.INSTANCE.getConfig(soundPackName).orElse(LMConfigManager.EMPTY_CONFIG);
        soundPlayable.setConfigHolder(configHolder);
        sendS2CPacket(entity, configHolder);
    }

}
