package org.mule.tooling.runtime.framework.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.framework.MuleFrameworkUtil;
import org.mule.tooling.runtime.RuntimeIcons;

import javax.swing.*;

public class MuleFacetType extends FacetType<MuleFacet, MuleFacetConfiguration> {
  public static final FacetTypeId<MuleFacet> TYPE_ID = new FacetTypeId<MuleFacet>(MuleFacet.ID);

  public MuleFacetType() {
    super(TYPE_ID, MuleFacet.ID, "Mule ESB");
  }

  @Override
  public MuleFacetConfiguration createDefaultConfiguration() {
    return new MuleFacetConfiguration();
  }

  @Override
  public MuleFacet createFacet(@NotNull Module module,
                               String s,
                               @NotNull MuleFacetConfiguration configuration,
                               Facet facet) {
    return new MuleFacet(this, module, s, configuration, facet);
  }

  @Override
  public boolean isSuitableModuleType(ModuleType type) {
    return MuleFrameworkUtil.isAcceptableModuleType(type);
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return RuntimeIcons.MuleRunConfigIcon;
  }
}
