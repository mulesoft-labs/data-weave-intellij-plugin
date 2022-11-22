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

    private final EnumSet<HTTPMethod> alreadyImplementedMethods = EnumSet.noneOf(HTTPMethod.class);

    EndpointNode(@NotNull String path, @Nullable EndPoint endPoint) {
        super(endPoint);
        this.path = path;
    }

    public EndPoint getEndpoint() {
        return (EndPoint) getUserObject();
    }

    @Override
    public String toString() {
        return path;
    }

    public Set<HTTPMethod> getSelectedMethods() {
        return selectedMethods;
    }

    public EnumSet<HTTPMethod> getAlreadyImplementedMethods() {
        return alreadyImplementedMethods;
    }

    public boolean alreadyImplemented(HTTPMethod method) {
        return alreadyImplementedMethods.contains(method);
    }


    boolean selectedForGeneration(HTTPMethod method) {
        return selectedMethods.contains(method);
    }
}
