package net.sistr.littlemaidmodelloader.util.forge;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.ChunkManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class PlayerListImpl {

    public static Collection<ServerPlayerEntity> tracking(Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        ChunkManager manager = entity.world.getChunkManager();

        if (manager instanceof ServerChunkManager) {
            ThreadedAnvilChunkStorage storage = ((ServerChunkManager) manager).threadedAnvilChunkStorage;

            // return an immutable collection to guard against accidental removals.
            return Collections.unmodifiableCollection(((ThreadedAnvilChunkStorageTrackingExtensions) storage)
                    .fabric_getTrackingPlayers(entity));
        }

        throw new IllegalArgumentException("Only supported on server worlds!");
    }

}
