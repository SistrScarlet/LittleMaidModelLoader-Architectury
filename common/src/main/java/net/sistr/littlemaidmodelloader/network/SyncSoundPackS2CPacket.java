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
import net.sistr.littlemaidmodelloader.entity.compound.SoundPlayable;
import net.sistr.littlemaidmodelloader.resource.holder.ConfigHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMConfigManager;

public record SyncSoundPackS2CPacket(SyncSoundPackData data) implements CustomPayload {
    public static final CustomPayload.Id<SyncSoundPackS2CPacket> ID =
            new CustomPayload.Id<>(Identifier.of(LMMLMod.MODID, "sync_sound_pack_s2c"));

    public static final PacketCodec<RegistryByteBuf, SyncSoundPackS2CPacket> CODEC =
            PacketCodec.of(
                    (packet, buf) -> SyncSoundPackData.CODEC.encode(buf, packet.data()),
                    buf -> new SyncSoundPackS2CPacket(SyncSoundPackData.CODEC.decode(buf)));

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Environment(EnvType.CLIENT)
    public static void receive(
            SyncSoundPackS2CPacket payload, NetworkManager.PacketContext context) {
        SyncSoundPackData data = payload.data();
        context.queue(() -> applyClient(data.entityId(), data.soundPackName()));
    }

    @Environment(EnvType.CLIENT)
    public static void applyClient(int entityId, String soundPackName) {
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
}
