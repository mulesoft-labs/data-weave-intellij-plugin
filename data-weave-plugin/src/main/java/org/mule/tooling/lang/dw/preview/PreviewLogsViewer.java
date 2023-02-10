package org.mule.tooling.lang.dw.preview;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.filters.UrlFilter;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.mule.tooling.lang.dw.filter.DataWeaveFilter;
import org.mule.weave.v2.agent.api.event.WeaveLogMessage;

import java.util.List;
import java.util.stream.Collectors;

public class PreviewLogsViewer extends BorderLayoutPanel implements Disposable {

    private ConsoleView console;

    public PreviewLogsViewer(Project project) {
        initUI(project);
    }

    void initUI(Project project) {
        TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        builder.addFilter(new DataWeaveFilter(project));
        builder.addFilter(new UrlFilter());
        console = builder.getConsole();
        add(console.getComponent());
    }

    public void clear() {
        console.clear();
    }

    public void logInfo(List<WeaveLogMessage> weaveLogMessages) {
        String result = weaveLogMessages.stream().map((logMessage) -> "[INFO] " + logMessage.timestamp() + " : " + logMessage.message()).collect(Collectors.joining("\n"));
        if (!result.isEmpty()) {
            console.print(result, ConsoleViewContentType.SYSTEM_OUTPUT);
        }
    }

    public void logError(String message) {
        console.print(message, ConsoleViewContentType.ERROR_OUTPUT);
    }

    @Override
    public void dispose() {
        console.dispose();
    }
}
