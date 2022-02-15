package org.mule.tooling.als.settings;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class ALSSettingUI {
  private JPanel root;

  private JPanel tableContainer;
  private JPanel tablePanel;

  ListTableModel<DialectsRegistry.DialectLocation> model;

  public ALSSettingUI() {
  }

  public JPanel getTopField() {
    return root;
  }


  private void createUIComponents() {
    final JBTable dialectsTable = new JBTable();
    final ColumnInfo<DialectsRegistry.DialectLocation, String> nameColumn = new ColumnInfo<>("Name") {

      @Override
      public @Nullable String valueOf(DialectsRegistry.DialectLocation dialect) {
        return dialect.getName();
      }
    };

    final ColumnInfo<DialectsRegistry.DialectLocation, String> urlColumn = new ColumnInfo<>("Url") {

      @Override
      public @Nullable String valueOf(DialectsRegistry.DialectLocation dialect) {
        return dialect.getDialectFilePath();
      }
    };

    final ColumnInfo<DialectsRegistry.DialectLocation, String>[] columnInfos = new ColumnInfo[]{
            nameColumn, urlColumn
    };

    List<DialectsRegistry.DialectLocation> dialectsRegistry = DialectsRegistry.getInstance().getDialectsRegistry();
    model = new ListTableModel<>(columnInfos, dialectsRegistry);
    dialectsTable.setModel(model);
    dialectsTable.getEmptyText().setText("No Dialects where registered.");
    ToolbarDecorator decorator = ToolbarDecorator.createDecorator(dialectsTable);
    tablePanel = ToolbarDecorator.createDecorator(dialectsTable)
            .setToolbarPosition(ActionToolbarPosition.TOP)
            .setAddAction(new AnActionButtonRunnable() {
              @Override
              public void run(AnActionButton anActionButton) {
                DialectInputDialog newDialect = new DialectInputDialog();
                newDialect.setTitle("New Dialect");
                newDialect.show();
                model.addRow(new DialectsRegistry.DialectLocation(newDialect.getName(), newDialect.getPath()));
              }
            })
            .setEditAction(new AnActionButtonRunnable() {
              @Override
              public void run(AnActionButton anActionButton) {
                DialectInputDialog newDialect = new DialectInputDialog();
                newDialect.setTitle("New Dialect");
                DialectsRegistry.DialectLocation item = model.getItem(dialectsTable.getSelectedRow());
                newDialect.setName(item.getName());
                newDialect.setPath(item.getDialectFilePath());
                newDialect.show();
                item.setName(newDialect.getName());
                item.setDialectFilePath(newDialect.getPath());
              }
            })
            .createPanel();
  }

  public List<DialectsRegistry.DialectLocation> dialects() {
    return model.getItems();
  }
}
