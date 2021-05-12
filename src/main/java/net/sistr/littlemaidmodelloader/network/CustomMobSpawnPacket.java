package net.sistr.littlemaidmodelloader.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.sistr.littlemaidmodelloader.LittleMaidModelLoader;
import net.sistr.littlemaidmodelloader.network.util.CustomPacketEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class CustomMobSpawnPacket {
    public static final Identifier ID =
            new Identifier(LittleMaidModelLoader.MODID, "custom_mob_spawn");
    public static final Logger LOGGER = LogManager.getLogger();

    public static Packet<?> createPacket(LivingEntity entity) {
        if (!(entity instanceof CustomPacketEntity)) {
            throw new IllegalStateException("CustomPacketEntityを実装していません。");
        }
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(entity.getEntityId());
        buf.writeUuid(entity.getUuid());
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeByte((int) (entity.yaw * 256.0F / 360.0F));
        buf.writeByte((int) (entity.pitch * 256.0F / 360.0F));
        buf.writeByte((int) (entity.headYaw * 256.0F / 360.0F));
        Vec3d vec3d = entity.getVelocity();
        int velocityX = (int) (MathHelper.clamp(vec3d.x, -3.9D, 3.9D) * 8000D);
        int velocityY = (int) (MathHelper.clamp(vec3d.y, -3.9D, 3.9D) * 8000D);
        int velocityZ = (int) (MathHelper.clamp(vec3d.z, -3.9D, 3.9D) * 8000D);
        buf.writeShort(velocityX);
        buf.writeShort(velocityY);
        buf.writeShort(velocityZ);
        ((CustomPacketEntity) entity).writeCustomPacket(buf);
        return ServerPlayNetworking.createS2CPacket(ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2CPacket(MinecraftClient client, ClientPlayNetworkHandler handler,
                                        PacketByteBuf buf, PacketSender responseSender) {
        int id = buf.readVarInt();
        UUID uuid = buf.readUuid();
        int entityTypeId = buf.readVarInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float yaw = (buf.readByte() * 360F) / 256F;
        float pitch = (buf.readByte() * 360F) / 256F;
        float headYaw = (buf.readByte() * 360F) / 256F;
        float velocityX = buf.readShort() / 8000F;
        float velocityY = buf.readShort() / 8000F;
        float velocityZ = buf.readShort() / 8000F;
        //そのまんまbuf渡すと、spawnが実行されるまでの間に読み込めなくなるため、コピーする
        PacketByteBuf additional = new PacketByteBuf(buf.copy());
        client.execute(() -> spawn(id, uuid, entityTypeId, x, y, z, yaw, pitch, headYaw,
                velocityX, velocityY, velocityZ, additional));
    }

    @Environment(EnvType.CLIENT)
    private static void spawn(int id, UUID uuid, int entityTypeId, double x, double y, double z,
                              float yaw, float pitch, float headYaw,
                              float velocityX, float velocityY, float velocityZ, PacketByteBuf additional) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world == null) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity)
                EntityType.createInstanceFromId(entityTypeId, world);
        if (livingEntity instanceof CustomPacketEntity) {
            livingEntity.updateTrackedPosition(x, y, z);
            livingEntity.bodyYaw = headYaw;
            livingEntity.headYaw = headYaw;

            livingEntity.setEntityId(id);
            livingEntity.setUuid(uuid);
            livingEntity.updatePositionAndAngles(x, y, z, yaw, pitch);
            livingEntity.setVelocity(velocityX, velocityY, velocityZ);
            ((CustomPacketEntity) livingEntity).readCustomPacket(additional);
            world.addEntity(id, livingEntity);
        } else {
            LOGGER.warn("Skipping Entity with id {}", entityTypeId);
        }
        if (additional.refCnt() > 0) {
            additional.release();
        }
    }

}
