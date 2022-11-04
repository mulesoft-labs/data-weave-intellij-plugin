package org.mule.tooling.restsdk.generation;

import amf.apicontract.client.platform.model.domain.EndPoint;
import amf.apicontract.client.platform.model.domain.Operation;
import com.intellij.util.net.HTTPMethod;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.TableCellRenderer;
import java.util.Locale;
import java.util.Set;

class MethodColumnInfo extends ColumnInfo<Object, Boolean> {
    private final HTTPMethod method;

    public MethodColumnInfo(String method) {
        super(method.toUpperCase(Locale.ROOT));
        this.method = HTTPMethod.valueOf(getName());
    }

    @Nullable
    @Override
    public Boolean valueOf(Object o) {
        if (!(o instanceof EndpointNode))
            return null;
        EndpointNode epn = (EndpointNode) o;
        EndPoint ep = epn.getEndpoint();
        if (ep != null) {
            for (Operation operation : ep.operations()) {
                if (operation.method().value().equalsIgnoreCase(method.name()))
                    return epn.getSelectedMethods().contains(method);
            }
        }
        return null;
    }

    @Override
    public @Nullable TableCellRenderer getRenderer(Object item) {
        return BulkEndpointOperationsGeneratorDialog.OPERATION_RENDERER;
    }

    @Override
    public boolean isCellEditable(Object o) {
        return valueOf(o) != null;
    }

    @Override
    public @Nullable String getTooltipText() {
        return super.getTooltipText();
    }

    @Override
    public Class<?> getColumnClass() {
        return Boolean.class;
    }

    @Override
    public void setValue(Object o, Boolean value) {
        if (value == null)
            return;
        Set<HTTPMethod> s = ((EndpointNode) o).getSelectedMethods();
        if (value)
            s.add(method);
        else
            s.remove(method);
    }
}
