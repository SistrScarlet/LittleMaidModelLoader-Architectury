package net.sistr.littlemaidmodelloader.maidmodel.compat;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;
import net.sistr.littlemaidmodelloader.maidmodel.ModelBoxBase;
import net.sistr.littlemaidmodelloader.maidmodel.ModelRenderer;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

/**
 * GL互換クラス
 * 完全ではないが、メイドモデル読み込みに限っては概ね問題無い
 * 問題が出るモデルが出るたびに更新すること
 * */
public final class GLCompat {

    //行列関連
    public static int mode = GL11.GL_MODELVIEW;
    public static ModelRenderer modelRenderer;
    //todo Modelしか使わんので無駄が多い
    public static MatrixStack textureStack = new MatrixStack();

    //頂点描画関連
    private static int renderMode;
    private static ModelBoxBase.PositionTextureVertex vertexCurrent;
    private static ModelBoxBase.PositionTextureVertex vertexPrev1;
    private static ModelBoxBase.PositionTextureVertex vertexPrev2;
    private static Vec3f pos;
    private static Vec2f tex;

    public static void glPushMatrix() {
        if (mode == GL11.GL_MODELVIEW) {
            ModelRenderer.matrixStack.push();
        } else if (mode == GL11.GL_TEXTURE) {
            textureStack.push();
        }
    }

    public static void glPopMatrix() {
        if (mode == GL11.GL_MODELVIEW) {
            ModelRenderer.matrixStack.pop();
        } else if (mode == GL11.GL_TEXTURE) {
            textureStack.pop();
        }
    }

    public static void glTranslatef(float x, float y, float z) {
        if (mode == GL11.GL_MODELVIEW) {
            ModelRenderer.matrixStack.translate(x, y, z);
        } else if (mode == GL11.GL_TEXTURE) {
            textureStack.translate(x, y, z);
        }
    }

    public static void glScalef(float x, float y, float z) {
        if (mode == GL11.GL_MODELVIEW) {
            MatrixStack.Entry entry = ModelRenderer.matrixStack.peek();
            entry.getModel().multiply(Matrix4f.scale(x, y, z));
        } else if (mode == GL11.GL_TEXTURE) {
            MatrixStack.Entry entry = textureStack.peek();
            entry.getModel().multiply(Matrix4f.scale(x, y, z));
        }
    }

    public static void glRotatef(float deg, float x, float y, float z) {
        if (mode == GL11.GL_MODELVIEW) {
            if (x == 1F) {
                ModelRenderer.matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(deg));
            } else if (y == 1F) {
                ModelRenderer.matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(deg));
            } else if (z == 1F) {
                ModelRenderer.matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(deg));
            }

        } else if (mode == GL11.GL_TEXTURE) {
            if (x == 1F) {
                textureStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(deg));
            } else if (y == 1F) {
                textureStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(deg));
            } else if (z == 1F) {
                textureStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(deg));
            }
        }
    }

    public static void glColor3f(float red, float green, float blue) {
        ModelRenderer.red = red;
        ModelRenderer.green = green;
        ModelRenderer.blue = blue;
    }

    public static void glMatrixMode(int mode) {
        GLCompat.mode = mode;
    }

    //現在のマトリックスを書き込む
    public static void glGetFloat(int mode, FloatBuffer buf) {
        if (mode == GL11.GL_MODELVIEW_MATRIX) {
            ModelRenderer.matrixStack.peek().getModel().writeColumnMajor(buf);
        }
    }

    //バッファを読み込む
    public static void glLoadMatrix(FloatBuffer buf) {
        if (mode == GL11.GL_MODELVIEW) {
            ModelRenderer.matrixStack.peek().getModel().readColumnMajor(buf);
        } else if (mode == GL11.GL_TEXTURE) {
            textureStack.peek().getModel().readColumnMajor(buf);
        }
    }

    public static void glMultMatrix(FloatBuffer buf) {
        if (mode == GL11.GL_MODELVIEW) {
            //透過バグは直るが当然位置がおかしくなる
            //float num = buf.get(7);
            //buf.put(7, num * 0);
            Matrix4f matrix4f = new Matrix4f();
            matrix4f.readColumnMajor(buf);
            ModelRenderer.matrixStack.peek().getModel().multiply(matrix4f);
            //buf.put(7, num);
        } else if (mode == GL11.GL_TEXTURE) {
            Matrix4f matrix4f = new Matrix4f();
            matrix4f.readColumnMajor(buf);
            textureStack.peek().getModel().multiply(matrix4f);
        }
    }

    private static int pack(int x, int y) {
        return y * 4 + x;
    }

    public static void glCallList(int i) {
        for (ModelBoxBase boxBase : modelRenderer.cubeList) {
            boxBase.render(ModelRenderer.matrixStack, ModelRenderer.buffer, ModelRenderer.light, ModelRenderer.overlay, ModelRenderer.red, ModelRenderer.green, ModelRenderer.blue, ModelRenderer.alpha, modelRenderer.scale);
        }
    }

    public static void glLoadIdentity() {
        ModelRenderer.matrixStack.peek().getModel().loadIdentity();
    }

    public static void glBegin(int i) {
        if (i == GL11.GL_TRIANGLE_STRIP) {
            renderMode = i;
        }
    }

    public static void glEnd() {
        if (renderMode == GL11.GL_TRIANGLE_STRIP) {
            vertexCurrent = null;
            vertexPrev1 = null;
            vertexPrev2 = null;
            pos = null;
            tex = null;
        }
        renderMode = 0;
    }

    public static void glVertex3f(float x, float y, float z) {
        if (renderMode == GL11.GL_TRIANGLE_STRIP) {
            pos = new Vec3f(x, y, z);
            combine();
        }

    }

    public static void glNormal3f(float f, float f2, float f3) {

    }

    public static void glTexCoord2f(float u, float v) {
        if (renderMode == GL11.GL_TRIANGLE_STRIP) {
            tex = new Vec2f(u, v);
            combine();
        }
    }

    private static void combine() {
        if (tex != null && pos != null) {
            vertexPrev2 = vertexPrev1;
            vertexPrev1 = vertexCurrent;
            vertexCurrent = new ModelBoxBase.PositionTextureVertex(pos, tex.x, tex.y);
            pos = null;
            tex = null;
            if (vertexPrev2 != null) {
                ModelBoxBase.TexturedQuad quad = new ModelBoxBase.TexturedQuad(
                        new ModelBoxBase.PositionTextureVertex[]{vertexPrev2, vertexPrev1, vertexCurrent, vertexCurrent});
                quad.draw(ModelRenderer.matrixStack, ModelRenderer.buffer, ModelRenderer.light, ModelRenderer.overlay, ModelRenderer.red, ModelRenderer.green, ModelRenderer.blue, ModelRenderer.alpha, 1F);
            }
        }
    }

    public static void glPushAttrib(int i) {
        //WGL_NUMBER_OVERLAYS_ARB?
    }

    public static void glPopAttrib() {
    }

    public static void glCullFace(int i) {

    }

    public static void glEnable(int i) {
        //GL_CULL_FACE
        //GL_RESCALE_NORMAL = 0x803A
        //GL11.glEnable(i);
    }

    //ダミークラス
    //現状ほぼ変換されることはない
    public static void dummy() { }
    public static void dummy(int i) { }
    public static void dummy(float a, float b) { }
    public static void dummy(float a, float b, float c) { }
    public static void dummy(FloatBuffer f) { }
    public static void dummy(int i, FloatBuffer f) { }
}
