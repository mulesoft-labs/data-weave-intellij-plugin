package org.mule.tooling.lang.dw.preview;

import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.Nullable;
import org.mule.weave.v2.debugger.event.WeaveLogMessage;

import java.util.List;

public class PreviewLogsViewer extends BorderLayoutPanel {

    private ListTableModel<WeaveLogMessage> model;

    public PreviewLogsViewer() {
        initUI();
    }

    void initUI() {
        final JBTable logTable = new JBTable();
        logTable.setShowColumns(true);


        final ColumnInfo timeStamp = new ColumnInfo<WeaveLogMessage, String>("Time") {
            @Nullable
            @Override
            public String valueOf(WeaveLogMessage message) {
                return message.timestamp();

            }
        };
        final ColumnInfo message = new ColumnInfo<WeaveLogMessage, String>("Message") {
            @Nullable
            @Override
            public String valueOf(WeaveLogMessage message) {
                return message.message();

            }


        };
        model = new ListTableModel<>(timeStamp, message);

        logTable.setModel(model);

        this.addToCenter(logTable);
    }

    public void setLogs(List<WeaveLogMessage> weaveLogMessages) {
        model.setItems(weaveLogMessages);
    }
}
