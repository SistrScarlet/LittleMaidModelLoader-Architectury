package net.sistr.littlemaidmodelloader;

//ModInitializerが最も最初に起こるため、その場合は判定不能
public class SideChecker {
    private static Side side;
    private static boolean initialized;

    public static void init(Side side) {
        if (initialized) {
            throw new IllegalStateException("既に初期化済みです。" + "side = " + side);
        }
        SideChecker.side = side;
        initialized = true;
    }

    public static Side getSide() {
        return side;
    }

    public static boolean isClient() {
        return side == Side.CLIENT;
    }

    public static boolean isServer() {
        return side == Side.SERVER;
    }

    public enum Side {
        CLIENT,
        SERVER;
    }

}
