package net.sistr.littlemaidmodelloader.maidmodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.sistr.littlemaidmodelloader.maidmodel.compat.GLCompat;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class ModelBoxBase {
    protected PositionTextureVertex[] vertexPositions;
    protected TexturedQuad[] quadList;
    public float posX1;
    public float posY1;
    public float posZ1;
    public float posX2;
    public float posY2;
    public float posZ2;
    public String boxName;


    /**
     * こちらを必ず実装すること。
     *
     * @param pMRenderer
     * @param pArg
     */
    public ModelBoxBase(ModelRenderer pMRenderer, Object... pArg) {
    }

    public final void render(MatrixStack matrices, VertexConsumer buffer,
                             int light, int overlay, float red, float green, float blue, float alpha,
                             float scale) {
        for (TexturedQuad texturedQuad : quadList) {
            texturedQuad.draw(matrices, buffer, light, overlay, red, green, blue, alpha, scale);
        }
    }

    public ModelBoxBase setBoxName(String pName) {
        boxName = pName;
        return this;
    }

    @Environment(EnvType.CLIENT)
    public static class PositionTextureVertex {
        public Vector3f vector3D;
        public float texturePositionX;
        public float texturePositionY;

        public PositionTextureVertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }

        public PositionTextureVertex setTexturePosition(float u, float v) {
            return new PositionTextureVertex(this, u, v);
        }

        public PositionTextureVertex(PositionTextureVertex textureVertex, float texturePositionXIn, float texturePositionYIn) {
            this.vector3D = textureVertex.vector3D;
            this.texturePositionX = texturePositionXIn;
            this.texturePositionY = texturePositionYIn;
        }

        public PositionTextureVertex(Vector3f vec, float u, float v) {
            this.vector3D = vec;
            this.texturePositionX = u;
            this.texturePositionY = v;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class TexturedQuad {
        public PositionTextureVertex[] vertexPositions;
        public int nVertices;
        //private boolean invertNormal;
        private Vector3f normalCache;

        public TexturedQuad(PositionTextureVertex[] vertices) {
            this.vertexPositions = vertices;
            this.nVertices = vertices.length;

            this.normalCache = calcNormal();
        }

        public TexturedQuad(PositionTextureVertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight) {
            this(vertices);
            float f = 0.0F / textureWidth;
            float f1 = 0.0F / textureHeight;
            vertices[0] = vertices[0].setTexturePosition((float) texcoordU2 / textureWidth - f, (float) texcoordV1 / textureHeight + f1);
            vertices[1] = vertices[1].setTexturePosition((float) texcoordU1 / textureWidth + f, (float) texcoordV1 / textureHeight + f1);
            vertices[2] = vertices[2].setTexturePosition((float) texcoordU1 / textureWidth + f, (float) texcoordV2 / textureHeight - f1);
            vertices[3] = vertices[3].setTexturePosition((float) texcoordU2 / textureWidth - f, (float) texcoordV2 / textureHeight - f1);
        }

        public void flipFace() {
            PositionTextureVertex[] vertices = new PositionTextureVertex[this.vertexPositions.length];

            for (int i = 0; i < this.vertexPositions.length; ++i) {
                vertices[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
            }

            this.vertexPositions = vertices;

            this.normalCache = calcNormal();
        }

        public final void draw(MatrixStack matrices, VertexConsumer buffer,
                               int light, int overlay, float red, float green, float blue, float alpha, float scale) {
            MatrixStack.Entry entry = matrices.peek();
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            Vector3f normal = matrix3f.transform(new Vector3f(normalCache));

            float normalX = normal.x();
            float normalY = normal.y();
            float normalZ = normal.z();

            /*if (this.invertNormal) {
                normalX = -normalX;
                normalY = -normalY;
                normalZ = -normalZ;
            }*/

            for (int i = 0; i < 4; ++i) {
                ModelBoxBase.PositionTextureVertex vertex = this.vertexPositions[i];
                float x = vertex.vector3D.x() * scale;
                float y = vertex.vector3D.y() * scale;
                float z = vertex.vector3D.z() * scale;
                Vector4f pos = matrix4f.transform(new Vector4f(x, y, z, 1.0F));
                if (pos.w() != 1.0F) {
                    pos.normalize();
                }

                Vector4f uv = new Vector4f(vertex.texturePositionX, vertex.texturePositionY, 0, 1.0F);
                if (!GLCompat.textureStack.isEmpty()) {
                    GLCompat.textureStack.peek().getPositionMatrix().transform(uv);
                }

                buffer.vertex(pos.x(), pos.y(), pos.z(),
                        red, green, blue, alpha,
                        uv.x(), uv.y(),
                        overlay, light, normalX, normalY, normalZ);
            }

        }

        private Vector3f calcNormal() {
            Vector3f n1 = new Vector3f(this.vertexPositions[0].vector3D);
            Vector3f n2 = new Vector3f(this.vertexPositions[2].vector3D);
            n1.sub(this.vertexPositions[1].vector3D);
            n2.sub(this.vertexPositions[1].vector3D);
            n2.cross(n1);
            n2.normalize();
            return n2;
        }

    }

}
