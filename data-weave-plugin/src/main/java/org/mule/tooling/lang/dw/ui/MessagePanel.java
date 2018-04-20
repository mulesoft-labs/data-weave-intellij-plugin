package org.mule.tooling.lang.dw.ui;

import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.StatusText;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JBPanel<MessagePanel> {


    public MessagePanel(String message) {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UIUtil.getTreeTextBackground());
        StatusText statusText = new StatusText() {

            @Override
            protected boolean isStatusVisible() {
                return true;
            }
        };
        statusText.setText(message);
        this.add(statusText.getComponent());
    }


}
