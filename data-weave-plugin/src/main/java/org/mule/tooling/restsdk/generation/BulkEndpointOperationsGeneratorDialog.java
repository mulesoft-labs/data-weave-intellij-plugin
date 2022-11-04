package org.mule.tooling.restsdk.generation;

import amf.apicontract.client.platform.model.domain.EndPoint;
import amf.apicontract.client.platform.model.domain.Operation;
import amf.apicontract.client.platform.model.domain.api.WebApi;
import amf.core.client.platform.model.document.Document;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableModelAdapter;
import com.intellij.util.net.HTTPMethod;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLElementGenerator;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.mule.tooling.restsdk.completion.RestSdkCompletionService;
import org.mule.tooling.restsdk.utils.RestSdkHelper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intellij.util.ObjectUtils.tryCast;
import static org.mule.tooling.restsdk.utils.RestSdkPaths.ENDPOINTS_PATH;
import static org.mule.tooling.restsdk.utils.YAMLUtils.mergeInto;

public class BulkEndpointOperationsGeneratorDialog extends DialogWrapper {

    public static final ColumnInfo<?, ?> ENDPOINT = new ColumnInfo<Object, String>("Endpoint") {
        @Nullable
        @Override
        public String valueOf(Object o) {
            return o.toString();
        }

        @Override
        public Class<?> getColumnClass() {
            return TreeTableModel.class;
        }
    };

    static TableCellRenderer OPERATION_RENDERER = new BooleanTableCellRenderer();
    static TableCellEditor OPERATION_EDITOR = new BooleanTableCellEditor();

    private final Project project;
    private final Module module;
    private JPanel ui;
    private com.intellij.ui.treeStructure.treetable.TreeTable endpointsTree;
    private JLabel selectionLabel;
    private ListTreeTableModel treeTableModel;
    private VirtualFile descriptorFile;

    protected BulkEndpointOperationsGeneratorDialog(@Nullable Project project, Module module) {
        super(project);
        this.project = project;
        this.module = module;
        setTitle("Endpoint Operations Generator");
        setOKButtonText("Generate Operations");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return ui;
    }

