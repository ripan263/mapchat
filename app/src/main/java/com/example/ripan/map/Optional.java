package com.example.ripan.map;

public class Optional<T> {
    //private static final Optional<?> EMPTY = new Optional<>(null, Null.INSTANCE);

    private T object;
    private boolean isPresent;

    protected Optional() {
        isPresent = false;
    }

    protected Optional(T x) {
        isPresent = true;
        object = x;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    //@SuppressWarnings("unchecked")
    public static <T> Optional<T> empty() {
        return new Optional<T>();
    }

    public static <T> Optional<T> ofNullable(T value) {
        if (value == null) {
            return empty();
        } else {
            return of(value);
        }
    }

    public T value() {
        return object;
    }

    public boolean isPresent() {
        return isPresent;
    }

}
