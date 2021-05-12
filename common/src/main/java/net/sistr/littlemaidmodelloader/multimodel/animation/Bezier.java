package net.sistr.littlemaidmodelloader.multimodel.animation;

public final class Bezier implements Interpolator {
    private final float aX;
    private final float aY;
    private final float bX;
    private final float bY;

    public Bezier(float aX, float aY, float bX, float bY) {
        this.aX = Math.max(0F, Math.min(1F, aX));
        this.aY = aY;
        this.bX = Math.max(0F, Math.min(1F, bX));
        this.bY = bY;
    }

    public float get(float percent) {
        float x = calcKai(aX, bX, percent);
        float r = 1F - x;
        return 3F * r * r * x * aY
                + 3F * r * x * x * bY
                + x * x * x;
    }

    public float calcKai(float aValue, float bValue, float percent) {
        float a = 3F * aValue - 3F * bValue + 1F;
        float b = 3F * bValue - 6F * aValue;
        float c = 3F * aValue;
        float d = -percent;
        float p = (3F * a * c - b * b) / (3F * a * a);
        float q = (2F * b * b * b - 9F * a * b * c + 27F * a * a * d) / (27F * a * a * a);

        if (p == 0F && q == 0F) return 0F;

        float D = (27F * q * q + 4F * p * p * p) / 108F;

        float kai;
        float kai1;
        float kai2;

        if (D > 0F) {
            double temp0 = -q / 2F;
            double temp1 = Math.sqrt(D);
            float alpha = (float) Math.cbrt(temp0 + temp1);
            float beta = (float) Math.cbrt(temp0 - temp1);
            kai = alpha + beta;
            kai1 = kai2 = -1F;
            //虚数解
            /*float temp2 = -1F / 2F * (alpha + beta);
            float temp3 = (float) (Math.sqrt(3) / 2F * (alpha - beta));
            kai1 = temp2 + temp3;
            kai2 = temp2 - temp3;*/
        } else if (D == 0F) {
            float temp = (float) Math.cbrt(q / 2F);
            kai = -2F * temp;
            kai1 = temp;
            kai2 = -1F;
        } else if (D < 0F) {
            float alpha = -q / 2F;
            float beta = (float) Math.sqrt(-D);
            float temp = (float) (2F * Math.sqrt(Math.cbrt(alpha * alpha + beta * beta)));
            float temp1 = (float) Math.atan2(beta, alpha);
            kai = (float) (temp * Math.cos(temp1 / 3F));
            kai1 = (float) (temp * Math.cos((temp1 + 2F * Math.PI) / 3F));
            kai2 = (float) (temp * Math.cos((temp1 + 4F * Math.PI) / 3F));
        } else {
            throw new RuntimeException();
        }

        float temp = -b / (3F * a);
        float kaikai = kai + temp;
        float kaikai1 = kai1 + temp;
        float kaikai2 = kai2 + temp;

        if (0 <= kaikai && kaikai <= 1) return kaikai;
        if (0 <= kaikai1 && kaikai1 <= 1) return kaikai1;
        if (0 <= kaikai2 && kaikai2 <= 1) return kaikai2;

        return 0;
    }

}
