package net.sistr.littlemaidmodelloader.multimodel;

import net.minecraft.util.math.MathHelper;
import net.sistr.littlemaidmodelloader.maidmodel.IModelCaps;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMMatrixStack;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMPose;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMRenderContext;

/**
 * マルチモデル用識別インターフェース
 */
//todo 不要メソッドの消去
public interface IMultiModel {

    /**
     *
     */
    void setupTransform(IModelCaps caps, MMMatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta);

    /**
     * @param caps         情報を受け取るやつ
     * @param limbAngle    移動量が加算されつづけた値
     * @param limbDistance 1tickの移動量
     * @param tickDelta    tick間の現在フレームの位置する割合
     */
    void animateModel(IModelCaps caps, float limbAngle, float limbDistance, float tickDelta);

    /**
     * @param caps              情報を受け取るやつ
     * @param limbAngle         移動量が加算されつづけた値
     * @param limbDistance      1tickの移動量
     * @param animationProgress エンティティの存在した期間(tick) + tick間の現在フレームの位置する割合
     * @param headYaw           エンティティが向いている方向
     * @param headPitch         エンティティが向いている方向
     */
    void setAngles(IModelCaps caps, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch);

    /**
     * 描画
     */
    void render(MMRenderContext context);

    /**
     * アイテム保持位置の調整
     */
    void adjustHandItem(MMMatrixStack matrices, boolean isLeft);

    /**
     * テクスチャの横サイズ
     */
    int getTextureWidth();

    /**
     * テクスチャの縦サイズ
     */
    int getTextureHeight();

    /**
     * 内側防具サイズ
     */
    float getInnerArmorSize();

    /**
     * 外側防具サイズ
     */
    float getOuterArmorSize();

    /**
     * 横幅
     */
    float getWidth(IModelCaps caps, MMPose pose);

    /**
     * 身長
     */
    float getHeight(IModelCaps caps, MMPose pose);

    /**
     * 目の高さ
     */
    float getEyeHeight(IModelCaps caps, MMPose pose);

    /**
     * (メイドさんの上に乗る)モデルのYオフセット
     */
    float getyOffset(IModelCaps caps);

    /**
     * (メイドさんを)上に乗せる時のオフセット高
     */
    float getMountedYOffset(IModelCaps caps);

    /**
     * ロープの取り付け位置調整用
     */
    float getLeashOffset(IModelCaps caps);

    /**
     * パーツをすべて表示する
     */
    void showAllParts(IModelCaps caps);

    /**
     * 部位ごとの装甲表示。
     *
     * @param parts 3:頭部。
     *              2:胴部。
     *              1:脚部
     *              0:足部
     * @param index 0:inner
     *              1:outer
     * @return 戻り値は基本 -1
     */
    int showArmorParts(int parts, int index);

    /**
     * ハードポイントに接続されたアイテムを表示する
     */
    void renderItems(IModelCaps pEntityCaps);

    /**
     * 主観視点の手を描画する
     */
    void renderFirstPersonHand(IModelCaps pEntityCaps);

    //計算関数

    static float sin(float value) {
        return MathHelper.sin(value);
    }

    static float cos(float value) {
        return MathHelper.cos(value);
    }

    static float sqrt(float value) {
        return MathHelper.sqrt(value);
    }

    static float floor(float value) {
        return MathHelper.floor(value);
    }

    static float ceil(float value) {
        return MathHelper.ceil(value);
    }

    static float abs(float value) {
        return MathHelper.abs(value);
    }

    static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (max < value) return max;
        return value;
    }

    static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

}
