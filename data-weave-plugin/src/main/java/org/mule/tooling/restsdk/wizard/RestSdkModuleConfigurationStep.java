package org.mule.tooling.restsdk.wizard;

import amf.apicontract.client.platform.model.domain.api.WebApi;
import amf.core.client.platform.model.document.Document;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.restsdk.utils.RestSdkHelper;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

public class RestSdkModuleConfigurationStep {
    private JTextField restSdkVersion;
    private JPanel container;
    private JTextField name;
    private TextFieldWithBrowseButton apiSpecTextField;

    public RestSdkModuleConfigurationStep() {
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, false, false) {
            @Override
            public void validateSelectedFiles(VirtualFile @NotNull [] files) {
                updateConnectorName(files[0]);
            }
        }.withFileFilter(file -> {
            boolean caseSensitive = file.getFileSystem().isCaseSensitive();
            String extension = file.getExtension();
            return Comparing.equal(extension, "yaml", caseSensitive)
                    || Comparing.equal(extension, "raml", caseSensitive)
                    || Comparing.equal(extension, "json", caseSensitive);
        });
        apiSpecTextField.addBrowseFolderListener(null, null, null, fileChooserDescriptor);
        apiSpecTextField.getTextField().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateConnectorName(getApiSpecVirtualFile());
            }
        });
    }

    @Nullable VirtualFile getApiSpecVirtualFile() {
        String fileName = apiSpecTextField.getText();
        return fileName.isBlank() ? null : VirtualFileManager.getInstance().findFileByNioPath(Path.of(fileName));
    }

    private VirtualFile lastFileProcessed = null;

    private void updateConnectorName(VirtualFile file) {
        if (Objects.equals(lastFileProcessed, file))
            return;
        if (!file.exists())
            return;
        name.setEnabled(false);
        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Reading API") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                Document document = RestSdkHelper.parseWebApi(file);
                String inferredName = document == null ? null : apiToName((WebApi) document.encodes());
                SwingUtilities.invokeLater(() -> {
                    if (inferredName != null) {
                        name.setText("mule4-connector-" + inferredName);
                        lastFileProcessed = file;
                    }
                    name.setEnabled(true);
                });
            }
        });
    }

    private static @Nullable String apiToName(@NotNull WebApi api) {
        var f = api.name();
        if (f != null)
        {
            var str = f.value();
            if (str != null && !str.isBlank()) {
                return withDashes(str);
            }
        }
        f = api.description();
        if (f != null)
        {
            var str = f.value();
            if (str != null && !str.isBlank()) {
                return withDashes(str);
            }
        }
        var o = api.provider();
        if (o != null) {
            f = o.name();
            if (f != null) {
                var str = f.value();
                if (str != null && !str.isBlank()) {
                    return withDashes(str);
                }
            }
        }
        return null;
    }

    static String withDashes(String str) {
        return str.toLowerCase(Locale.ROOT).replaceAll("\\W+", " ").replaceAll("\\s+", "-");
    }

    public JTextField getRestSdkVersion() {
        return restSdkVersion;
    }

    public JTextField getName() {
        return name;
    }


    public JPanel getContainer() {
        return container;
    }

    public TextFieldWithBrowseButton getApiSpecTextField() {
        return apiSpecTextField;
    }
}
