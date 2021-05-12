package net.sistr.littlemaidmodelloader.setup;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.sistr.littlemaidmodelloader.SideChecker;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.maidmodel.ModelLittleMaid_Archetype;
import net.sistr.littlemaidmodelloader.maidmodel.ModelLittleMaid_Aug;
import net.sistr.littlemaidmodelloader.maidmodel.ModelLittleMaid_Orign;
import net.sistr.littlemaidmodelloader.maidmodel.ModelLittleMaid_SR2;
import net.sistr.littlemaidmodelloader.network.Networking;
import net.sistr.littlemaidmodelloader.resource.loader.LMFileLoader;
import net.sistr.littlemaidmodelloader.resource.manager.LMModelManager;

public class ServerSetup implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        SideChecker.init(SideChecker.Side.SERVER);

        //モデルを読み込む
        LMModelManager manager = LMModelManager.INSTANCE;
        manager.addModel("Default", ModelLittleMaid_Orign.class);
        manager.addModel("SR2", ModelLittleMaid_SR2.class);
        manager.addModel("Aug", ModelLittleMaid_Aug.class);
        manager.addModel("Archetype", ModelLittleMaid_Archetype.class);
        manager.setDefaultModel(manager.getModel("Default", IHasMultiModel.Layer.SKIN)
                .orElseThrow(RuntimeException::new));

        LMFileLoader.INSTANCE.load();

        Networking.INSTANCE.init();
    }
}
