package org.mule.tooling.restsdk.wizzard;

public enum ApiKind {
  RAML("api.raml", "RAML"),
  OAS_JSON("api.json", "OAS (JSON)"),
  OAS_YAML("api.yaml", "OAS (YAML)");

  private final String apiFileName;
  private final String displayName;

  ApiKind(String apiFileName, String displayName) {
    this.apiFileName = apiFileName;
    this.displayName = displayName;
  }

  public String getApiFileName(){
    return apiFileName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
