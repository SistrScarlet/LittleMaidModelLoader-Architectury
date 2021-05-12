package net.sistr.littlemaidmodelloader.entrypoint;

import net.fabricmc.api.ClientModInitializer;
import net.sistr.littlemaidmodelloader.setup.ClientSetup;

public class ClientEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSetup.init();
    }
}
