package org.mule.tooling.lang.dw.util;

import org.jetbrains.annotations.NotNull;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

public class ScalaUtils {
    @NotNull
    public static <T> List<T> toList(Seq<T> overloads) {
        return new ArrayList<>(JavaConversions.asJavaCollection(overloads));
    }

    @NotNull
    public static <T> T[] toArray(Seq<T> overloads, T[] a) {
        return JavaConversions.asJavaCollection(overloads).toArray(a);
    }
}
