package net.sistr.littlemaidmodelloader.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerList {

  @ExpectPlatform
  public static Collection<ServerPlayerEntity> tracking(Entity entity) {
    throw new AssertionError();
  }
}
