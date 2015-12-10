package processScheduler.ui;

import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import processScheduler.logic.Strategy;
import processScheduler.model.*;
import processScheduler.ui.graph.GraphAdapter;

import java.util.Arrays;
import java.util.Random;

public class Main extends Application {
    private ObservableList<Process> processed;
    protected int VERTEXES_AMOUNT = 30;
    private Screen root;
    protected Graph graph;
    private GraphAdapter adapter;
    private Strategy strategy;
    private static final int[] weights = {1, 3, 4, 5, 7, 10, 12, 14, 15, 18, 21, 22, 25};


    @Override
    public void start(Stage primaryStage) throws Exception {
        graph = new Graph();
        adapter = new GraphAdapter(graph);
        addGraphComponents();

        primaryStage.setTitle("Network");
        root = new Screen(primaryStage, adapter);

        primaryStage.show();
        strategy = new Strategy(graph);
        strategy.registerPlayObserver(this);
    }

    public void addGraphComponents() {



        for (int i = 1; i <= 30; i++) {
            graph.addVertex(new Vertex(i));
        }
        Random r = new Random();
        for (int i = 1; i <= 10; i++) {
            int j = i + 1;
            if (j > 10)
                j = j % 10;
            graph.addEdge(graph.generateEdge(i, j, weights[r.nextInt(weights.length)], r.nextBoolean() ? DuplexChannel.class : HalfDuplexChannel.class));
        }
        for (int i = 11; i <= 20; i++) {
            int j = i + 1;
            if (j > 20)
                j = 10 + j % 20;
            graph.addEdge(graph.generateEdge(i, j, weights[r.nextInt(weights.length - 1)], r.nextBoolean() ? DuplexChannel.class : HalfDuplexChannel.class));
        }

        for (int i = 21; i <= 30; i++) {
            int j = i + 1;
            if (j > 30)
                j = 20 + j % 30;
            graph.addEdge(graph.generateEdge(i, j, weights[r.nextInt(weights.length - 1)], r.nextBoolean() ? DuplexChannel.class : HalfDuplexChannel.class));
        }

        graph.addEdge(graph.generateEdge(1 + r.nextInt(10), 11 + r.nextInt(10), 3 * weights[r.nextInt(weights.length)], r.nextBoolean() ? DuplexChannel.class : HalfDuplexChannel.class));
        graph.addEdge(graph.generateEdge(1 + r.nextInt(10), 21 + r.nextInt(10),  weights[r.nextInt(weights.length)], r.nextBoolean() ? DuplexChannel.class : HalfDuplexChannel.class));
        graph.addEdge(graph.generateEdge(11 + r.nextInt(10), 21 + r.nextInt(10),  weights[r.nextInt(weights.length)], r.nextBoolean() ? DuplexChannel.class : HalfDuplexChannel.class));
        while ((float) graph.getEdges().size() / graph.getVertexeses().size() < 3.0) {
            Vertex vertex1;
            Vertex vertex2;
            Channel e;
            int start;
            switch (r.nextInt(3)){
                case 0:
                    start = 1;
                    break;
                case 1:
                    start = 11;
                    break;
                case 2:
                    start = 21;
                    break;
                default:
                    start = 1;
                    break;
            }
            do {
                vertex1 = graph.findVertex(start + r.nextInt(10));
                vertex2 = graph.findVertex(start + r.nextInt(10));
                e = graph.findChannel(vertex1, vertex2);
            } while (vertex1.equals(vertex2) || e != null);
            graph.addEdge(graph.generateEdge(vertex1.getId(), vertex2.getId(), weights[r.nextInt(weights.length - 1)], r.nextBoolean() ? DuplexChannel.class : HalfDuplexChannel.class));

        }
        System.out.println(String.format("Avg. edges = %f", (float) graph.getEdges().size() / graph.getVertexeses().size()));



        //LOCKAL NETWORK1

/*        graph.addEdge(graph.generateEdge(1, 8, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(1, 6, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(1, 5, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(2, 7, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(2, 4, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(2, 6, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(3, 4, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(3, 7, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(3, 9, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(3, 10, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(4, 10, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(4, 9, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(5, 8, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(5, 10, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(5, 9, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(6, 7, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(6, 8, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(8, 7, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(10, 9, 0, DuplexChannel.class));
*/







        //LOKAL NETWORK2

/*        graph.addEdge(graph.generateEdge(11, 18, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(11, 16, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(11, 15, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(12, 17, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(12, 14, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(12, 16, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(13, 14, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(13, 17, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(13, 19, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(13, 20, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(14, 20, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(14, 19, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(15, 18, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(15, 20, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(15, 19, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(16, 17, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(16, 18, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(18, 17, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(20, 19, 0, DuplexChannel.class));








        //LOCKAL NETWORK3


        graph.addEdge(graph.generateEdge(21, 28, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(21, 26, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(21, 25, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(22, 27, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(22, 24, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(22, 26, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(23, 24, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(23, 27, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(23, 29, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(23, 30, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(24, 30, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(24, 29, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(25, 28, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(25, 30, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(25, 29, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(26, 27, 0, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(26, 28, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(28, 27, 0, DuplexChannel.class));

        graph.addEdge(graph.generateEdge(30, 29, 0, DuplexChannel.class));



        //BINDING NETWORKS

        graph.addEdge(graph.generateEdge(1, 12, -1, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(2, 21, -1, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(11, 22, -1, DuplexChannel.class));
*/

/*
        graph.addEdge(graph.generateEdge(1, 2, 5, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(1, 3, 1, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(2, 3, 3, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(3, 4, 4, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(2, 5, 6, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(4, 6, 7, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(5, 7, 2, DuplexChannel.class));*/
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Subscribe
    public void onPlayStarted(Strategy.PlaybackMode mode) {
        root.bindLabels(strategy.dataPackagesProperty(),
                strategy.sysPackagesProperty(),
                strategy.systemTimeProperty(),
                strategy.averageTimeProperty());
    }
}
