package net.sistr.littlemaidmodelloader.network;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.util.PlayerList;

// NeoForge 制約: 同じ payload id を方向違いで二重登録できないため、内部的に S2C / C2S
// 専用 payload に分離している。外向け API はこの facade に集約。
public final class SyncMultiModelPacket {
    private SyncMultiModelPacket() {}

    @Environment(EnvType.CLIENT)
    public static void sendC2SPacket(Entity entity, IHasMultiModel hasMultiModel) {
        NetworkManager.sendToServer(
                new SyncMultiModelC2SPacket(SyncMultiModelData.of(entity, hasMultiModel)));
    }

    public static void sendS2CPacket(Entity entity, IHasMultiModel hasMultiModel) {
        NetworkManager.sendToPlayers(
                PlayerList.tracking(entity),
                new SyncMultiModelS2CPacket(SyncMultiModelData.of(entity, hasMultiModel)));
    }
}
