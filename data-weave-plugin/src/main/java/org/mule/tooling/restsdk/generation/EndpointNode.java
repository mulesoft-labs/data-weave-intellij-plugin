package org.mule.tooling.restsdk.generation;

import amf.apicontract.client.platform.model.domain.EndPoint;
import com.intellij.util.net.HTTPMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.EnumSet;
import java.util.Set;

class EndpointNode extends DefaultMutableTreeNode {
    private final String path;
    private final EnumSet<HTTPMethod> selectedMethods = EnumSet.noneOf(HTTPMethod.class);

    EndpointNode(@NotNull String path, @Nullable EndPoint endPoint) {
        super(endPoint);
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

    public Set<HTTPMethod> getSelectedMethods() {
        return selectedMethods;
    }

    public EndPoint getEndpoint() {
        return (EndPoint) getUserObject();
    }
}
