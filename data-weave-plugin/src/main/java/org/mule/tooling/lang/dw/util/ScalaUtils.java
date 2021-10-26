package org.mule.tooling.lang.dw.util;

import org.jetbrains.annotations.NotNull;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.mutable.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScalaUtils {
  @NotNull
  public static <T> List<T> toList(Seq<T> overloads) {
    return new ArrayList<>(JavaConversions.asJavaCollection(overloads));
  }

  @NotNull
  public static <T> T[] toArray(Seq<T> overloads, T[] a) {
    return JavaConversions.asJavaCollection(overloads).toArray(a);
  }

  @NotNull
  public static <T> Option<T> toOption(Optional<T> optional) {
    if (optional.isPresent()) {
      return Option.<T>apply(optional.get());
    } else {
      return Option.<T>empty();
    }
  }

  @NotNull
  public static <T> Optional<T> toOptional(Option<T> optional) {
    if (optional.isDefined()) {
      return Optional.of(optional.get());
    } else {
      return Optional.empty();
    }
  }

  public static <T> Seq<T> toSeq(T... values) {
    Builder<T, Seq<T>> fieldsBuilder = Seq$.MODULE$.<T>newBuilder();
    for (T value : values) {
      fieldsBuilder.$plus$eq(value);
    }
    return fieldsBuilder.result();
  }
}
