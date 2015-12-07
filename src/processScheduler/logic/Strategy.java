package processScheduler.logic;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import processScheduler.model.Graph;
import processScheduler.ui.Blocks.FromOneToAnother;
import processScheduler.ui.Blocks.TestNet;

/**
 * Created by Milena on 02.12.2015.
 */
public class Strategy {

    private Graph graph;
    public static EventBus eventBus = new EventBus();
    private AbstractMode mode;
    private AbstractBuilder builder;
    private Timeline timeline;
    private final int TICK_DURATION = 100;
    private final int TICKSFORMESSAGE = 100;
    private int counter;
    private PlaybackMode playbackMode;
    private ObservableList<Message> sentMessages;
    private ObservableList<Message> deliveredMessages;
    private IntegerProperty dataPackages;
    private IntegerProperty sysPackages;
    protected IntegerProperty averageTime;
    protected IntegerProperty systemTime;

    public Strategy(Graph graph) {
        eventBus.register(this);
        this.graph = graph;
        timeline = new Timeline(new KeyFrame(Duration.millis(TICK_DURATION), event -> {
            step();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        this.counter = 1;
        sentMessages = FXCollections.<Message>observableArrayList();
        deliveredMessages = FXCollections.<Message>observableArrayList();
        dataPackages = new SimpleIntegerProperty(0);
        sysPackages = new SimpleIntegerProperty(0);
        systemTime = new SimpleIntegerProperty(0);
        averageTime = new SimpleIntegerProperty(0);
        IntegerBinding binding = new IntegerBinding() {
            {
                super.bind(deliveredMessages);
            }

            @Override
            protected int computeValue() {
                return deliveredMessages.isEmpty()?0:deliveredMessages.stream().mapToInt(Message::getDeliveryTime).sum();
            }

            @Override
            public ObservableList getDependencies() {
                return FXCollections.singletonObservableList(deliveredMessages);
            }

            @Override
            public void dispose() {
                super.unbind(deliveredMessages);
            }
        };
        averageTime.bind(binding);
    }

    public void play() {
        eventBus.post(playbackMode);
        timeline.play();
    }

    @Subscribe
    public void onPlayButton(TestNet.PlayEvent event) {
        playbackMode = PlaybackMode.REPEATING;
        cancel();
        counter = 100;
        if (mode != null)
            mode.cancel();
        builder = new RandomBuilder(graph);
        if (TestNet.PlayEvent.mode == 0) {
            mode = new DatagramMode(graph, builder);
        } else {
            mode = new VirtualChannelMode(graph, builder);
        }
        mode.registerObserver(eventBus);
        play();
    }

    @Subscribe
    public void onStop(TestNet.StopEvent event) {
        System.out.println("Stop");
        timeline.stop();
    }

    @Subscribe
    public void onSendOneMessage(FromOneToAnother.PlayEvent event) {
        playbackMode = PlaybackMode.ONCE;
        cancel();
        if (mode != null)
            mode.cancel();
        builder = new FromOneToAnotherBuilder(graph, event.from, event.to, event.msgSize);
        if (event.mode == 0) {
            mode = new DatagramMode(graph, builder);
        } else {
            mode = new VirtualChannelMode(graph, builder);
        }
        mode.registerObserver(eventBus);
        play();
    }

    public void cancel() {
        dataPackages.setValue(0);
        sysPackages.setValue(0);
        sentMessages.clear();
        deliveredMessages.clear();
        systemTime.setValue(0);
        timeline.stop();
    }

    public void step() {
        if ((playbackMode == PlaybackMode.REPEATING && counter == TICKSFORMESSAGE) ||
                (playbackMode == PlaybackMode.ONCE && sentMessages.isEmpty() && deliveredMessages.isEmpty())) {
            Message msg = mode.sendMessage();
            if (msg != null) {
                msg.setStart(systemTime.get());
                sentMessages.add(msg);
            }
            counter = 1;
        } else {
            if (playbackMode == PlaybackMode.ONCE && !deliveredMessages.isEmpty()) {
                timeline.stop();
            }
            counter++;
        }
        mode.tick();
        systemTime.setValue(systemTime.get() + 1);
        System.out.println("+++");
    }

    @Subscribe
    public void onMessageDelivered(Message msg) {
        sentMessages.remove(msg);
        msg.setDeliveryTime(systemTime.get() - msg.getStart());
        deliveredMessages.add(msg);
    }

    @Subscribe
    public void onPackageDelivered(Package pkg){
        if(pkg instanceof SysPackage){
            sysPackages.setValue(sysPackages.get()+1);
        }else{
            dataPackages.setValue(dataPackages.get()+1);
        }
    }

    public enum PlaybackMode {
        REPEATING, ONCE
    }

    public void registerPlayObserver(Object o) {
        eventBus.register(o);
    }

    public IntegerProperty dataPackagesProperty() {
        return dataPackages;
    }

    public IntegerProperty sysPackagesProperty() {
        return sysPackages;
    }

    public IntegerProperty systemTimeProperty() {
        return systemTime;
    }

    public IntegerProperty averageTimeProperty() {
        return averageTime;
    }

}
