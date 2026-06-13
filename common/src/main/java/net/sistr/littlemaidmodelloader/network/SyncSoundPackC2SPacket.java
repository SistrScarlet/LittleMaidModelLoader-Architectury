package net.sistr.littlemaidmodelloader.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.SoundPlayable;
import net.sistr.littlemaidmodelloader.resource.holder.ConfigHolder;
import net.sistr.littlemaidmodelloader.resource.manager.LMConfigManager;

public record SyncSoundPackC2SPacket(SyncSoundPackData data) implements CustomPayload {
    public static final CustomPayload.Id<SyncSoundPackC2SPacket> ID =
            new CustomPayload.Id<>(Identifier.of(LMMLMod.MODID, "sync_sound_pack_c2s"));

    public static final PacketCodec<RegistryByteBuf, SyncSoundPackC2SPacket> CODEC =
            PacketCodec.of(
                    (packet, buf) -> SyncSoundPackData.CODEC.encode(buf, packet.data()),
                    buf -> new SyncSoundPackC2SPacket(SyncSoundPackData.CODEC.decode(buf)));

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(
            SyncSoundPackC2SPacket payload, NetworkManager.PacketContext context) {
        SyncSoundPackData data = payload.data();
        context.queue(
                () -> applyServer(context.getPlayer(), data.entityId(), data.soundPackName()));
    }

    public static void applyServer(PlayerEntity player, int entityId, String soundPackName) {
        Entity entity = player.getWorld().getEntityById(entityId);
        if (!(entity instanceof SoundPlayable soundPlayable)) return;
        ConfigHolder configHolder =
                LMConfigManager.INSTANCE
                        .getConfig(soundPackName)
                        .orElse(LMConfigManager.EMPTY_CONFIG);
        soundPlayable.setConfigHolder(configHolder);
        SyncSoundPackPacket.sendS2CPacket(entity, configHolder);
    }
}
