package net.sistr.littlemaidmodelloader.resource.classloader;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
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
    //private static final Logger LOGGER = LogManager.getLogger();
    private static final String newPackageString = "net/sistr/littlemaidmodelloader/maidmodel/";

    private static final Map<String, String> replaceMap = new Object2ObjectOpenHashMap<>() {
        {
            //継承関係で整理
            //また、未使用っぽいのはコメントアウト

            //継承多数
            addTransformTarget("IModelCaps");

            //継承無し
            addTransformTarget("ModelRenderer");
            addTransformTarget("ModelCapsHelper");
            //addTransformTarget("EquippedStabilizer");

            //ボックス
            addTransformTarget("ModelBoxBase");
            addTransformTarget("ModelBox");
            addTransformTarget("ModelPlate");

            //モデル
            addTransformTarget("ModelLittleMaid_Aug");
            addTransformTarget("ModelLittleMaid_SR2");
            addTransformTarget("ModelLittleMaid_Orign");
            //addTransformTarget("ModelLittleMaid_RX0");//元コードだとRX2になってたけどこれ正しいのか？
            addTransformTarget("ModelLittleMaid_Archetype");
            addTransformTarget("ModelLittleMaidBase");
            //addTransformTarget("ModelLittleMaid_AC");
            addTransformTarget("ModelMultiMMMBase");
            addTransformTarget("ModelMultiBase");
            //addTransformTarget("ModelStabilizer_WitchHat");
            //addTransformTarget("ModelStabilizerBase");
            addTransformTarget("ModelBase");

            //Caps系
            //今のところCapsが一つしかないため移動先もまたひとつ
            put("mmmlibx/lib/MMM_EntityCaps", "net/sistr/littlemaidmodelloader/maidmodel/EntityCaps");
            put("net/blacklab/lmr/util/EntityCapsLiving", "net/sistr/littlemaidmodelloader/maidmodel/EntityCaps");
            put("littleMaidMobX/EntityCaps", "net/sistr/littlemaidmodelloader/maidmodel/EntityCaps");
            put("net/blacklab/lmr/util/EntityCaps", "net/sistr/littlemaidmodelloader/maidmodel/EntityCaps");

            //Beverly7
            put("net/blacklab/lmr/entity/EntityLittleMaid", "net/sistr/littlemaidmodelloader/entity/EntityLittleMaid");
            put("net/minecraft/entity/EntityLivingBase", "net/minecraft/entity/LivingEntity");
            put("net/minecraft/entity/passive/EntityAnimal", "net/minecraft/entity/passive/AnimalEntity");
            put("net/minecraft/entity/player/EntityPlayer", "net/minecraft/entity/player/PlayerEntity");
            //put("func_184187_bx", "method_5854");

            //NM
            put("net/blacklab/lmr/entity/littlemaid/EntityLittleMaid", "net/sistr/littlemaidmodelloader/entity/EntityLittleMaid");
        }

        private void addTransformTarget(String className) {
            put("MMM_" + className, newPackageString + className);
            put("mmmlibx/lib/multiModel/model/mc162/" + className, newPackageString + className);
            put("net/blacklab/lmr/entity/maidmodel/" + className, newPackageString + className);
        }
    };

    private static final Set<String> glReplaceSet = new ObjectOpenHashSet<>() {
        {
            add("()V");
            add("(I)V");
            add("(FF)V");
            add("(FFF)V");
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
                        //可能であれば型を合わせて置き換える
                        if (glReplaceSet.contains(fANode.desc)) {
                            mNode.instructions.set(fANode, new MethodInsnNode(fANode.getOpcode(),
                                    "net/sistr/littlemaidmodelloader/resource/classloader/GLDummy",
                                    "dummy",
                                    fANode.desc));
                        } else {
                            mNode.instructions.set(fANode, new MethodInsnNode(fANode.getOpcode(),
                                    "net/sistr/littlemaidmodelloader/resource/classloader/GLDummy",
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
        for (Entry<String, String> entry : replaceMap.entrySet()) {
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
