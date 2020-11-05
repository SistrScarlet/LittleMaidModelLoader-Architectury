package net.sistr.lmml.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.sistr.lmml.SideChecker;

public class Networking {
    public static final Networking INSTANCE = new Networking();

    public void init() {
        if (SideChecker.isClient()) {
            clientInit();
        }
        serverInit();
    }

    @Environment(EnvType.CLIENT)
    private void clientInit() {
        ClientSidePacketRegistry.INSTANCE.register(SyncMultiModelPacket.ID, SyncMultiModelPacket::receiveS2CPacket);
        ClientSidePacketRegistry.INSTANCE.register(LMSoundPacket.ID, LMSoundPacket::receiveS2CPacket);
        ClientSidePacketRegistry.INSTANCE.register(CustomMobSpawnPacket.ID, CustomMobSpawnPacket::receiveS2CPacket);
    }

    private void serverInit() {
        ServerSidePacketRegistry.INSTANCE.register(SyncMultiModelPacket.ID, SyncMultiModelPacket::receiveC2SPacket);
    }

}
