package processScheduler.ui.Blocks;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import processScheduler.logic.Strategy;


/**
 * Created by Milena on 16.11.2015.
 */
public class TestNet extends BorderPane {

    private BorderPane header;
    private HBox vboxmain;
    private ToggleGroup radiogroup;
    private Button playButton;
    private Button stopButton;
    private static RadioButton rb1;
    private RadioButton rb2;

    public TestNet() {
        //------------Header--------------------------//
        header = new BorderPane();

        Label label = new Label("Testing network");
        label.setFont(new Font("Times New Roman", 16));


        //---------------Buttons---------------------//
        playButton =  new Button();
        stopButton = new Button();

        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Strategy strategy = new Strategy();
                playButton.setDisable(false);
                Strategy.eventBus1.post(new StopEvent());
            }
        });


        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PlayEvent ev =new PlayEvent(rb1.isSelected() ? PlayEvent.DATAGRAM : PlayEvent.VIRTUAL);
                //eventBus.post(ev);
                playButton.setDisable(true);
               Strategy.eventBus1.post(ev);
            }
        });


        HBox btns = new HBox(2);
        btns.getChildren().addAll(playButton,stopButton);

        header.setRight(btns);
        header.setLeft(label);
        //-------------Body----------------------//

        vboxmain = new HBox(2);
        radiogroup = new ToggleGroup();

        rb1 = new RadioButton("Datagram mode");
        rb1.setToggleGroup(radiogroup);
        rb1.setSelected(true);

        rb2 = new RadioButton("Virtual channel mode");
        rb2.setToggleGroup(radiogroup);

        vboxmain.getChildren().addAll(rb1, rb2);

        //-------------Resume-------------------//
        set_visual_part();
        this.setTop(header);
        this.setCenter(vboxmain);
        this.setMargin(label, new javafx.geometry.Insets(0, 10, 10, 10));

    }
    private void set_visual_part(){
        this.setMinSize(500, 200);
        this.setMaxSize(500, 200);
        this.setPrefSize(500, 200);

        Image imagePlay = new Image(getClass().getResourceAsStream("Images/play1.png"));
        this.playButton.setGraphic(new ImageView(imagePlay));
        this.playButton.prefHeight(15);
        this.playButton.minHeight(15);
        this.playButton.maxHeight(15);

        Image imageStop = new Image(getClass().getResourceAsStream("Images/stop1.png"));
        this.stopButton.setGraphic(new ImageView(imageStop));
        this.stopButton.prefHeight(15);
        this.stopButton.minHeight(15);
        this.stopButton.maxHeight(15);
    }

    public static class PlayEvent{
        public static final int DATAGRAM = 0;
        public static final int VIRTUAL = 1;
        public static int mode;
        public  PlayEvent(int mode){
            this.mode = mode;
        }
    }

    public static class StopEvent{
        public StopEvent(){
        }
    }

    public static RadioButton getRb1() {
        return rb1;
    }
}
