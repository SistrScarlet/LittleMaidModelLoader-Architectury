package net.sistr.littlemaidmodelloader.network;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.SoundPlayable;
import net.sistr.littlemaidmodelloader.util.PlayerList;

public record LMSoundPacket(int entityId, String soundName) implements CustomPayload {
    public static final CustomPayload.Id<LMSoundPacket> ID =
            new CustomPayload.Id<>(Identifier.of(LMMLMod.MODID, "lm_sound"));

    public static final PacketCodec<RegistryByteBuf, LMSoundPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT,
                    LMSoundPacket::entityId,
                    PacketCodecs.STRING,
                    LMSoundPacket::soundName,
                    LMSoundPacket::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void sendS2CPacket(Entity entity, String soundName) {
        LMSoundPacket payload = new LMSoundPacket(entity.getId(), soundName);
        NetworkManager.sendToPlayers(
                PlayerList.tracking(entity).stream()
                        .filter(p -> p.squaredDistanceTo(entity) < 16 * 16)
                        .toList(),
                payload);
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(
            LMSoundPacket payload, NetworkManager.PacketContext context) {
        context.queue(() -> playSoundClient(payload.entityId(), payload.soundName()));
    }

    @Environment(EnvType.CLIENT)
    public static void playSoundClient(int entityId, String soundName) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        Entity entity = player.getWorld().getEntityById(entityId);
        if (entity instanceof SoundPlayable) {
            ((SoundPlayable) entity).play(soundName);
        }
    }
}
