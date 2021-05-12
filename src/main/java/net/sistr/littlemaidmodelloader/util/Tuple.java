package net.sistr.littlemaidmodelloader.util;

public final class Tuple<T, M> {
    private final T a;
    private final M b;

    public Tuple(T a, M b) {
        this.a = a;
        this.b = b;
    }

    public T getA() {
        return a;
    }

    public M getB() {
        return b;
    }
}
