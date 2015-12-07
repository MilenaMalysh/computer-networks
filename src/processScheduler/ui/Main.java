package processScheduler.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import processScheduler.logic.io.network.Strategy;
import processScheduler.model.DuplexChannel;
import processScheduler.model.Graph;
import processScheduler.model.Vertex;
import processScheduler.ui.Blocks.FromOneToAnother;
import processScheduler.ui.Blocks.TestNet;
import processScheduler.ui.graph.GraphAdapter;

public class Main extends Application {
    private ObservableList<Process> processed;
    protected int VERTEXES_AMOUNT = 7;
    private Screen root;
    protected Graph graph;


    EventBus eventBus;
    private GraphAdapter adapter;
    private Strategy strategy;
    private EventBus bus1;
    private EventBus bus2;


    @Override
    public void start(Stage primaryStage) throws Exception {
        graph = new Graph();
        adapter = new GraphAdapter(graph);
        addGraphComponents();

        primaryStage.setTitle("Network");
        //bus1 = new EventBus();
        //bus1.register(this);
        //bus2 = new EventBus();
        //bus2.register(this);


        //root = new Screen(primaryStage, adapter, bus1, bus2);
        root = new Screen(primaryStage, adapter);

        primaryStage.show();
        Strategy strategy = new Strategy(graph);
    }

    public void addGraphComponents() {

        for (int i = 1; i <= VERTEXES_AMOUNT; i++) {
            graph.addVertex(new Vertex(i));
        }

        graph.addEdge(graph.generateEdge(1, 2, 5, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(1, 3, 1, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(2, 3, 3, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(3, 4, 4, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(2, 5, 6, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(4, 6, 7, DuplexChannel.class));
        graph.addEdge(graph.generateEdge(5, 7, 2, DuplexChannel.class));
    }

    public static void main(String[] args) {
        launch(args);
    }

//    @Subscribe
//    public void onFromOneToOther(FromOneToAnother.Event event){
//        Strategy strategy = new Strategy(graph);
//    }

//    @Subscribe
//    public Strategy onTestNet(TestNet.PlayEvent event){
//        Strategy strategy = new Strategy(graph);
//        return strategy;
//    }
}
