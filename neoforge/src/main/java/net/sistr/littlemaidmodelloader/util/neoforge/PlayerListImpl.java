package net.sistr.littlemaidmodelloader.util.neoforge;

import java.util.Collection;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListImpl {

    public static Collection<ServerPlayerEntity> tracking(Entity entity) {
        return entity.getWorld().getPlayers().stream()
                .map(p -> (ServerPlayerEntity) p)
                .collect(Collectors.toList());
    }
}
