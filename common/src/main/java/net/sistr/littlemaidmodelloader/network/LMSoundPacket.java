package net.sistr.littlemaidmodelloader.network;

import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.SoundPlayable;
import net.sistr.littlemaidmodelloader.util.PlayerList;

public class LMSoundPacket {
    public static final Identifier ID =
            new Identifier(LMMLMod.MODID, "lm_sound");

    public static void sendS2CPacket(Entity entity, String soundName) {
        PacketByteBuf passedData = createS2CPacket(entity, soundName);
        PlayerList.tracking(entity).forEach(watchingPlayer ->
                NetworkManager.sendToPlayer(watchingPlayer, ID, passedData));
    }

    public static PacketByteBuf createS2CPacket(Entity entity, String soundName) {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeVarInt(entity.getEntityId());
        passedData.writeString(soundName);
        return passedData;
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(PacketByteBuf buf, NetworkManager.PacketContext context) {
        int entityId = buf.readVarInt();
        String soundName = buf.readString();
        context.queue(() -> playSoundClient(entityId, soundName));
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
