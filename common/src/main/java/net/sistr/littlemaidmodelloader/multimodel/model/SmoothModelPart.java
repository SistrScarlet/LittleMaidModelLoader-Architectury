package net.sistr.littlemaidmodelloader.multimodel.model;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.sistr.littlemaidmodelloader.client.util.CuboidAccessor;
import net.sistr.littlemaidmodelloader.client.util.ModelPartAccessor;

public class SmoothModelPart extends net.minecraft.client.model.ModelPart {
    private final Direction direction = Direction.DOWN;
    private net.minecraft.client.model.ModelPart smoothChild;

    public SmoothModelPart(int textureWidth, int textureHeight, int textureOffsetU, int textureOffsetV) {
        super(textureWidth, textureHeight, textureOffsetU, textureOffsetV);
    }

    @Override
    public void addChild(net.minecraft.client.model.ModelPart part) {
        super.addChild(part);
        if (smoothChild == null) smoothChild = part;
        if (part instanceof SmoothModelPart2 && ((SmoothModelPart2) part).smoothParent == null)
            ((SmoothModelPart2) part).smoothParent = this;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        boolean shouldSmooth = smoothChild != null && (smoothChild.pitch != 0 || smoothChild.yaw != 0 || smoothChild.roll != 0);
        //スムーズに描画する必要が無いならスーパークラスのを使う
        if (!shouldSmooth) {
            super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            return;
        }
        if (!this.visible) {
            return;
        }
        ObjectList<Cuboid> cuboids = ((ModelPartAccessor) this).getCuboids();
        ObjectList<net.minecraft.client.model.ModelPart> children = ((ModelPartAccessor) this).getChildren();
        if (cuboids.isEmpty() && children.isEmpty()) {
            return;
        }
        matrices.push();

        this.rotate(matrices);
        MatrixStack.Entry defaultEntry = matrices.peek();
        matrices.push();
        followChildAngle(matrices, smoothChild);
        MatrixStack.Entry childEntry = matrices.peek();
        matrices.pop();
        this.renderSmoothCuboids(defaultEntry, childEntry, vertices, light, overlay, red, green, blue, alpha);

        for (net.minecraft.client.model.ModelPart modelPart : children) {
            modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }

        matrices.pop();

    }

    public void followChildAngle(MatrixStack matrix, net.minecraft.client.model.ModelPart child) {
        float x = child.pivotX / 16.0F;
        float y = child.pivotY / 16.0F;
        float z = child.pivotZ / 16.0F;
        matrix.translate(x, y, z);
        if (child.roll != 0.0F) matrix.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(child.roll));
        if (child.yaw != 0.0F) matrix.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(child.yaw));
        if (child.pitch != 0.0F) matrix.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(child.pitch));
        matrix.translate(-x, -y, -z);
    }

    private void renderSmoothCuboids(MatrixStack.Entry defaultEntry, MatrixStack.Entry childEntry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        Matrix4f defaultPosMat = defaultEntry.getModel();
        Matrix3f defaultNormal = defaultEntry.getNormal();

        Matrix4f childPosMat = childEntry.getModel();
        Matrix3f childNormal = childEntry.getNormal();

        ObjectList<Cuboid> cuboids = ((ModelPartAccessor) this).getCuboids();
        for (Cuboid cuboid : cuboids) {
            Quad[] quads = ((CuboidAccessor) cuboid).getQuads();
            //ここで値取ってこれと一致する頂点をーの方がいいかもね
            int indexQ = 0;
            for (Quad quad : quads) {
                Direction quadDirection = getQuadDirection(indexQ++);

                Vector3f defaultNormalVec = quad.direction.copy();
                defaultNormalVec.transform(defaultNormal);

                Vector3f smoothNormalVec = quad.direction.copy();
                smoothNormalVec.transform(childNormal);

                int indexV = 0;
                for (Vertex vertex : quad.vertices) {
                    Vector4f posVec = new Vector4f(
                            vertex.pos.getX() / 16.0F,
                            vertex.pos.getY() / 16.0F,
                            vertex.pos.getZ() / 16.0F, 1.0F);
                    if (shouldRotate(indexV++, quadDirection, direction)) {
                        posVec.transform(childPosMat);
                        vertexConsumer.vertex(posVec.getX(), posVec.getY(), posVec.getZ(),
                                red, green, blue, alpha, vertex.u, vertex.v, overlay, light,
                                smoothNormalVec.getX(), smoothNormalVec.getY(), smoothNormalVec.getZ());
                    } else {
                        posVec.transform(defaultPosMat);
                        vertexConsumer.vertex(posVec.getX(), posVec.getY(), posVec.getZ(),
                                red, green, blue, alpha, vertex.u, vertex.v, overlay, light,
                                defaultNormalVec.getX(), defaultNormalVec.getY(), defaultNormalVec.getZ());
                    }
                }
            }
        }
    }

    //Cuboidの実装に依存
    //quadの面の向きを返す
    //Normalの向きは逆
    private Direction getQuadDirection(int indexQ) {
        switch (indexQ) {
            case 0:
                return Direction.WEST;
            case 1:
                return Direction.EAST;
            case 2:
                return Direction.UP;
            case 3:
                return Direction.DOWN;
            case 4:
                return Direction.SOUTH;
            case 5:
                return Direction.NORTH;
        }
        return Direction.WEST;
    }

    //[indexQ] = {indexV...} Normalの方向 / 面の向き
    /*
     * [0] = {6, 2, 3, 7} EAST  / WEST
     * [1] = {0, 5, 8, 4} WEST  / EAST
     * [2] = {6, 5, 0, 2} DOWN  / UP
     * [3] = {3, 4, 8, 7} UP    / DOWN
     * [4] = {2, 0, 4, 3} NORTH / SOUTH
     * [5] = {5, 6, 7, 8} SOUTH / NORTH
     * */

    //Cuboidの実装に依存
    private boolean shouldRotate(int indexV, Direction quadDirection, Direction direction) {
        if (quadDirection.getAxis() == direction.getAxis()) return false;
        switch (direction) {
            case WEST:
                if (quadDirection == Direction.NORTH) {
                    if (indexV == 1 || indexV == 2) return true;
                } else if (indexV == 0 || indexV == 3) return true;
                break;
            case EAST:
                if (quadDirection == Direction.NORTH) {
                    if (indexV == 0 || indexV == 3) return true;
                } else if (indexV == 1 || indexV == 2) return true;
                break;
            case UP:
                if (indexV == 0 || indexV == 1) return true;
                break;
            case DOWN:
                if (indexV == 2 || indexV == 3) return true;
                break;
            case SOUTH:
                switch (quadDirection) {
                    case WEST:
                        if (indexV == 1 || indexV == 2) return true;
                        break;
                    case EAST:
                        if (indexV == 0 || indexV == 3) return true;
                        break;
                    case UP:
                        if (indexV == 2 || indexV == 3) return true;
                        break;
                    case DOWN:
                        if (indexV == 0 || indexV == 1) return true;
                        break;
                }
                break;
            case NORTH:
                switch (quadDirection) {
                    case WEST:
                        if (indexV == 0 || indexV == 3) return true;
                        break;
                    case EAST:
                        if (indexV == 1 || indexV == 2) return true;
                        break;
                    case UP:
                        if (indexV == 0 || indexV == 1) return true;
                        break;
                    case DOWN:
                        if (indexV == 2 || indexV == 3) return true;
                        break;
                }
                break;
        }
        return false;
    }

}
