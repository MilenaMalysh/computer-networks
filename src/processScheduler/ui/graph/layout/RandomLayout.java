package processScheduler.ui.graph.layout;



import processScheduler.model.Vertex;
import processScheduler.ui.graph.GraphAdapter;
import processScheduler.ui.graph.vertex.VertexView;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class RandomLayout extends Layout {

    GraphAdapter graph;

    Random rnd = new Random();

    public RandomLayout(GraphAdapter graph) {

        this.graph = graph;

    }

    public void execute() {
        Set<Map.Entry<Vertex, VertexView>> vertexes = graph.getAllVertexes();
        for (Map.Entry<Vertex, VertexView> vertex : vertexes) {
            double x = rnd.nextDouble() * 900;
            double y = rnd.nextDouble() * 500;
            vertex.getValue().relocate(x, y);
        }
    }
}