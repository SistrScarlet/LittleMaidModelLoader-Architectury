package net.sistr.lmml.network.util;

import net.minecraft.network.PacketByteBuf;

public interface CustomPacketEntity {

    void writeCustomPacket(PacketByteBuf packet);

    void readCustomPacket(PacketByteBuf packet);

}
