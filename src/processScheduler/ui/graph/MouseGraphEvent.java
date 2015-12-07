package processScheduler.ui.graph;

import processScheduler.model.Vertex;

public class MouseGraphEvent {
    public final int type;
    public final Vertex vertex;
    public MouseGraphEvent(int type, Vertex v){
        this.type = type;
        vertex = v;
    }
}
