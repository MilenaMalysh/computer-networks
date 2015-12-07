package processScheduler.ui;


import com.google.common.eventbus.EventBus;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import processScheduler.ui.Blocks.FromOneToAnother;
import processScheduler.ui.Blocks.TestNet;
import processScheduler.ui.Blocks.TransNet;
import processScheduler.ui.graph.GraphAdapter;
import processScheduler.ui.graph.layout.RandomLayout;

/**
 * Created by Milena on 16.11.2015.
 */
public class Screen{

    private Menu menu1, menu2, menu3;
    private Separator separatorVer= new Separator();
    private Separator separatorHor1= new Separator();
    private Separator separatorHor2= new Separator();
    private GraphAdapter graphAdapter;


    public Screen(Stage stage, GraphAdapter graphAdapter) {
        stage.show();

        //--------------Create Menu--------------------//
        menu1 = new Menu("File");
        menu2 = new Menu("Parameters");
        menu3 = new Menu("Help");
        this.graphAdapter = graphAdapter;
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem loadMenuItem = new MenuItem("Load");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        menu1.getItems().addAll(newMenuItem, loadMenuItem, saveMenuItem,
                new SeparatorMenuItem(), exitMenuItem);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu1, menu2, menu3);
        //--------------Create Main Screen--------------//
        VBox vBox = new VBox(2);
        HBox hBox = new HBox(2);

        Scene scene = new Scene(vBox, 802, 602);
        stage.setScene(scene);

        VBox vBox1 = new VBox(2);
        VBox vBox2 = new VBox(2);

        set_visual_part();

        TestNet testnet = new TestNet();
        vBox1.getChildren().addAll(graphAdapter.getScrollPane(),separatorHor1, testnet);

        FromOneToAnother shortdist = new FromOneToAnother(graphAdapter.getModel());
        TransNet transnet = new TransNet();
        vBox2.getChildren().addAll(shortdist,separatorHor2, transnet);


        hBox.getChildren().addAll(vBox1,separatorVer,vBox2);
        vBox.getChildren().addAll(menuBar,hBox);

        //--------------Set Scene-----------------------//
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        stage.setScene(scene);

        RandomLayout randomLayout = new RandomLayout(graphAdapter);
        randomLayout.execute();
    }

    private void set_visual_part(){
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
}