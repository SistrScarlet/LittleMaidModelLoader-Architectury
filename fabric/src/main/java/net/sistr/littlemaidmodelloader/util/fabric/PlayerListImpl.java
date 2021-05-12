package net.sistr.littlemaidmodelloader.util.fabric;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class PlayerListImpl {

    public static Collection<ServerPlayerEntity> tracking(Entity entity) {
        return PlayerLookup.tracking(entity);
    }

}
