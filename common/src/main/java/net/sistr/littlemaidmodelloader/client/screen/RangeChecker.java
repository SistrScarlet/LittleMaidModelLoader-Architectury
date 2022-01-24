package net.sistr.littlemaidmodelloader.client.screen;

public final class RangeChecker {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public RangeChecker(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean check(double x, double y) {
        return checkFromWidth(x, y, this.x, this.y, width, height);
    }

    public static boolean checkFromWidth(double x, double y, double baseX, double baseY, double width, double height) {
        return check(x, y, baseX, baseY, baseX + width, baseY + height);
    }

    public static boolean check(double x, double y, double minX, double minY, double maxX, double maxY) {
        return minX <= x && x < maxX && minY <= y && y < maxY;
    }

}
