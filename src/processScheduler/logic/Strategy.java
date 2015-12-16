package processScheduler.logic;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import processScheduler.model.Graph;
import processScheduler.ui.Blocks.FromOneToAnother;
import processScheduler.ui.Blocks.TestNet;

import java.util.OptionalDouble;

/**
 * Created by Milena on 02.12.2015.
 */
public class Strategy {

    private Graph graph;
    public static EventBus eventBus = new EventBus();
    private AbstractMode mode;
    private AbstractBuilder builder;
    private Timeline timeline;
    private static final int TICK_DURATION = 10;
    public static int TICKSFORMESSAGE =1000;
    private int counter;
    private PlaybackMode playbackMode;
    private ObservableList<Message> sentMessages;
    private ObservableList<Message> deliveredMessages;
    private IntegerProperty dataPackages;
    private IntegerProperty sysPackages;
    private IntegerProperty informationalData;
    private IntegerProperty systemData;
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
        informationalData = new SimpleIntegerProperty(0);
        systemData = new SimpleIntegerProperty(0);
        systemTime = new SimpleIntegerProperty(0);
        averageTime = new SimpleIntegerProperty(0);
        IntegerBinding binding = new IntegerBinding() {
            {
                super.bind(deliveredMessages);
            }

            @Override
            protected int computeValue() {
                OptionalDouble average = deliveredMessages.stream().mapToInt(Message::getDeliveryTime).average();
                return average.isPresent()? (int) average.getAsDouble() :0;
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
        counter = TICKSFORMESSAGE;
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
        systemData.setValue(0);
        informationalData.setValue(0);
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
    }

    @Subscribe
    public void onMessageEvent(Message msg) {
        if (msg.isTransmitted()) {
            msg.setDeliveryTime(systemTime.get() - msg.getStart());
            deliveredMessages.add(msg);
            System.out.println(String.format("%s delivered at %d", msg, systemTime.get()));
        }else System.out.println(String.format("%s sent from %s to %s at %d", msg, msg.getSource(), msg.getTarget(),systemTime.get()));
    }

    @Subscribe
    public void onPackageDelivered(Package pkg){
        if(pkg instanceof SysPackage){
            sysPackages.setValue(sysPackages.get()+1);
            systemData.setValue(systemData.get()+pkg.getSize());
            if(((SysPackage) pkg).mode!= SysPackage.Mode.NOTIFY)
            System.out.println(String.format("%s delivered at %d", pkg, systemTime.get()));
        }else{
            dataPackages.setValue(dataPackages.get()+1);
            informationalData.setValue(informationalData.get()+pkg.getSize());
            System.out.println(String.format("%s delivered at %d", pkg, systemTime.get()));
        }
    }

    public int getInformationalData() {
        return informationalData.get();
    }

    public IntegerProperty informationalDataProperty() {
        return informationalData;
    }

    public void setInformationalData(int informationalData) {
        this.informationalData.set(informationalData);
    }

    public int getSystemData() {
        return systemData.get();
    }

    public IntegerProperty systemDataProperty() {
        return systemData;
    }

    public void setSystemData(int systemData) {
        this.systemData.set(systemData);
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

    public ObservableList<Message> getSentMessages() {
        return sentMessages;
    }

    public ObservableList<Message> getDeliveredMessages() {
        return deliveredMessages;
    }
}
