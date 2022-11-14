package org.mule.tooling.restsdk.schema;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.SchemaType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

import static org.mule.tooling.restsdk.utils.RestSdkHelper.isRestSdkDescriptor;

public class RestSdkDescriptorFileProvider implements JsonSchemaFileProvider {

    private final Project project;

    public RestSdkDescriptorFileProvider(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public boolean isAvailable(@NotNull VirtualFile file) {
        try {
            String text = VfsUtilCore.loadText(file, 2000);
            return isRestSdkDescriptor(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public @NotNull @Nls String getName() {
        return "Rest Connector Descriptor";
    }

    @Override
    public @Nullable VirtualFile getSchemaFile() {
        URL resource = RestSdkDescriptorFileProvider.class.getResource("/schema/RestSdkDescriptor.json");
        assert resource != null;
        return VfsUtil.findFileByURL(resource);
    }

    @Override
    public @NotNull SchemaType getSchemaType() {
        return SchemaType.userSchema;
    }
}
