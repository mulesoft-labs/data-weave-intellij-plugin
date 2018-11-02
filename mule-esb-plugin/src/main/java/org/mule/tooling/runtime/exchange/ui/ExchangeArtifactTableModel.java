package org.mule.tooling.runtime.exchange.ui;

import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.exchange.ExchangeArtifact;

public class ExchangeArtifactTableModel extends ListTableModel<ExchangeArtifact> {
    public ExchangeArtifactTableModel() {
        super(
            new ColumnInfo<ExchangeArtifact, String>("Name") {
                @Nullable
                @Override
                public String valueOf(ExchangeArtifact artifact) {
                    return artifact.getName();
                }
            },
            new ColumnInfo<ExchangeArtifact, String>("Version") {
                @Nullable
                @Override
                public String valueOf(ExchangeArtifact artifact) {
                    return artifact.getVersion();
                }
            },
            new ColumnInfo<ExchangeArtifact, String>("Description") {
                @Nullable
                @Override
                public String valueOf(ExchangeArtifact artifact) {
                    return artifact.getDescription();
                }
            },
            new ColumnInfo<ExchangeArtifact, String>("Group ID") {
                @Nullable
                @Override
                public String valueOf(ExchangeArtifact artifact) {
                    return artifact.getGroupId();
                }
            },
            new ColumnInfo<ExchangeArtifact, String>("Artifact ID") {
                @Nullable
                @Override
                public String valueOf(ExchangeArtifact artifact) {
                    return artifact.getArtifactId();
                }
            },
            new ColumnInfo<ExchangeArtifact, String>("Classifier") {
                @Nullable
                @Override
                public String valueOf(ExchangeArtifact artifact) {
                    return artifact.getClassifier() == null ? artifact.getClassifier() : "";
                }
            },
            new ColumnInfo<ExchangeArtifact, String>("Mule Runtime Version") {
                @Nullable
                @Override
                public String valueOf(ExchangeArtifact artifact) {
                    return artifact.getRuntimeVersion();
                }
            }
        );
    }

    @Override
    public boolean isSortable() {
        return true;
    }
}
