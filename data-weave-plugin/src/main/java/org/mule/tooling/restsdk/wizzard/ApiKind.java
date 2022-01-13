package org.mule.tooling.restsdk.wizzard;

public enum ApiKind {
  RAML {
    @Override
    public String getApiFile() {
      return "api.raml";
    }
  }, OPEN_API {
    @Override
    public String getApiFile() {
      return "api.yaml";
    }
  };

  public abstract String getApiFile();
}
