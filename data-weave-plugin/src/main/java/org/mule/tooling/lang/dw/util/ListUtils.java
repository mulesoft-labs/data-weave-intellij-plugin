package org.mule.tooling.lang.dw.util;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListUtils {

    @Nullable
    public static <T> T head(List<T> element) {
        if (element.isEmpty()) {
            return null;
        }
        return element.get(0);
    }

    @Nullable
    public static <T> T last(List<T> element) {
        if (element.isEmpty()) {
            return null;
        }
        return element.get(element.size() - 1);
    }
}
