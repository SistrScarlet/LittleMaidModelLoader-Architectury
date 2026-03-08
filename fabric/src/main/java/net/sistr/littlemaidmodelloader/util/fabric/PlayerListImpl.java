package net.sistr.littlemaidmodelloader.util.fabric;

import java.util.Collection;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListImpl {

  public static Collection<ServerPlayerEntity> tracking(Entity entity) {
    return PlayerLookup.tracking(entity);
  }
}
