package org.mule.tooling.runtime.artifact_json;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClassBasedCompletionContributor extends CompletionProvider<CompletionParameters> {

  private Class<?> rootClass;
  private Predicate<PsiFile> filter;

  public ClassBasedCompletionContributor(Class<?> rootClass, Predicate<PsiFile> filter) {
    this.rootClass = rootClass;
    this.filter = filter;
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    if (!filter.test(parameters.getOriginalFile())) {
      return;
    }

    PsiElement position = parameters.getPosition();
    PsiElement parent = position.getParent();
    List<PathItem> path = getSelectionPath(parent);

    Class<?> contextClass = rootClass;
    Field declaredField = null;
    for (PathItem pathItem: path) {
      if (pathItem instanceof ObjectProperty) {
        declaredField = ReflectionUtil.getDeclaredField(contextClass, ((ObjectProperty) pathItem).name);
        if (declaredField != null) {
          contextClass = declaredField.getType();
        }
      } else if (pathItem instanceof ArrayItemProperty) {
        if (declaredField != null) {
          Type genericType = declaredField.getGenericType();
          contextClass = resolveClass(genericType);
          declaredField = null;
        }
      }
    }

    JsonObject parentOfType = PsiTreeUtil.getParentOfType(position.getParent(), JsonObject.class);
    if (parentOfType != null) {
      List<String> existingKeys = parentOfType.getPropertyList().stream().map(JsonProperty::getName).collect(Collectors.toList());
      if (String.class.isAssignableFrom(contextClass)) {
        result.addElement(LookupElementBuilder.create('"' + '"'));
      } else if (Iterable.class.isAssignableFrom(contextClass) || contextClass.isArray()) {
        result.addElement(LookupElementBuilder.create("[]"));
      } else if (Boolean.class.isAssignableFrom(contextClass)) {
        result.addElement(LookupElementBuilder.create("true").bold());
        result.addElement(LookupElementBuilder.create("false").bold());
      } else if (contextClass.isEnum()) {
        Object[] values = contextClass.getEnumConstants();
        for (Object value: values) {
          String name = ((Enum) value).name();
          result.addElement(LookupElementBuilder.create(name, '"' + name + '"'));
        }
      } else {
        final List<Field> classDeclaredFields = ReflectionUtil.collectFields(contextClass);
        for (Field classDeclaredField: classDeclaredFields) {
          if (!Modifier.isStatic(classDeclaredField.getModifiers())) {
            if (!existingKeys.contains(classDeclaredField.getName())) {
              LookupElementBuilder elementBuilder = LookupElementBuilder.create('"' + classDeclaredField.getName() + '"');
              elementBuilder = elementBuilder.withTypeText(classDeclaredField.getType().getSimpleName());
              result.addElement(elementBuilder);
            }
          }
        }
      }
    }
    result.stopHere();
  }

  private Class<?> resolveClass(Type genericType) {
    Class<?> contextClass = null;
    if (genericType instanceof Class<?>) {
      contextClass = (Class<?>) genericType;
    } else if (genericType instanceof ParameterizedType) {
      contextClass = resolveClass(((ParameterizedType) genericType).getRawType());
    } else if (genericType instanceof WildcardType) {
      Type[] upperBounds = ((WildcardType) genericType).getUpperBounds();
      if (upperBounds != null && upperBounds.length > 0) {
        contextClass = resolveClass(upperBounds[0]);
      }
    }
    return contextClass;
  }


  private List<PathItem> getSelectionPath(PsiElement parent) {
    final List<PathItem> result = new ArrayList<>();
    PsiElement current = parent;
    while (!(current instanceof PsiFile)) {
      if (current instanceof JsonProperty) {
        String name = ((JsonProperty) current).getName();
        if (!name.trim().equalsIgnoreCase(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) {
          result.add(0, new ObjectProperty(name));
        }
      } else if (current instanceof JsonArray) {
        result.add(0, new ArrayItemProperty());
      }
      current = current.getParent();
    }
    return result;
  }

  interface PathItem {
  }

  private static class ObjectProperty implements PathItem {
    private String name;

    public ObjectProperty(String name) {
      this.name = name;
    }
  }

  private static class ArrayItemProperty implements PathItem {
    public ArrayItemProperty() {
    }
  }
}
