package net.sistr.littlemaidmodelloader.resource.classloader;

import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 古いマルチモデルのロード用。
 * 使用しているクラスを置換えて新しいものへ対応。
 */
public class MultiModelClassTransformer {
    private static final String PACKAGE_STRING = "net/sistr/littlemaidmodelloader/maidmodel/";

    private static final Map<String, String> CODE_REPLACE_MAP = new Object2ObjectOpenHashMap<>() {
        {
            //未使用っぽいのはコメントアウト

            //Caps系
            String caps = "firis/lmmm/api/caps/";
            addTransformTarget("IModelCaps");
            addTransformTarget("IModelCaps", caps);
            //今のところCapsが一つしかないため移動先もまたひとつ
            put("mmmlibx/lib/MMM_EntityCaps", "net/sistr/littlemaidmodelloader/maidmodel/EntityCaps");
            put("littleMaidMobX/EntityCaps", "net/sistr/littlemaidmodelloader/maidmodel/EntityCaps");
            put("net/blacklab/lmr/util/EntityCaps", "net/sistr/littlemaidmodelloader/maidmodel/EntityCaps");
            put("net/blacklab/lmr/util/EntityCapsLiving", "net/sistr/littlemaidmodelloader/maidmodel/EntityCaps");

            addTransformTarget("ModelCapsHelper");
            addTransformTarget("ModelCapsHelper", caps);

            //レンダラ
            String renderer = "firis/lmmm/api/renderer/";
            addTransformTarget("ModelRenderer");
            addTransformTarget("ModelRenderer", renderer);
            //addTransformTarget("EquippedStabilizer");

            //ボックス
            String modelParts = "firis/lmmm/api/model/parts/";
            addTransformTarget("ModelBoxBase");
            addTransformTarget("ModelBoxBase", modelParts);
            addTransformTarget("ModelBox");
            addTransformTarget("ModelBox", modelParts);
            addTransformTarget("ModelPlate");
            addTransformTarget("ModelPlate", modelParts);

            //モデル
            String builtinModel = "firis/lmmm/builtin/model/";
            addTransformTarget("ModelLittleMaid_Aug");
            addTransformTarget("ModelLittleMaid_Aug", builtinModel);
            addTransformTarget("ModelLittleMaid_SR2");
            addTransformTarget("ModelLittleMaid_SR2", builtinModel);
            addTransformTarget("ModelLittleMaid_Orign");
            addTransformTarget("ModelLittleMaid_Orign", builtinModel);
            //addTransformTarget("ModelLittleMaid_RX0");//元コードだとRX2になってたけどこれ正しいのか？
            addTransformTarget("ModelLittleMaid_Archetype");
            addTransformTarget("ModelLittleMaid_Archetype", builtinModel);

            String model = "firis/lmmm/api/model/";
            addTransformTarget("ModelLittleMaidBase");
            addTransformTarget("ModelLittleMaidBase", model);
            //addTransformTarget("ModelLittleMaid_AC");
            addTransformTarget("ModelMultiMMMBase");
            addTransformTarget("ModelMultiMMMBase", model);
            addTransformTarget("ModelMultiBase");
            addTransformTarget("ModelMultiBase", model);
            //addTransformTarget("ModelStabilizer_WitchHat");
            //addTransformTarget("ModelStabilizerBase");
            addTransformTarget("ModelBase");
            addTransformTarget("ModelBase", model);

            //Beverly7 同梱するようにしたので不要ではあるが、他のモデルで使用している可能性があるため残す
            put("net/blacklab/lmr/entity/EntityLittleMaid", "net/sistr/littlemaidmodelloader/entity/EntityLittleMaid");
            put("net/minecraft/entity/EntityLivingBase", "net/minecraft/entity/LivingEntity");
            put("net/minecraft/entity/passive/EntityAnimal", "net/minecraft/entity/passive/AnimalEntity");
            put("net/minecraft/entity/player/EntityPlayer", "net/minecraft/entity/player/PlayerEntity");
            //put("func_184187_bx", "method_5854");

            //NM
            put("net/blacklab/lmr/entity/littlemaid/EntityLittleMaid", "net/sistr/littlemaidmodelloader/entity/EntityLittleMaid");
            if (!Platform.isDevelopmentEnvironment()) {
                if (Platform.isFabric()) {
                    put("net/minecraft/entity/Entity", "net/minecraft/class_1297");
                } else {
                    put("net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity");
                }
            }
        }

        private void addTransformTarget(String className) {
            put("MMM_" + className, PACKAGE_STRING + className);
            put("mmmlibx/lib/multiModel/model/mc162/" + className, PACKAGE_STRING + className);
            put("net/blacklab/lmr/entity/maidmodel/" + className, PACKAGE_STRING + className);
        }

        private void addTransformTarget(String className, String oldPackage) {
            put(oldPackage + className, PACKAGE_STRING + className);
        }
    };

    private static final Set<String> GL_REPLACE_MODEL_RENDERER_SET = new ObjectOpenHashSet<>() {
        {
            add("glPushMatrix()V");
            add("glPopMatrix()V");
            add("glTranslatef(FFF)V");
            add("glScalef(FFF)V");
            add("glRotatef(FFFF)V");
            add("glColor3f(FFF)V");
            add("glMatrixMode(I)V");
            add("glGetFloat(ILjava/nio/FloatBuffer;)V");
            add("glLoadMatrix(Ljava/nio/FloatBuffer;)V");
            add("glMultMatrix(Ljava/nio/FloatBuffer;)V");
            add("glCallList(I)V");
            add("glEnable(I)V");
            add("glTexCoord2f(FF)V");
            add("glNormal3f(FFF)V");
            add("glVertex3f(FFF)V");
            add("glPushAttrib(I)V");
            add("glCullFace(I)V");
            add("glBegin(I)V");
            add("glEnd()V");
            add("glPopAttrib()V");
            add("glLoadIdentity()V");
            add("");
        }
    };

