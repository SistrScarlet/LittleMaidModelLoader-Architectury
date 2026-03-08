package net.sistr.littlemaidmodelloader.setup;

import net.sistr.littlemaidmodelloader.network.Networking;

public class ModSetup {

  public static void init() {
    Networking.INSTANCE.init();
  }
}
