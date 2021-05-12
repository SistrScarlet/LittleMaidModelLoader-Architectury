package net.sistr.littlemaidmodelloader.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.sistr.littlemaidmodelloader.util.ThreadedAnvilChunkStorageTrackingExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Collections;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin implements ThreadedAnvilChunkStorageTrackingExtensions {
    @Shadow
    @Final
    // We can abuse type erasure here and just get the type in the map as the accessor.
    // This allows us to avoid an access widener for the package-private `EntityTracker` subclass.
    private Int2ObjectMap<EntityTrackerAccessor> entityTrackers;

    @Override
    public Collection<ServerPlayerEntity> fabric_getTrackingPlayers(Entity entity) {
        EntityTrackerAccessor accessor = this.entityTrackers.get(entity.getEntityId());

        if (accessor != null) {
            return accessor.getPlayersTracking();
        }

        return Collections.emptySet();
    }
}
