package net.sistr.littlemaidmodelloader.resource.util;

import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.Optional;

public class ResourceHelper {

    /**
     * 旧タイプのファイル名
     */
    protected static String[] defNames = {
            "mob_littlemaid0.png", "mob_littlemaid1.png",
            "mob_littlemaid2.png", "mob_littlemaid3.png",
            "mob_littlemaid4.png", "mob_littlemaid5.png",
            "mob_littlemaid6.png", "mob_littlemaid7.png",
            "mob_littlemaid8.png", "mob_littlemaid9.png",
            "mob_littlemaida.png", "mob_littlemaidb.png",
            "mob_littlemaidc.png", "mob_littlemaidd.png",
            "mob_littlemaide.png", "mob_littlemaidf.png",
            "mob_littlemaidw.png",
            "mob_littlemaid_a00.png", "mob_littlemaid_a01.png"
    };
    private static final int OLD_WILD = 0x10; //16;
    private static final int OLD_ARMOR_1 = 0x11; //17;
    private static final int OLD_ARMOR_2 = 0x12; //18;

    /**
     * assets/minecraft/textures/entity/littlemaid/[texture]_[model]/xxxx_[index].png
     * のうち、xxxx_[index].pngを抜き取る
     */
    public static String getFileName(String path, boolean isArchive) {
        String name = path;
        if (!isArchive) {
            name = name.replace("\\", "/");
        }
        int lastSplitter = name.lastIndexOf("/");
        if (lastSplitter == -1) {
            return name;
        }
        //xxxx_[index].pngの前を削る
        return name.substring(lastSplitter + 1);
    }

    /**
     * assets/minecraft/textures/entity/littlemaid/xxxx/yyyy/[texture]_[model]/zzzz_[index].png
     * のうち、xxxx/yyyy/[texture]_[model]を抜き取り、また/を.に変換
     */
    public static Optional<String> getTexturePackName(String path, boolean isArchive) {
        String name = path;
        if (!isArchive) {
            name = name.replace("\\", "/");
        }
        if (path.contains("/littlemaid/") || path.contains("littleMaid")) {
            ///littlemaid/まで削る
            int lmFolderIndex = path.lastIndexOf("/littlemaid/");
            if (lmFolderIndex == -1) {
                lmFolderIndex = path.lastIndexOf("/littleMaid/");
            }
            if (lmFolderIndex == -1) {
                return getParentFolderName(path, isArchive);
            }
            name = name.substring(lmFolderIndex + "/littlemaid/".length());

            //[texture]_[model]の後を削る
            int lastSplitter = name.lastIndexOf("/");
            if (lastSplitter == -1) {
                return getParentFolderName(path, isArchive);
            }
            return Optional.of(name.substring(0, lastSplitter).replace("/", "."));
        }
        //littlemaidフォルダが含まれない場合は、親フォルダをパック名とする
        return getParentFolderName(path, isArchive);
    }

    /**
     * xxxx/yyyy/[texture]_[model]
     * のうち、[model]を抜き取る
     */
    public static String getModelName(String textureName) {
        int lastSplitter = textureName.lastIndexOf("_");
        if (lastSplitter == -1) {
            return "default";
        }
        //[texture]_[model]の前を削る
        textureName = textureName.substring(lastSplitter + 1);
        return textureName;
    }

    /**
     * assets/minecraft/textures/entity/littlemaid/[texture]_[model]/xxxx_[index].png
     * のうち、[texture]_[model]を抜き取る
     */
    public static Optional<String> getParentFolderName(String path, boolean isArchive) {
        String name = path;
        if (!isArchive) {
            name = name.replace("\\", "/");
        }
        int lastSplitter = name.lastIndexOf("/");
        if (lastSplitter == -1) {
            return Optional.empty();
        }
        //[texture]_[model]の後ろを削る
        name = name.substring(0, lastSplitter);
        lastSplitter = name.lastIndexOf("/");
        if (lastSplitter != -1) {
            //[texture]_[model]の前を削る
            name = name.substring(lastSplitter + 1);
        }
        return Optional.of(name);
    }

