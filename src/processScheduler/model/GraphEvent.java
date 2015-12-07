package processScheduler.model;

public class GraphEvent<T> {
    public static final int REMOVED = 0;
    public static final int ADDED = 1;

    private T target;
    private int event;

    public GraphEvent(T target, int event){
        this.event = event;
        this.target = target;
    }

    public int getEvent() {
        return event;
    }

    public T getTarget() {
        return target;
    }

    static public class Vertex extends GraphEvent<processScheduler.model.Vertex>{

        public Vertex(processScheduler.model.Vertex target, int event) {
            super(target, event);
        }
    }

    static public class Edge extends GraphEvent<Channel>{

        public Edge(Channel target, int event) {
            super(target, event);
        }
    }
}