    private static final Set<String> GL_REPLACE_DUMMY_SET = new ObjectOpenHashSet<>() {
        {
            add("()V");
            add("(I)V");
            add("(FF)V");
            add("(FFF)V");
            add("(Ljava/nio/FloatBuffer;)V");
            add("(ILjava/nio/FloatBuffer;)V");
        }
    };

    /**
     * バイナリを解析して古いクラスを置き換える。
     */
    public byte[] transform(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode cNode = new ClassNode();
        reader.accept(cNode, 0);

        final AtomicBoolean changed = new AtomicBoolean(false);

        // 親クラスの置き換え
        tryReplace(changed, cNode.superName, superName -> cNode.superName = superName);

        //パラレルで処理すると1.3倍くらい早くなった

        // フィールドの置き換え
        cNode.fields.parallelStream().forEach(fNode ->
                tryReplace(changed, fNode.desc, desc -> fNode.desc = desc));

        // メソッドの置き換え
        cNode.methods.parallelStream().forEach(mNode -> {
            tryReplace(changed, mNode.desc, desc -> mNode.desc = desc);

            if (mNode.localVariables != null) {
                mNode.localVariables.parallelStream().forEach(lNode -> {
                    if (lNode.desc != null) {
                        tryReplace(changed, lNode.desc, desc -> lNode.desc = desc);
                    }
                    if (lNode.name != null) {
                        tryReplace(changed, lNode.name, name -> lNode.name = name);
                    }
                    if (lNode.signature != null) {
                        tryReplace(changed, lNode.signature, signature -> lNode.signature = signature);
                    }
                });
            }

            AbstractInsnNode aNode = mNode.instructions.getFirst();
            while (aNode != null) {
                if (aNode instanceof FieldInsnNode fANode) {//4
                    tryReplace(changed, fANode.desc, desc -> fANode.desc = desc);
                    tryReplace(changed, fANode.name, name -> fANode.name = name);
                    tryReplace(changed, fANode.owner, owner -> fANode.owner = owner);
                } else if (aNode instanceof InvokeDynamicInsnNode fANode) {//6
                    tryReplace(changed, fANode.desc, desc -> fANode.desc = desc);
                    tryReplace(changed, fANode.name, name -> fANode.name = name);
                } else if (aNode instanceof MethodInsnNode fANode) {//5
                    if (shouldRemove(fANode.owner)) {
                        changed.set(true);
                        aNode = aNode.getNext();
                        //置き換え対象があるならそちらへ
                        if (GL_REPLACE_MODEL_RENDERER_SET.contains(fANode.name + fANode.desc)) {
                            mNode.instructions.set(fANode, new MethodInsnNode(fANode.getOpcode(),
                                    "net/sistr/littlemaidmodelloader/maidmodel/compat/GLCompat",
                                    fANode.name,
                                    fANode.desc));
                        } else if (GL_REPLACE_DUMMY_SET.contains(fANode.desc)) {
                            //置き換え対象が無いならダミーに置き換え
                            mNode.instructions.set(fANode, new MethodInsnNode(fANode.getOpcode(),
                                    "net/sistr/littlemaidmodelloader/maidmodel/compat/GLCompat",
                                    "dummy",
                                    fANode.desc));
                        } else {
                            //型の合わないメソッドは引数無しメソッドに書き換えるが、多分失敗する
                            mNode.instructions.set(fANode, new MethodInsnNode(fANode.getOpcode(),
                                    "net/sistr/littlemaidmodelloader/maidmodel/compat/GLCompat",
                                    "dummy",
                                    "()V"));
                        }
                        continue;
                    }
                    tryReplace(changed, fANode.desc, desc -> fANode.desc = desc);
                    tryReplace(changed, fANode.name, name -> fANode.name = name);
                    tryReplace(changed, fANode.owner, owner -> fANode.owner = owner);
                } else if (aNode instanceof MultiANewArrayInsnNode fANode) {//13
                    tryReplace(changed, fANode.desc, desc -> fANode.desc = desc);
                } else if (aNode instanceof TypeInsnNode fANode) {//3
                    tryReplace(changed, fANode.desc, desc -> fANode.desc = desc);
                } else if (aNode instanceof LdcInsnNode fANode && ((LdcInsnNode) aNode).cst instanceof Type) {
                    tryReplace(changed, ((Type) fANode.cst).getInternalName(), desc -> fANode.cst = Type.getObjectType(desc));
                }
                aNode = aNode.getNext();
            }
        });

        // バイナリコードの書き出し
        if (changed.get()) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cNode.accept(writer);
            //必要性が薄い
            //if (LMMLConfig.isDebugMode()) LOGGER.debug("Replaced : " + name);
            return writer.toByteArray();
        }
        return basicClass;
    }

    /**
     * replaceMapに沿って置き換える
     */
    private void tryReplace(AtomicBoolean changed, String text, Consumer<String> replacer) {
        String newText = null;
        for (Entry<String, String> entry : CODE_REPLACE_MAP.entrySet()) {
            if ((text + ";").contains(entry.getKey() + ";")) {
                text = text.replace(entry.getKey(), entry.getValue());
                newText = text;
                //量がエグいので基本表示しないように
                //if (LMMLConfig.isDebugMode()) LOGGER.debug("Replacing : " + text + " to " + newText);

                //稀に1行に2つ変換対象がある場合があるため、ここでリターンはしない
                //return newText;
            }
        }
        if (newText != null) {
            changed.set(true);
            replacer.accept(newText);
        }
    }

    private boolean shouldRemove(String text) {
        return text.endsWith("GL11");
    }

}
