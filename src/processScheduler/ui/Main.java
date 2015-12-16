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
        graph.addEdge(graph.generateEdge(1 + r.nextInt(10), 21 + r.nextInt(10), 3*weights[r.nextInt(weights.length)], r.nextBoolean() ? DuplexChannel.class : HalfDuplexChannel.class));
        graph.addEdge(graph.generateEdge(11 + r.nextInt(10), 21 + r.nextInt(10), 3*weights[r.nextInt(weights.length)], r.nextBoolean() ? DuplexChannel.class : HalfDuplexChannel.class));
        while ((float) 2*graph.getEdges().size() / graph.getVertexeses().size() < 3.0) {
            Vertex vertex1;
            Vertex vertex2;
            Channel e;
            int start;
            switch (r.nextInt(3)) {
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
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Subscribe
    public void onPlayStarted(Strategy.PlaybackMode mode) {
        root.bindLabels(strategy.dataPackagesProperty(),
                strategy.sysPackagesProperty(),
                strategy.systemTimeProperty(),
                strategy.averageTimeProperty(),
                strategy.informationalDataProperty(),
                strategy.systemDataProperty(),
                strategy.getSentMessages(),
                strategy.getDeliveredMessages());
    }
}
