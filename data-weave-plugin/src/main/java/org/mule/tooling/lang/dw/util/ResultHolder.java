package org.mule.tooling.lang.dw.util;

public class ResultHolder<T> {
    private T result;

    public ResultHolder() {
    }

    public T get() {
        return result;
    }

    public void set(T result) {
        this.result = result;
    }

    public boolean isEmpty() {
        return result == null;
    }

    public boolean nonEmpty() {
        return !isEmpty();
    }
}
