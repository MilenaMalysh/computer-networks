package processScheduler.ui;

import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import processScheduler.logic.Strategy;
import processScheduler.model.DuplexChannel;
import processScheduler.model.Graph;
import processScheduler.model.Vertex;
import processScheduler.ui.graph.GraphAdapter;

public class Main extends Application {
    private ObservableList<Process> processed;
    protected int VERTEXES_AMOUNT = 4;
    private Screen root;
    protected Graph graph;
    private GraphAdapter adapter;
    private Strategy strategy;


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

        for (int i = 1; i <= VERTEXES_AMOUNT; i++) {
            graph.addVertex(new Vertex(i));
        }
        graph.addEdge(graph.generateEdge(1, 2, 5, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(1, 3, 4, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(2, 4, 5, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(3, 4, 5, DuplexChannel.class));

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
