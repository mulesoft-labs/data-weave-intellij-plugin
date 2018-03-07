package org.mule.tooling.lang.browser;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class BrowserToolWindowFactory implements ToolWindowFactory {


    public void createToolWindowContent(final Project project, final ToolWindow toolWindow) {

		final Browser browser = new Browser();
		final BrowserView view = new BrowserView(browser);

		browser.loadURL("https://anypoint.mulesoft.com");

		toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(view, "Browser", false));
	}
}