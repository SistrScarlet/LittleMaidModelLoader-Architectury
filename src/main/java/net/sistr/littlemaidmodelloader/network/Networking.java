package net.sistr.littlemaidmodelloader.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.sistr.littlemaidmodelloader.SideChecker;

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
        ClientPlayNetworking.registerGlobalReceiver(SyncMultiModelPacket.ID, SyncMultiModelPacket::receiveS2CPacket);
        ClientPlayNetworking.registerGlobalReceiver(LMSoundPacket.ID, LMSoundPacket::receiveS2CPacket);
        ClientPlayNetworking.registerGlobalReceiver(CustomMobSpawnPacket.ID, CustomMobSpawnPacket::receiveS2CPacket);
    }

    private void serverInit() {
        ServerPlayNetworking.registerGlobalReceiver(SyncMultiModelPacket.ID, SyncMultiModelPacket::receiveC2SPacket);
    }

}
