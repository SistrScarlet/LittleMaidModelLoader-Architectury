package net.sistr.littlemaidmodelloader.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record SyncSoundPackData(int entityId, String soundPackName) {
    public static final PacketCodec<RegistryByteBuf, SyncSoundPackData> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT,
                    SyncSoundPackData::entityId,
                    PacketCodecs.STRING,
                    SyncSoundPackData::soundPackName,
                    SyncSoundPackData::new);
}
