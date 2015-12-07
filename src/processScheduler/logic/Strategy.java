package processScheduler.logic;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import processScheduler.model.Graph;
import processScheduler.model.Vertex;
import processScheduler.ui.Blocks.FromOneToAnother;
import processScheduler.ui.Blocks.TestNet;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Milena on 02.12.2015.
 */
public class Strategy {

    private TreeMap<Vertex, RouterModel> nodes;
    private Graph graph;
    public static EventBus eventBus1 = new EventBus();
    public static EventBus eventBus2 = new EventBus();
    private AbstractMode mode;
    private AbstractBuilder builder;
    private Timeline timeline;
    private final int TICK_DURATION = 100;
    private boolean stopping;
    private static int number_strategy = 0;
    private final int TICKSFORMESSAGE = 100;
    private int counter;

    public Strategy(Graph graph) {
        eventBus1.register(this);
        this.nodes = new TreeMap<>();
        this.graph = graph;
        this.stopping = false;
        //this.ticksformessage = 3;
        this.counter = 1;
        //update_configuration();
        //number_strategy++;
    }

    public <T extends AbstractMode> void play(T mode) {
        update_configuration();
        AbstractMode newMode = null;
        timeline = new Timeline(new KeyFrame(Duration.millis(TICK_DURATION), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //когда придет false - распределение закончилось
                if (!stopping) {
                    if (counter == TICKSFORMESSAGE) {
                        mode.sendMessage();
                        counter = 1;
                    } else {
                        counter++;
                    }
                    mode.tick();
                    System.out.println("+++");
                    //меняем время и показываем его
                    //systemTime++;
                    //перезапускаем таймер
                    timeline.stop();
                    timeline.setDelay(Duration.millis(TICK_DURATION));
                    timeline.play();
                } else System.out.println("Finished ");
            }
        }));
        timeline.play();
    }

    public void update_configuration() {
        nodes.clear();
        for (Map.Entry<Integer, Vertex> i : graph.getVertexes().entrySet()) {
            nodes.put(i.getValue(), new RouterModel(graph, i.getValue()));
        }
    }

    public TreeMap<Vertex, RouterModel> getNodes() {
        return nodes;
    }


    @Subscribe
    public void onPlayButton(TestNet.PlayEvent event) {
        counter = 100;
        stopping = false;
        if(mode!=null)
            mode.cancel();
        builder = new RandomBuilder(graph);
        if (TestNet.PlayEvent.mode == 0) {
            mode = new DatagramMode(nodes, graph, builder);
            play(mode);
        } else {
            mode = new VirtualChannelMode(nodes, graph, builder);
            play(mode);
        }

    }

    @Subscribe
    public void onStopButton(TestNet.StopEvent event) {
        System.out.println("Stop");
        //timeline.stop();
        stopping = true;
    }

    @Subscribe
    public void onSendOneMessage(FromOneToAnother.PlayEvent event) {
        counter = 100;
        stopping = false;
        if(mode!=null)
            mode.cancel();
        builder = new FromOneToAnotherBuilder(graph, event.from, event.to, event.msgSize);
        if (event.mode == 0) {
            mode = new DatagramMode(nodes, graph, builder);
            play(mode);
        } else {
            mode = new VirtualChannelMode(nodes, graph, builder);
            play(mode);
        }

    }
}
