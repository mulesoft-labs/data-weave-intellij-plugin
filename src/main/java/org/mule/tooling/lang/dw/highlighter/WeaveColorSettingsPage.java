package org.mule.tooling.lang.dw.highlighter;


import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveIcons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class WeaveColorSettingsPage implements ColorSettingsPage {

  private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
          new AttributesDescriptor("Key", WeaveSyntaxHighlighter.KEY),
          new AttributesDescriptor("Infix Function", WeaveSyntaxHighlighter.INFIX_FUNCTION_CALL),
          new AttributesDescriptor("Comment", WeaveSyntaxHighlighter.COMMENT),
          new AttributesDescriptor("Variable", WeaveSyntaxHighlighter.VARIABLE),
  };

  @Nullable
  @Override
  public Icon getIcon() {
    return WeaveIcons.DataWeaveIcon;
  }

  @NotNull
  @Override
  public SyntaxHighlighter getHighlighter() {
    return WeaveSyntaxHighlighter.getInstance();
  }

  @NotNull
  @Override
  public String getDemoText() {
    return "%weave0.1\n" + "%output xml\n" + "---\n" + "{\n" + "  a: $a.b.c.d?,\n" + "  b: $a['b']['c']['d']?,\n" + "  c: $a['b'].c['d'].e?,\n" + "  d: $a.b[0][1]?,\n" + "  e: ['Mariano'][0]?\n}";
  }

  @Nullable
  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return new HashMap<>();
  }

  @NotNull
  @Override
  public AttributesDescriptor[] getAttributeDescriptors() {
    return DESCRIPTORS;
  }

  @NotNull
  @Override
  public ColorDescriptor[] getColorDescriptors() {
    return new ColorDescriptor[0];
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Weave";
  }
}
