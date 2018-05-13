/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mule.tooling.lang.dw.breadcrums;

import com.intellij.lang.Language;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.xml.breadcrumbs.BreadcrumbsInfoProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveLanguage;
import org.mule.tooling.lang.dw.parser.psi.WeaveArrayExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveConditionalAttribute;
import org.mule.tooling.lang.dw.parser.psi.WeaveConditionalKeyValuePair;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveKey;
import org.mule.tooling.lang.dw.parser.psi.WeaveLiteralExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElement;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveQualifiedName;
import org.mule.tooling.lang.dw.parser.psi.WeaveSimpleAttribute;
import org.mule.tooling.lang.dw.parser.psi.WeaveSimpleKeyValuePair;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

import java.util.List;

public class WeaveBreadcrumbsInfoProvider extends BreadcrumbsInfoProvider {
    private final static Language[] LANGUAGES = new Language[]{WeaveLanguage.getInstance()};

    private final static int SCALAR_MAX_LENGTH = 20;

    @Override
    public Language[] getLanguages() {
        return LANGUAGES;
    }

    @Override
    public boolean acceptElement(@NotNull PsiElement e) {
        return e instanceof WeaveLiteralExpression || WeavePsiUtils.isArrayItem(e) || e instanceof WeaveNamedElement || e instanceof WeaveDocument || e instanceof WeaveSimpleKeyValuePair || e instanceof WeaveSimpleAttribute;
    }

    @NotNull
    @Override
    public String getElementInfo(@NotNull PsiElement e) {
        String result;
        if (e instanceof WeaveNamedElement) {
            result = ((WeaveNamedElement) e).getIdentifier().getName();
        } else if (e instanceof WeaveSimpleKeyValuePair) {
            String prefix = ":";
            if (e.getParent() instanceof WeaveConditionalKeyValuePair) {
                prefix = "?:";
            }
            result = getElementInfo(((WeaveSimpleKeyValuePair) e).getKey()) + prefix;
        } else if (e instanceof WeaveSimpleAttribute) {
            String prefix = ":";
            if (e.getParent() instanceof WeaveConditionalAttribute) {
                prefix = "?:";
            }
            result = "@" + getElementInfo(((WeaveSimpleAttribute) e).getQualifiedName()) + prefix;
        } else if (e instanceof WeaveKey) {
            WeaveQualifiedName identifier = ((WeaveKey) e).getQualifiedName();
            result = getElementInfo(identifier);
        } else if (e instanceof WeaveQualifiedName) {
            WeaveIdentifier nameIdentifier = ((WeaveQualifiedName) e).getIdentifier();
            if (nameIdentifier != null) {
                result = nameIdentifier.getName();
            } else {
                result = "DynamicKey";
            }
        } else if (e instanceof WeaveDocument) {
            String name = ((WeaveDocument) e).getQualifiedName();
            if (name == null) {
                result = "Document";
            } else {
                result = NameIdentifier.apply(name, Option.empty()).localName().name();
            }
        } else if (e instanceof WeaveLiteralExpression) {
            return StringUtil.first(e.getText(), SCALAR_MAX_LENGTH, true);
        } else {
            final PsiElement parent = e.getParent();
            if (parent instanceof WeaveArrayExpression) {

                final List<WeaveExpression> items = ((WeaveArrayExpression) parent).getExpressionList();
                if (e instanceof WeaveExpression) {
                    result = "Item " + getIndexOf(items, e);
                } else {
                    result = "Item " + getIndexOf(items, e.getPrevSibling());
                }
            } else {
                result = "Item";
            }
        }
        return result;
    }

    @Nullable
    @Override
    public String getElementTooltip(@NotNull PsiElement e) {
        return null;
    }

    @NotNull
    private static String getIndexOf(@NotNull List<?> list, Object o) {
        int i = list.indexOf(o);
        if (i >= 0) {
            return String.valueOf(1 + i) + '/' + list.size();
        } else {
            return "";
        }
    }
}
