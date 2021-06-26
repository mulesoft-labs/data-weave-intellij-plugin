package org.mule.tooling.restsdk.schema;

import com.intellij.openapi.project.Project;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class RestSdkDescriptorJsonSchemaProviderFactory implements JsonSchemaProviderFactory {
  @NotNull
  public List<JsonSchemaFileProvider> getProviders(@NotNull Project project) {

    List<JsonSchemaFileProvider> providers = new LinkedList<>();
    providers.add(new RestSdkDescriptorFileProvider(project));

    return providers;
  }
}
