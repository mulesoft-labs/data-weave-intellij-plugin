package org.mule.tooling.lang.dw.preview;

import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

public class PreviewErrorPanel extends JPanel {

    private final JTextPane myContent;

    public PreviewErrorPanel(String message) {
        super(new BorderLayout());
        myContent = new JTextPane();
        myContent.setEditorKit(UIUtil.getHTMLEditorKit());
        myContent.setEditable(false);
        myContent.setBackground(UIUtil.getListBackground());
        final JScrollPane pane = ScrollPaneFactory.createScrollPane(myContent, true);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(pane);
        myContent.setText(createContent(message));
    }

    public void updateMessage(String error) {
        myContent.setText(createContent(error));
    }

    public String createContent(String message) {
        return "<html>\n" +
                "<header>\n" +
                "<style type='text/css'>\n" +
                ".ErrorMessage_wrapper_AI5Yq {\n" +
                "    min-height: 30px;\n" +
                "    border: 1px solid #ff6969;\n" +
                "    background: #ffe1e1;\n" +
                "    border-radius: 3px;\n" +
                "    margin: 10px 10px;\n" +
                "    color: #a90000;\n" +
                "    font-size: " + UIUtil.getLabelFont().getSize() + ";\n" +
                "    font-family: '" + UIUtil.getLabelFont().getName() + ",serif';\n" +
                "    padding: 10px;\n" +
                "    box-sizing: border-box;\n" +
                "    overflow: auto;\n" +
                "}\n" +
                "</style>\n" +
                "</header>\n" +
                "<body>\n" +
                "<pre class=\"ErrorMessage_wrapper_AI5Yq\">" + message + "</pre>" +
                "</body>\n" +
                "</html>";
    }


}
