package net.sistr.littlemaidmodelloader.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Networking {
    public static final Networking INSTANCE = new Networking();

    public void init() {
        if (Platform.getEnv() == EnvType.CLIENT) {
            // クライアントでは registerReceiver(S2C, ...) が type 登録も内部で行うため、
            // 明示登録は不要 (二重登録すると Fabric の PayloadTypeRegistry が IllegalArgumentException)。
            clientInit();
        } else {
            // 専用サーバには S2C receiver が無いため、送信用に type 登録のみ明示する。
            NetworkManager.registerS2CPayloadType(SyncMultiModelPacket.ID, SyncMultiModelPacket.CODEC);
            NetworkManager.registerS2CPayloadType(SyncSoundPackPacket.ID, SyncSoundPackPacket.CODEC);
            NetworkManager.registerS2CPayloadType(LMSoundPacket.ID, LMSoundPacket.CODEC);
        }
        serverInit();
    }

    @Environment(EnvType.CLIENT)
    private void clientInit() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                SyncMultiModelPacket.ID,
                SyncMultiModelPacket.CODEC,
                SyncMultiModelPacket::receiveS2CPacket);
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                SyncSoundPackPacket.ID,
                SyncSoundPackPacket.CODEC,
                SyncSoundPackPacket::receiveS2CPacket);
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                LMSoundPacket.ID,
                LMSoundPacket.CODEC,
                LMSoundPacket::receiveS2CPacket);
    }

    private void serverInit() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SyncMultiModelPacket.ID,
                SyncMultiModelPacket.CODEC,
                SyncMultiModelPacket::receiveC2SPacket);
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SyncSoundPackPacket.ID,
                SyncSoundPackPacket.CODEC,
                SyncSoundPackPacket::receiveC2SPacket);
    }
}
