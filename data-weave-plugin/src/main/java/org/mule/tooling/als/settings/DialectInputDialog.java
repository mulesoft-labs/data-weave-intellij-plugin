package org.mule.tooling.als.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

public class DialectInputDialog extends DialogWrapper {
  private TextFieldWithBrowseButton myFilePath;
  private JTextField myNameField;
  private JPanel myWholePanel;

  public DialectInputDialog() {
    super(true);

    setTitle("Weave Input");
    init();
  }

  public void init(String name, String pattern) {
    myNameField.setText(name);
    myFilePath.setText(pattern);
    setOKActionEnabled(!StringUtil.isEmpty(pattern) && !StringUtil.isEmpty(name));
  }


  @Nullable
  @Override
  protected JComponent createCenterPanel() {

    myFilePath.addBrowseFolderListener("Select Input File", "Select input file", null, FileChooserDescriptorFactory.createSingleFileDescriptor(), TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
    myFilePath.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(DocumentEvent e) {
        checkOkActionShouldBeEnabled();
      }
    });
    myNameField.addInputMethodListener(new InputMethodListener() {
      @Override
      public void inputMethodTextChanged(InputMethodEvent event) {
        checkOkActionShouldBeEnabled();
      }

      @Override
      public void caretPositionChanged(InputMethodEvent event) {
      }
    });
    return myWholePanel;
  }

  private void checkOkActionShouldBeEnabled() {
    setOKActionEnabled(!StringUtil.isEmpty(myNameField.getText()) && !StringUtil.isEmpty(myFilePath.getText()));
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return myNameField;
  }

  public String getName() {
    return myNameField.getText();
  }

  public String getPath() {
    return myFilePath.getText();
  }

  public void setName(String name) {
    myNameField.setText(name);
  }

  public void setPath(String dialectFilePath) {
    this.myFilePath.setText(dialectFilePath);
  }
}
