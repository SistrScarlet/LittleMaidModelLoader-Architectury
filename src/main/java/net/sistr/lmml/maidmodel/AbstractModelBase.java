package net.sistr.lmml.maidmodel;

/**
 * マルチモデル用識別クラス<br>
 * インターフェースでもいいような気がする。
 *
 */
public abstract class AbstractModelBase {

	/**
	 * アーマーモデルのサイズを返す。
	 * サイズは内側のものから。
	 */
	public abstract float[] getArmorModelsSize();

}
