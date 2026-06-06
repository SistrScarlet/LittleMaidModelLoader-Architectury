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
import net.minecraft.world.World;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.SoundPlayable;
import net.sistr.littlemaidmodelloader.resource.holder.ConfigHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMConfigManager;
import net.sistr.littlemaidmodelloader.util.PlayerList;

public record SyncSoundPackPacket(int entityId, String soundPackName) implements CustomPayload {
    public static final CustomPayload.Id<SyncSoundPackPacket> ID =
            new CustomPayload.Id<>(Identifier.of(LMMLMod.MODID, "sync_sound_pack"));

    public static final PacketCodec<RegistryByteBuf, SyncSoundPackPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, SyncSoundPackPacket::entityId,
                    PacketCodecs.STRING, SyncSoundPackPacket::soundPackName,
                    SyncSoundPackPacket::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Environment(EnvType.CLIENT)
    public static void sendC2SPacket(Entity entity, ConfigHolder configHolder) {
        NetworkManager.sendToServer(
                new SyncSoundPackPacket(entity.getId(), configHolder.getName()));
    }

    public static void sendS2CPacket(Entity entity, ConfigHolder configHolder) {
        NetworkManager.sendToPlayers(
                PlayerList.tracking(entity),
                new SyncSoundPackPacket(entity.getId(), configHolder.getName()));
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(
            SyncSoundPackPacket payload, NetworkManager.PacketContext context) {
        context.queue(() -> applyMultiModelClient(payload.entityId(), payload.soundPackName()));
    }

    @Environment(EnvType.CLIENT)
    public static void applyMultiModelClient(int entityId, String soundPackName) {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return;
        Entity entity = world.getEntityById(entityId);
        if (!(entity instanceof SoundPlayable soundPlayable)) return;
        ConfigHolder configHolder =
                LMConfigManager.INSTANCE
                        .getConfig(soundPackName)
                        .orElse(LMConfigManager.EMPTY_CONFIG);
        soundPlayable.setConfigHolder(configHolder);
    }

    public static void receiveC2SPacket(
            SyncSoundPackPacket payload, NetworkManager.PacketContext context) {
        context.queue(
                () ->
                        applyMultiModelServer(
                                context.getPlayer(), payload.entityId(), payload.soundPackName()));
    }

    public static void applyMultiModelServer(
            PlayerEntity player, int entityId, String soundPackName) {
        Entity entity = player.getWorld().getEntityById(entityId);
        if (!(entity instanceof SoundPlayable soundPlayable)) return;
        ConfigHolder configHolder =
                LMConfigManager.INSTANCE
                        .getConfig(soundPackName)
                        .orElse(LMConfigManager.EMPTY_CONFIG);
        soundPlayable.setConfigHolder(configHolder);
        sendS2CPacket(entity, configHolder);
    }
}
