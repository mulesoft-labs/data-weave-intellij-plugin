package org.mule.tooling.restsdk.utils;

import com.intellij.json.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.*;

import java.util.Iterator;
import java.util.List;

public class SelectionPath {

  public static final String ARRAY_ITEM = "[]";
  public static final String ANY_ELEMENT = "*";
  public static final String PARENT_SELECTOR = "..";

  public static SelectionPath DOCUMENT = new SelectionPath(null, "#", Kind.DOCUMENT);
  public static SelectionPath PARENT = new SelectionPath(null, PARENT_SELECTOR, Kind.PARENT);
  @Nullable
  private SelectionPath parent;
  private String name;
  private Kind kind;

  public static SelectionPath pathOfYaml(PsiElement element) {
    if (element instanceof YAMLDocument || element instanceof PsiFile) {
      return SelectionPath.DOCUMENT;
    } else if (element instanceof YAMLKeyValue) {
      return pathOfYaml(element.getParent()).child(((YAMLKeyValue) element).getKeyText());
    } else if (element instanceof YAMLSequenceItem && element.getParent() instanceof YAMLSequence) {
      List<YAMLSequenceItem> items = ((YAMLSequence) element.getParent()).getItems();
      return pathOfYaml(element.getParent()).arrayItem(items.indexOf(element));
    } else {
      return pathOfYaml(element.getParent());
    }
  }

  public SelectionPath(@Nullable SelectionPath parent, String name, Kind kind) {
    this.parent = parent;
    this.name = name;
    this.kind = kind;
  }

  public String getName() {
    return name;
  }

  public boolean matches(SelectionPath path) {
    boolean matches;
    if (path.parent != null && parent != null) {
      matches = parent.matches(path.parent);
    } else {
      matches = path.parent == null && parent == null;
    }
    if (matches) {
      matches = path.name.equals(name) || path.name.equals(ANY_ELEMENT);
    }
    return matches;
  }

  @Nullable
  public PsiElement selectYaml(@NotNull PsiElement element) {
    PsiElement parentNode = element;
    if (parent != null) {
      parentNode = parent.selectYaml(element);
    }
    PsiElement match = null;
    if (kind.equals(Kind.PARENT)) {
      match = PsiTreeUtil.getParentOfType(parentNode, YAMLMapping.class);
    } else if (kind.equals(Kind.INDEX)) {
      if (parentNode instanceof YAMLSequence) {
        Iterator<YAMLSequenceItem> iterator = ((YAMLSequence) parentNode).getItems().iterator();
        if (iterator.hasNext()) {
          match = iterator.next().getValue();
        }
      }
    } else if (kind.equals(Kind.ANY)) {
      if (parentNode instanceof YAMLMapping) {
        Iterator<YAMLKeyValue> keyValues = ((YAMLMapping) parentNode).getKeyValues().iterator();
        if (keyValues.hasNext()) {
          match = keyValues.next().getValue();
        }
      }
    } else if (kind.equals(Kind.FIELD)) {
      if (parentNode instanceof YAMLMapping) {
        YAMLKeyValue keyValueByKey = ((YAMLMapping) parentNode).getKeyValueByKey(name);
        if (keyValueByKey != null) {
          match = keyValueByKey.getValue();
        }
      } else if (parentNode instanceof YAMLDocument) {
        YAMLMapping yamlMapping = PsiTreeUtil.getChildOfType(parentNode, YAMLMapping.class);
        if (yamlMapping != null) {
          YAMLKeyValue keyValueByKey = yamlMapping.getKeyValueByKey(name);
          if (keyValueByKey != null) {
            match = keyValueByKey.getValue();
          }
        }
      }
    } else if (kind.equals(Kind.DOCUMENT)) {
      if (element instanceof YAMLDocument) {
        match = element;
      } else if (element instanceof PsiFile) {
        match = PsiTreeUtil.getChildOfType(element, YAMLDocument.class);
      }
    }
    return match;
  }


  @Nullable
  public PsiElement selectJson(@NotNull PsiElement element) {
    PsiElement parentNode = element;
    if (parent != null) {
      parentNode = parent.selectJson(element);
    }
    PsiElement match = null;
    if (kind.equals(Kind.PARENT)) {
      match = PsiTreeUtil.getParentOfType(parentNode, JsonObject.class);
    } else if (kind.equals(Kind.INDEX)) {
      if (parentNode instanceof JsonArray) {
        Iterator<JsonValue> iterator = ((JsonArray) parentNode).getValueList().iterator();
        if (iterator.hasNext()) {
          match = iterator.next();
        }
      }
    } else if (kind.equals(Kind.ANY)) {
      if (parentNode instanceof JsonObject) {
        Iterator<JsonProperty> keyValues = ((JsonObject) parentNode).getPropertyList().iterator();
        if (keyValues.hasNext()) {
          match = keyValues.next().getValue();
        }
      }
    } else if (kind.equals(Kind.FIELD)) {
      if (parentNode instanceof JsonObject) {
        JsonProperty keyValueByKey = ((JsonObject) parentNode).getPropertyList().stream()
                .filter((jp) -> jp.getName().equals(name))
                .findFirst()
                .orElse(null);
        if (keyValueByKey != null) {
          match = keyValueByKey.getValue();
        }
      }
    } else if (kind.equals(Kind.DOCUMENT)) {
      if (element instanceof PsiFile) {
        match = PsiTreeUtil.getChildOfType(element, JsonElement.class);
      } else if (element.getParent() instanceof PsiFile) {
        match = element;
      }
    }
    return match;
  }

  public SelectionPath arrayItem(int index) {
    return new SelectionPath(this, String.valueOf(index), Kind.INDEX);
  }

  public SelectionPath child(String name) {
    return new SelectionPath(this, name, Kind.FIELD);
  }

  public SelectionPath any() {
    return new SelectionPath(this, ANY_ELEMENT, Kind.ANY);
  }

  public SelectionPath parent() {
    return new SelectionPath(this, PARENT_SELECTOR, Kind.PARENT);
  }

  public String toString() {
    if (parent != null) {
      return parent.toString() + "/" + name;
    } else {
      return name;
    }
  }

  public Kind getKind() {
    return kind;
  }

  public SelectionPath getParent() {
    return parent;
  }

  public static enum Kind {
    FIELD, INDEX, PARENT, DOCUMENT, ANY
  }
}


