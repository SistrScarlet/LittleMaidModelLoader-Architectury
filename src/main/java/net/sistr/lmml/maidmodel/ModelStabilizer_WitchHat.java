package net.sistr.lmml.maidmodel;

import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class ModelStabilizer_WitchHat extends ModelStabilizerBase {

	public static Identifier ftex = new Identifier("/mob/littleMaid/ALTERNATIVE/Stabilizer_MagicHat.png");
	public ModelRenderer WitchHat;
	public ModelRenderer WitchHat1;
	public ModelRenderer WitchHat2;
	public ModelRenderer WitchHat3;


	public ModelStabilizer_WitchHat() {
		// まじょこぼう
		textureWidth = 64;
		textureHeight = 32;
		
		WitchHat = new ModelRenderer(this, 0, 0);
		WitchHat1 = new ModelRenderer(this, 0, 0);
		WitchHat2 = new ModelRenderer(this, 0, 0);
		WitchHat3 = new ModelRenderer(this, 0, 0);
		WitchHat.setTextureOffset( 0, 15).addBox(-8F, 0F, -8F, 16, 1, 16, 0.0F);
		WitchHat.setTextureOffset( 0,  0).addBox(-4.5F, -4F, -4.5F, 9, 4, 9);
		WitchHat1.setTextureOffset(40, 4).addBox(-3F, -3F, -3F, 6, 3, 6).setRotationPoint(0F, -4F, 0F);
		WitchHat2.setTextureOffset(28, 0).addBox(-2F, -2F, -2F, 4, 2, 4).setRotationPoint(0F, -3F, 0F);
		WitchHat3.setTextureOffset( 0, 0).addBox(-1F, -2F, -1F, 2, 2, 2).setRotationPoint(0F, -2F, 0F);
		
		WitchHat.addChild(WitchHat1);
		WitchHat1.addChild(WitchHat2);
		WitchHat2.addChild(WitchHat3);
	}
	
//	@Override
	public void render(float f5) {
		GL11.glTranslatef(0F, -0.1F, 0F);
		WitchHat.render(f5);
	}
	
	@Override
	public Identifier getTexture() {
		return ftex;
	}
	
	@Override
	public String getName() {
		return "WitchHat";
	}
	
	@Override
	public boolean isLoadAnotherTexture() {
		return true;
	}

	@Override
	public float[] getArmorModelsSize() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	
}
