package net.sistr.littlemaidmodelloader.maidmodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.Vector3f;

public abstract class ModelBoxBase {
	protected TexturedQuad[] quadList;
	public float posX1;
	public float posY1;
	public float posZ1;
	public float posX2;
	public float posY2;
	public float posZ2;

	@Deprecated
	protected PositionTextureVertex[] vertexPositions;
	@Deprecated
	public String boxName;

	/**
	 * こちらを必ず実装すること。
	 * @param pMRenderer
	 * @param pArg
	 */
	public ModelBoxBase(ModelRenderer pMRenderer, Object ... pArg) {
	}

	@Deprecated
	public void render(Tessellator par1Tessellator, float par2) {

	}

	@Deprecated
	public ModelBoxBase setBoxName(String pName) {
		boxName = pName;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public static class PositionTextureVertex {
		public final Vector3f position;
		public final float textureU;
		public final float textureV;

		public PositionTextureVertex(float x, float y, float z, float texU, float texV) {
			this(new Vector3f(x, y, z), texU, texV);
		}

		public PositionTextureVertex setTextureUV(float texU, float texV) {
			return new PositionTextureVertex(this.position, texU, texV);
		}

		public PositionTextureVertex(Vector3f posIn, float texU, float texV) {
			this.position = posIn;
			this.textureU = texU;
			this.textureV = texV;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class TexturedQuad {
		public PositionTextureVertex[] vertexPositions;
		public Vector3f normal;

		public TexturedQuad(PositionTextureVertex[] positionsIn, float u1, float v1, float u2, float v2, float texWidth, float texHeight) {
			this.vertexPositions = positionsIn;
			float f = 0.0F / texWidth;
			float f1 = 0.0F / texHeight;
			positionsIn[0] = positionsIn[0].setTextureUV(u2 / texWidth - f, v1 / texHeight + f1);
			positionsIn[1] = positionsIn[1].setTextureUV(u1 / texWidth + f, v1 / texHeight + f1);
			positionsIn[2] = positionsIn[2].setTextureUV(u1 / texWidth + f, v2 / texHeight - f1);
			positionsIn[3] = positionsIn[3].setTextureUV(u2 / texWidth - f, v2 / texHeight - f1);
		}

		public TexturedQuad(PositionTextureVertex[] positionsIn) {
			this.vertexPositions = positionsIn;
		}

		public void flipFace() {
			int i = vertexPositions.length;

			for(int j = 0; j < i / 2; ++j) {
				PositionTextureVertex modelrenderer$positiontexturevertex = vertexPositions[j];
				vertexPositions[j] = vertexPositions[i - 1 - j];
				vertexPositions[i - 1 - j] = modelrenderer$positiontexturevertex;
			}
			if (normal != null) {
				this.normal.multiplyComponentwise(-1.0F, 1.0F, 1.0F);
			}
			/*
			PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[this.vertexPositions.length];

			for (int i = 0; i < this.vertexPositions.length; ++i)
			{
				apositiontexturevertex[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
			}

			this.vertexPositions = apositiontexturevertex;
			 */
		}
	}

}
