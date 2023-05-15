package org.mule.tooling.gcl.schema;

import com.intellij.openapi.project.Project;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class GCLJsonSchemaProviderFactory implements JsonSchemaProviderFactory {
    @NotNull
    public List<JsonSchemaFileProvider> getProviders(@NotNull Project project) {
        final List<JsonSchemaFileProvider> providers = new LinkedList<>();
        providers.add(new GCLApiInstanceFileProvider(project));
        return providers;
    }
}
