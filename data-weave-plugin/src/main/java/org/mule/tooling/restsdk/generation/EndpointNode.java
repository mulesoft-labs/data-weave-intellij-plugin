package org.mule.tooling.restsdk.generation;

import amf.apicontract.client.platform.model.domain.EndPoint;
import com.intellij.util.net.HTTPMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.EnumSet;
import java.util.Set;

class EndpointNode extends DefaultMutableTreeNode {
    private String lastPathComponent;
    private final EnumSet<HTTPMethod> selectedMethods = EnumSet.noneOf(HTTPMethod.class);

    private final EnumSet<HTTPMethod> alreadyImplementedMethods = EnumSet.noneOf(HTTPMethod.class);

    EndpointNode(@NotNull String lastPathComponent, @Nullable EndPoint endPoint) {
        super(endPoint);
        this.lastPathComponent = lastPathComponent;
    }

    public EndPoint getEndpoint() {
        return (EndPoint) getUserObject();
    }

    @Override
    public void setUserObject(Object userObject) {
        if (userObject != null && !(userObject instanceof EndPoint))
            throw new IllegalArgumentException("userObject: " + userObject);
        super.setUserObject(userObject);
    }

    @Override
    public String toString() {
        return lastPathComponent;
    }

    public String getLastPathComponent() {
        return lastPathComponent;
    }

    public void setLastPathComponent(String lastPathComponent) {
        this.lastPathComponent = lastPathComponent;
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
