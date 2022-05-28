package net.sistr.littlemaidmodelloader.resource.holder;

import net.sistr.littlemaidmodelloader.resource.util.ResourceHelper;

import java.util.Map;
import java.util.Optional;

public class ConfigHolder {
    private final String packName;//LMMLResourcesフォルダ内のフォルダ/zip
    private final String parentName;//親ディレクトリ
    private final String fileName;//拡張子抜き

    private final Map<String, String> settings;

    public ConfigHolder(String packName, String parentName, String fileName, Map<String, String> settings) {
        this.packName = packName;
        this.parentName = parentName;
        this.fileName = fileName;
        this.settings = settings;
    }

    public String getName() {
        return packName + "." + parentName + "." + fileName;
    }

    public String getPackName() {
        return packName;
    }

    public String getParentName() {
        return parentName;
    }

    public String getFileName() {
        return fileName;
    }

    public Optional<String> getParameter(String parameterName) {
        return Optional.ofNullable(settings.get(parameterName));
    }

    public Optional<String> getSoundFileName(String soundName) {
        Optional<String> optional = getParameter(soundName);
        if (optional.filter(s -> s.contains(":")).isPresent()) {
            return optional;
        }
        return optional
                .map(ResourceHelper::removeNameLastIndex)//複数ファイルの数字ではなく、素で数字が末尾に付いてるファイル対策
                .map(fileName -> {
                    //[親].[ファイル]に変換
                    int firstSplitter = fileName.indexOf(".");
                    //.が無い
                    if (firstSplitter == -1) {
                        return "." + fileName;
                    }
                    int lastSplitter = fileName.lastIndexOf(".");
                    //.がひとつ
                    if (firstSplitter == lastSplitter) {
                        return fileName;
                    }
                    int secondLastSplitter = fileName.substring(0, lastSplitter).lastIndexOf(".");
                    return fileName.substring(secondLastSplitter + 1);
                })
                .map(fileName -> (packName + "." + fileName).toLowerCase());
    }
}
