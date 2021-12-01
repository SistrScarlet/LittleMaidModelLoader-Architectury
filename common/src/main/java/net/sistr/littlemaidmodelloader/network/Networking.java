package net.sistr.littlemaidmodelloader.network;

import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Networking {
    public static final Networking INSTANCE = new Networking();

    public void init() {
        if (Platform.getEnv() == EnvType.CLIENT) clientInit();
        serverInit();
    }

    @Environment(EnvType.CLIENT)
    private void clientInit() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncMultiModelPacket.ID, SyncMultiModelPacket::receiveS2CPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, LMSoundPacket.ID, LMSoundPacket::receiveS2CPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CustomMobSpawnPacket.ID, CustomMobSpawnPacket::receiveS2CPacket);
    }

    private void serverInit() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SyncMultiModelPacket.ID, SyncMultiModelPacket::receiveC2SPacket);
    }

}
