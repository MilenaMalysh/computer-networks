package processScheduler.ui.graph.layout;



import processScheduler.model.Vertex;
import processScheduler.ui.graph.GraphAdapter;
import processScheduler.ui.graph.vertex.VertexView;

import java.util.Map;
import java.util.Random;
import java.util.Set;


public class NetworkLayout extends Layout {

    GraphAdapter graph;

    Random rnd = new Random();

    public NetworkLayout(GraphAdapter graph) {

        this.graph = graph;

    }

    public void execute() {
        double radius = 150;
        double step = 2*Math.PI/10;
        double centerX;
        double centerY;
        double x;
        double y;
        for (Map.Entry<Vertex, VertexView> vertex : graph.getAllVertexes()) {
            int i = vertex.getKey().getId();
            if(1<=i&&i<=10){
                centerX = 170;
                centerY = 160;
            }else if(10<i&&i<=20){
                centerX = 640;
                centerY = 160;
            }else{
                centerX = 400;
                centerY = 400;
            }
            x = radius*Math.cos((i-1)%10*step) + centerX;
            y = radius*Math.sin((i-1)%10*step) + centerY;
            vertex.getValue().relocate(x, y);
        }
    }
}