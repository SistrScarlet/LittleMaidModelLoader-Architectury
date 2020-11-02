package net.sistr.lmml.resource.classloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 古いマルチモデルのロード用。
 * 使用しているクラスを置換えて新しいものへ対応。
 */
public class MultiModelClassTransformer {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String newPackageString = "net/sistr/lmml/maidmodel/";

    private static final Map<String, String> replaceMap = new HashMap<String, String>() {
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
            addTransformTarget("ModelBoxBase");//名前の長さ的にここだけ順番逆にした
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
            put("mmmlibx/lib/MMM_EntityCaps", "net/sistr/lmml/maidmodel/EntityCaps");
            put("net/blacklab/lmr/util/EntityCapsLiving", "net/sistr/lmml/maidmodel/EntityCaps");
            put("littleMaidMobX/EntityCaps", "net/sistr/lmml/maidmodel/EntityCaps");
            put("net/blacklab/lmr/util/EntityCaps", "net/sistr/lmml/maidmodel/EntityCaps");

            //Beverly7
            put("net/blacklab/lmr/entity/EntityLittleMaid", "net/sistr/lmml/entity/EntityLittleMaid");
            put("net/minecraft/entity/EntityLivingBase", "net/minecraft/entity/LivingEntity");
            put("net/minecraft/entity/passive/EntityAnimal", "net/minecraft/entity/passive/AnimalEntity");
            put("net/minecraft/entity/player/EntityPlayer", "net/minecraft/entity/player/PlayerEntity");
            put("func_184187_bx", "getRidingEntity");

            //NM
            put("net/blacklab/lmr/entity/littlemaid/EntityLittleMaid", "net/sistr/lmml/entity/EntityLittleMaid");
        }

        private void addTransformTarget(String className) {
            put("MMM_" + className, newPackageString + className);
            put("mmmlibx/lib/multiModel/model/mc162/" + className, newPackageString + className);
            put("net/blacklab/lmr/entity/maidmodel/" + className, newPackageString + className);
        }
    };

    /**
     * バイナリを解析して古いクラスを置き換える。
     */
    public byte[] transform(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);

        AtomicBoolean changed = new AtomicBoolean(false);

        // 親クラスの置き換え
        ClassNode cNode = new ClassNode();
        reader.accept(cNode, 0);
        replace(cNode.superName).ifPresent(superName -> {
            changed.set(true);
            cNode.superName = superName;
        });

        // フィールドの置き換え
        for (FieldNode fNode : cNode.fields) {
            replace(fNode.desc).ifPresent(desc -> {
                changed.set(true);
                fNode.desc = desc;
            });
        }

        // メソッドの置き換え
        for (MethodNode mNode : cNode.methods) {
            replace(mNode.desc).ifPresent(desc -> {
                changed.set(true);
                mNode.desc = desc;
            });

            if (mNode.localVariables != null) {
                for (LocalVariableNode vNode : mNode.localVariables) {
                    if (vNode.desc != null) {
                        replace(vNode.desc).ifPresent(desc -> {
                            changed.set(true);
                            vNode.desc = desc;
                        });
                    }
                    if (vNode.name != null) {
                        replace(vNode.name).ifPresent(name -> {
                            changed.set(true);
                            vNode.name = name;
                        });
                    }
                    if (vNode.signature != null) {
                       replace(vNode.signature).ifPresent(signature -> {
                           changed.set(true);
                           vNode.signature = signature;
                       });
                    }
                }
            }

            AbstractInsnNode aNode = mNode.instructions.getFirst();
            while (aNode != null) {
                if (aNode instanceof FieldInsnNode) {//4
                    FieldInsnNode fANode = (FieldInsnNode) aNode;
                    replace(fANode.desc).ifPresent(desc -> {
                        changed.set(true);
                        fANode.desc = desc;
                    });
                    replace(fANode.name).ifPresent(name -> {
                        changed.set(true);
                        fANode.name = name;
                    });
                    replace(fANode.owner).ifPresent(owner -> {
                        changed.set(true);
                        fANode.owner = owner;
                    });
                } else if (aNode instanceof InvokeDynamicInsnNode) {//6
                    InvokeDynamicInsnNode fANode = (InvokeDynamicInsnNode) aNode;
                    replace(fANode.desc).ifPresent(desc -> {
                        changed.set(true);
                        fANode.desc = desc;
                    });
                    replace(fANode.name).ifPresent(name -> {
                        changed.set(true);
                        fANode.name = name;
                    });
                } else if (aNode instanceof MethodInsnNode) {//5
                    MethodInsnNode fANode = (MethodInsnNode) aNode;
                    replace(fANode.desc).ifPresent(desc -> {
                        changed.set(true);
                        fANode.desc = desc;
                    });
                    replace(fANode.name).ifPresent(name -> {
                        changed.set(true);
                        fANode.name = name;
                    });
                    replace(fANode.owner).ifPresent(owner -> {
                        changed.set(true);
                        fANode.owner = owner;
                    });
                } else if (aNode instanceof MultiANewArrayInsnNode) {//13
                    MultiANewArrayInsnNode fANode = (MultiANewArrayInsnNode) aNode;
                    replace(fANode.desc).ifPresent(desc -> {
                        changed.set(true);
                        fANode.desc = desc;
                    });
                } else if (aNode instanceof TypeInsnNode) {//3
                    TypeInsnNode fANode = (TypeInsnNode) aNode;
                    replace(fANode.desc).ifPresent(desc -> {
                        changed.set(true);
                        fANode.desc = desc;
                    });
                }
                aNode = aNode.getNext();
            }
        }

        // バイナリコードの書き出し
        if (changed.get()) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cNode.accept(writer);
            byte[] newClass = writer.toByteArray();
            //必要性が薄い
            //if (LMMLConfig.isDebugMode()) LOGGER.debug("Replaced : " + name);
            return newClass;
        }
        return basicClass;
    }

    /**
     * replaceMapに沿って置き換える
     */
    private Optional<String> replace(String text) {
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
        return Optional.ofNullable(newText);
    }

}