    /**
     * assets/minecraft/textures/entity/littlemaid/[texture]_[model]/xxxx_[index].png
     * のうち、[index]を抜き取って16進数変換
     * [index]が存在しない場合、-1を返す
     */
    public static int getIndex(String path) {
        int index = -1;
        // 旧タイプのファイル名からindexを取得する
        for (int i = 0; i < defNames.length; i++) {
            if (path.endsWith(defNames[i])) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            String name = path.toLowerCase();
            int lastDot = name.lastIndexOf(".");
            if (lastDot == -1) {
                return -1;
            }
            name = name.substring(0, lastDot);
            int lastSplitter = name.lastIndexOf("_");
            if (lastSplitter == -1) {
                return -1;
            }
            name = name.substring(lastSplitter + 1);

            try {
                index = Integer.decode("0x" + name);
            } catch (Exception e) {
                return -1;
            }
        }
        // colorIndexの変換
        if (index == OLD_ARMOR_1) {
            index = TextureIndexes.ARMOR_1_DAMAGED.getIndexMin();
        }
        if (index == OLD_ARMOR_2) {
            index = TextureIndexes.ARMOR_2_DAMAGED.getIndexMin();
        }
        if (index == OLD_WILD) {
            index = TextureIndexes.COLOR_WILD.getIndexMin() + TextureColors.BROWN.getIndex();
        }
        return index;
    }

    /**
     * ファイル名最後尾の数字を削除する
     */
    public static String removeNameLastIndex(String fileName) {
        String removed = fileName;
        while (true) {
            if (removed.isEmpty()) {
                break;
            }
            int subLength = removed.length() - 1;
            String temp = removed.substring(subLength);
            try {
                Integer.valueOf(temp);
            } catch (NumberFormatException e) {
                break;
            }
            removed = removed.substring(0, subLength);
        }
        return removed;
    }

    /**
     * 拡張子の削除
     */
    public static String removeExtension(String fileName) {
        int lastSplitter = fileName.lastIndexOf(".");
        if (lastSplitter == -1) {
            return fileName;
        }
        return fileName.substring(0, lastSplitter);
    }

    /**
     * 一番外側のフォルダーまたはzipファイル名を取る
     */
    public static Optional<String> getFirstParentName(String path, Path homePath, boolean isArchive) {
        if (isArchive) {
            //zipファイル名を取る
            String zipName = homePath.getFileName().toString();
            return Optional.of(zipName.substring(0, zipName.lastIndexOf(".")));
        } else {
            //一番外側のフォルダの名前を取る
            path = path.substring(1);
            int firstSplitter = path.indexOf("\\");
            if (firstSplitter == -1) return Optional.empty();
            return Optional.of(path.substring(0, firstSplitter));
        }
    }

    /**
     * ResourceWrapperに登録するファイル名を取得する。
     */
    public static Identifier getLocation(String packName, String fileName) {
        packName = packName.toLowerCase().replaceAll("[^a-z0-9/._\\-]", "-");
        fileName = fileName.toLowerCase().replaceAll("[^a-z0-9/._\\-]", "-");
        return new Identifier("littlemaidmodelloader", packName + "/" + fileName);
    }

    /**
     * ResourceWrapperに登録するファイル名を取得する。
     * prefixはResourceWrapperでの読み込み時に使う
     */
    public static Identifier getLocation(String prefix, String packName, String fileName) {
        packName = packName.toLowerCase().replaceAll("[^a-z0-9/._\\-]", "-");
        fileName = fileName.toLowerCase().replaceAll("[^a-z0-9/._\\-]", "-");
        return new Identifier("littlemaidmodelloader", prefix + "/" + packName + "/" + fileName);
    }

}
