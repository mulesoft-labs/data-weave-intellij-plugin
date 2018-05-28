package org.mule.tooling.lang.dw.util;

public class CacheEntry<T> {
    private T value;
    private boolean valid;

    public CacheEntry(T value) {
        this.value = value;
        this.valid = true;
    }

    public void invalidate() {
        this.valid = false;
    }

    public T getValue() {
        return this.value;
    }

    public boolean isValid() {
        return valid;
    }
}