package org.mule.tooling.lang.dw.util;


public enum WeaveExecutableType {

  MAPPING {
    @Override
    public String getMainClass() {
      return "org.mule.weave.v2.runtime.utils.WeaveMappingRunner";
    }

    @Override
    public String getLabel() {
      return "Mapping File";
    }
  }, MODULE {
    @Override
    public String getMainClass() {
      return "org.mule.weave.v2.runtime.utils.WeaveMainRunner";
    }

    @Override
    public String getLabel() {
      return "Module File";
    }
  };

  public abstract String getMainClass();

  public abstract String getLabel();


}
