package processScheduler.ui.graph.vertex;

import javafx.beans.property.IntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public abstract class VertexView extends Pane {

    int vertexId;
    protected Node view;
    protected IntegerProperty status;

    public VertexView(int vertexId, IntegerProperty status) {
        this.vertexId = vertexId;
        this.status = status;
        this.view = createView();
        getChildren().add(view);

    }

    public Node getView() {
        return this.view;
    }

    public int getVertexId() {
        return vertexId;
    }

    protected abstract Node createView();
}