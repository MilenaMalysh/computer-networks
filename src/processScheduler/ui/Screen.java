package processScheduler.ui;


import com.google.common.eventbus.EventBus;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import processScheduler.logic.Message;
import processScheduler.ui.Blocks.FromOneToAnother;
import processScheduler.ui.Blocks.TestNet;
import processScheduler.ui.Blocks.TransNet;
import processScheduler.ui.graph.GraphAdapter;
import processScheduler.ui.graph.layout.NetworkLayout;
import processScheduler.ui.graph.layout.RandomLayout;

/**
 * Created by Milena on 16.11.2015.
 */
public class Screen {

    private Menu menu1, menu2, menu3;
    private Separator separatorVer = new Separator();
    private Separator separatorHor1 = new Separator();
    private Separator separatorHor2 = new Separator();
    private GraphAdapter graphAdapter;
    private final TransNet transnet;


    public Screen(Stage stage, GraphAdapter graphAdapter) {
        stage.show();

        this.graphAdapter = graphAdapter;

        //--------------Create Main Screen--------------//
        HBox hBox = new HBox(2);

        //Scene scene = new Scene(vBox, 802, 602);
        Scene scene = new Scene(hBox, 1302, 652);
        stage.setScene(scene);

        VBox vBox1 = new VBox(2);
        VBox vBox2 = new VBox(2);

        set_visual_part();

        TestNet testnet = new TestNet();
        vBox1.getChildren().addAll(graphAdapter.getScrollPane(), separatorHor1, testnet);

        FromOneToAnother shortdist = new FromOneToAnother(graphAdapter.getModel());
        transnet = new TransNet();
        vBox2.getChildren().addAll(shortdist, separatorHor2, transnet);
        VBox.setVgrow(graphAdapter.getScrollPane(), Priority.ALWAYS);

        hBox.getChildren().addAll(vBox1, separatorVer, vBox2);
        HBox.setHgrow(vBox1, Priority.ALWAYS);
        //--------------Set Scene-----------------------//
        //menuBar.prefWidthProperty().bind(stage.widthProperty());
        stage.setScene(scene);

        NetworkLayout networkLayout = new NetworkLayout(graphAdapter);
        networkLayout.execute();
    }

    private void set_visual_part() {
        separatorVer.setMinWidth(2);
        separatorVer.setMaxWidth(2);
        separatorVer.setPrefWidth(2);
        separatorVer.setOrientation(Orientation.VERTICAL);

        separatorHor1.setMinHeight(2);
        separatorHor1.setMaxHeight(2);
        separatorHor1.setPrefHeight(2);
        separatorHor1.setOrientation(Orientation.HORIZONTAL);

        separatorHor2.setMinHeight(2);
        separatorHor2.setMaxHeight(2);
        separatorHor2.setPrefHeight(2);
        separatorHor2.setOrientation(Orientation.HORIZONTAL);
    }

    public void bindLabels(IntegerProperty dataPkgs, IntegerProperty sysPkgs, IntegerProperty systemTime, IntegerProperty averageTime, IntegerProperty informationalData,
                           IntegerProperty systemData,
                           ObservableList<Message> sentMessages,
                           ObservableList<Message> receivedMessages) {
        transnet.bindLabels(dataPkgs, sysPkgs, systemTime, averageTime, informationalData,
                systemData,
                sentMessages,
                receivedMessages);
    }
}