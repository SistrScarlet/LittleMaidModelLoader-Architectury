package net.sistr.lmml.setup;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.sistr.lmml.LittleMaidModelLoader;
import net.sistr.lmml.SideChecker;
import net.sistr.lmml.entity.compound.IHasMultiModel;
import net.sistr.lmml.maidmodel.ModelLittleMaid_Archetype;
import net.sistr.lmml.maidmodel.ModelLittleMaid_Aug;
import net.sistr.lmml.maidmodel.ModelLittleMaid_Orign;
import net.sistr.lmml.maidmodel.ModelLittleMaid_SR2;
import net.sistr.lmml.network.Networking;
import net.sistr.lmml.resource.loader.LMFileLoader;
import net.sistr.lmml.resource.manager.LMModelManager;

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
