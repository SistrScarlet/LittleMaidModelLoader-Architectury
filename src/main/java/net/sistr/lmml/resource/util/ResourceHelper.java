package net.sistr.lmml.resource.util;

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
    public static String getFileName(String path) {
        int lastSplitter = path.lastIndexOf("/");
        if (lastSplitter == -1) {
            return path;
        }
        //xxxx_[index].pngの前を削る
        return path.substring(lastSplitter + 1);
    }

    /**
     * assets/minecraft/textures/entity/littlemaid/[texture]_[model]/xxxx_[index].png
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
    public static Optional<String> getParentFolderName(String path) {
        String name = path;
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
            index = TextureIndexes.ARMOR_1_ALIAS.getIndexMin();
        }
        if (index == OLD_ARMOR_2) {
            index = TextureIndexes.ARMOR_1_ALIAS.getIndexMin();
        }
        if (index == OLD_WILD) {
            index = TextureIndexes.WILD_ALIAS.getIndexMin() + 12;
        }
        return index;
    }

    /**
     * ファイル名最後尾の数字を削除する
     * */
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
     * */
    public static String removeExtension(String fileName) {
        int lastSplitter = fileName.lastIndexOf(".");
        if (lastSplitter == -1) {
            return fileName;
        }
        return fileName.substring(0, lastSplitter);
    }

    /**
     * 一番外側のフォルダーまたはzipファイル名を取る
     * */
    public static Optional<String> getFirstParentName(String path, Path homePath, boolean isArchive) {
        if (isArchive) {
            //zipファイル名を取る
            String zipName = homePath.getFileName().toString();
            return Optional.of(zipName.substring(0, zipName.lastIndexOf(".")));
        } else {
            //一番外側のフォルダの名前を取る
            int firstSplitter = path.indexOf("/");
            if (firstSplitter == -1) return Optional.empty();
            return Optional.of(path.substring(0, firstSplitter));
        }
    }

}
