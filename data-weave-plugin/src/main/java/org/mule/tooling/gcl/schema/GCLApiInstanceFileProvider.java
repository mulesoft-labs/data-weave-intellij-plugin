package org.mule.tooling.gcl.schema;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public class GCLApiInstanceFileProvider extends AbstractGCLFileProvider {

    public GCLApiInstanceFileProvider(@NotNull Project project) {
        super(project);
    }

    @Override
    public boolean isAvailable(@NotNull VirtualFile file) {
        return getKind(project, file) != null || getApiInstance(project, file) != null;
    }

    @Override
    public JsonSchemaVersion getSchemaVersion() {
        return JsonSchemaVersion.SCHEMA_7;
    }

    @Override
    public @NotNull @Nls String getName() {
        return "GCL Descriptor";
    }

    @Override
    public @Nullable VirtualFile getSchemaFile() {
        final URL resource = GCLApiInstanceFileProvider.class.getResource("/gcl/schemas/GCL.json");
        assert resource != null;
        return VfsUtil.findFileByURL(resource);
    }

}
