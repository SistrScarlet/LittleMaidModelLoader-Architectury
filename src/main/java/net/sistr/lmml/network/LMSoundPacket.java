package net.sistr.lmml.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.sistr.lmml.LittleMaidModelLoader;
import net.sistr.lmml.entity.compound.SoundPlayable;

public class LMSoundPacket {
    public static final Identifier ID =
            new Identifier(LittleMaidModelLoader.MODID, "lm_sound");

    public static void sendS2CPacket(Entity entity, String soundName) {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeVarInt(entity.getEntityId());
        passedData.writeString(soundName);
        PlayerStream.watching(entity).forEach(watchingPlayer ->
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(watchingPlayer, ID, passedData));
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(PacketContext context, PacketByteBuf attachedData) {
        int entityId = attachedData.readVarInt();
        String soundName = attachedData.readString();
        context.getTaskQueue().execute(() ->
                playSoundClient(entityId, soundName));
    }

    @Environment(EnvType.CLIENT)
    public static void playSoundClient(int entityId, String soundName) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        Entity entity = player.world.getEntityById(entityId);
        if (entity instanceof SoundPlayable) {
            ((SoundPlayable) entity).play(soundName);
        }
    }

}
