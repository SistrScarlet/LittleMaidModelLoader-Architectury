package net.sistr.littlemaidmodelloader.util.forge;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.stream.Collectors;

public class PlayerListImpl {

    public static Collection<ServerPlayerEntity> tracking(Entity entity) {
        return entity.world.getPlayers().stream().map(p -> (ServerPlayerEntity) p).collect(Collectors.toList());
    }

}
