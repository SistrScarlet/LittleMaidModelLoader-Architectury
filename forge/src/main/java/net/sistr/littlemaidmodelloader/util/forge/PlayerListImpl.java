package net.sistr.littlemaidmodelloader.util.forge;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.ChunkManager;
import net.sistr.littlemaidmodelloader.forge.mixin.EntityTrackerAccessor;
import net.sistr.littlemaidmodelloader.forge.mixin.ThreadedAnvilChunkStorageAccessor;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerListImpl {

    public static Collection<ServerPlayerEntity> tracking(Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        ChunkManager manager = entity.world.getChunkManager();

        if (manager instanceof ServerChunkManager) {
            ThreadedAnvilChunkStorage storage = ((ServerChunkManager) manager).threadedAnvilChunkStorage;
            EntityTrackerAccessor tracker = ((ThreadedAnvilChunkStorageAccessor) storage).getEntityTrackers().get(entity.getId());

            // return an immutable collection to guard against accidental removals.
            if (tracker != null) {
                return Collections.unmodifiableCollection(tracker.getPlayersTracking()
                        .stream().map(EntityTrackingListener::getPlayer).collect(Collectors.toSet()));
            }

            return Collections.emptySet();
        }

        throw new IllegalArgumentException("Only supported on server worlds!");
    }

}