    private void createUIComponents() {
        descriptorFile = RestSdkHelper.findDescriptorFile(module);
        if (descriptorFile == null)
            return;
        WebApi webApi = getWebApi(descriptorFile);
        if (webApi == null)
            return;
        EndpointNode top = createEndpointTreeModel(webApi);

        var columns = new ArrayList<ColumnInfo<?, ?>>();
        columns.add(ENDPOINT);
        webApi.endPoints().stream().flatMap(ep -> ep.operations().stream()).map(op -> op.method().value()).distinct().map(
                MethodColumnInfo::new).forEach(columns::add);

        treeTableModel = new ListTreeTableModel(top, columns.toArray(new ColumnInfo[0]));
        endpointsTree = new TreeTable(treeTableModel);
        endpointsTree.setRootVisible(false);
        endpointsTree.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            private final MouseAdapter mouseListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    TableModel tableModel = endpointsTree.getModel();
                    int cols = tableModel.getColumnCount();
                    for (int row : endpointsTree.getSelectedRows())
                        for (int col = 1; col < cols; col++)
                            tableModel.setValueAt(true, row, col);
                    ((TreeTableModelAdapter)tableModel).fireTableDataChanged();
                }
            };
            private boolean showing = false;
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean show = endpointsTree.getSelectedRowCount() > 0;
                if (show == showing)
                    return;
                if (show) {
                    selectionLabel.setText("<html><a href=\"\">Select all</a></html>");
                    selectionLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    selectionLabel.addMouseListener(mouseListener);
                } else {
                    selectionLabel.setText(null);
                    selectionLabel.setCursor(Cursor.getDefaultCursor());
                    selectionLabel.removeMouseListener(mouseListener);
                }
                showing = show;
            }
        });

        TableColumnModel columnModel = endpointsTree.getColumnModel();
        int n = columnModel.getColumnCount();
        for (int col = 1; col < n; col++) {
            TableColumn column = columnModel.getColumn(col);
            column.setCellRenderer(OPERATION_RENDERER);
            column.setCellEditor(OPERATION_EDITOR);
        }
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static EndpointNode createEndpointTreeModel(@NotNull WebApi webApi) {
        var top = new EndpointNode("", null);
        List<EndPoint> endPoints = new ArrayList<>(webApi.endPoints());
        endPoints.sort(Comparator.comparing(ep -> ep.path().value()));
        fillTree(endPoints, "", top);
        return top;
    }

    @Nullable
    private WebApi getWebApi(VirtualFile vf) {
        PsiFile descriptorPsiFile = PsiManager.getInstance(project).findFile(vf);
        Document webApiDocument = RestSdkHelper.parseWebApi(descriptorPsiFile);
        if (webApiDocument == null)
            return null;
        return (WebApi) webApiDocument.encodes();
    }

    private static void fillTree(List<EndPoint> endPoints, String prefix, EndpointNode node) {
        Map<String, List<EndPoint>> m = endPoints.stream().collect(Collectors.groupingBy(ep -> {
            String path = ep.path().value();
            int start = prefix.length() + 1;
            int end = path.indexOf('/', start);
            if (end == -1)
                end = path.length();
            if (start>=path.length()) {
                node.setUserObject(ep);
                return "";
            }
            return path.substring(start, end);
        }));
        if (m.size() == 1) {
            String k = m.keySet().iterator().next();
            if (!k.isBlank()) {
                node.setUserObject(node.getUserObject() + "/" + k);
                fillTree(m.values().iterator().next(), prefix + "/" + k, node);
            }
            return;
        }
        for (Map.Entry<String, List<EndPoint>> e : m.entrySet()) {
            String next = e.getKey();
            List<EndPoint> eps = e.getValue();
            if (next.isBlank())
                continue;
            var child = new EndpointNode(next, null);
            node.add(child);
            fillTree(eps, prefix + "/" + next, child);
        }
    }

    @Override
    protected void doOKAction() {
        if (!isOKActionEnabled())
            return;

        List<Object> nodes = new ArrayList<>();
        collectChildren(treeTableModel.getRoot(), nodes);
        WriteCommandAction.writeCommandAction(project)
                .withName("Generate Endpoint Operations")
                .run(() -> {
                    for (Object node : nodes) {
                        var epn = (EndpointNode) node;
                        EndPoint endpoint = epn.getEndpoint();
                        if (endpoint == null)
                            continue;
                        for (HTTPMethod method : epn.getSelectedMethods()) {
                            for (Operation operation : endpoint.operations()) {
                                if (operation.method().value().equalsIgnoreCase(method.name()))
                                    generate(endpoint, operation);
                            }
                        }
                    }
                });

        super.doOKAction();
    }

    private void generate(EndPoint endpoint, Operation operation) {
        PsiElement psiFile = PsiManager.getInstance(project).findFile(descriptorFile);
        assert psiFile != null;
        var existingEndpoints = tryCast(ENDPOINTS_PATH.selectYaml(psiFile), YAMLMapping.class);

        String templateString = RestSdkCompletionService.createEndpointOperationTemplate(endpoint, operation);
        final YAMLElementGenerator elementGenerator = YAMLElementGenerator.getInstance(project);
        var dummyFile = elementGenerator.createDummyYamlWithText(templateString);
        YAMLMapping templateEndpointMapping = (YAMLMapping) dummyFile.getFirstChild().getFirstChild();
        if (templateEndpointMapping == null) {
            Logger.getInstance(RestSdkCompletionService.class).error("Couldn't create YAML from: " + templateString);
            return;
        }
        mergeInto(existingEndpoints, templateEndpointMapping);
    }

    private void collectChildren(Object node, List<Object> nodes) {
        nodes.add(node);
        int childCount = treeTableModel.getChildCount(node);
        for (int i = 0; i < childCount; i++) {
            Object child = treeTableModel.getChild(node, i);
            collectChildren(child, nodes);
        }
    }
}
