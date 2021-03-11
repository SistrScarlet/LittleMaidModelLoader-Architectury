package net.sistr.lmml.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
        PlayerLookup.tracking(entity).forEach(watchingPlayer ->
                ServerPlayNetworking.send(watchingPlayer, ID, passedData));
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(MinecraftClient client, ClientPlayNetworkHandler handler,
                                        PacketByteBuf buf, PacketSender responseSender) {
        int entityId = buf.readVarInt();
        String soundName = buf.readString();
        client.execute(() -> playSoundClient(entityId, soundName));
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
