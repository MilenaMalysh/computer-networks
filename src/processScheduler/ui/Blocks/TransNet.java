package processScheduler.ui.Blocks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.TextArea;


import java.awt.*;

/**
 * Created by Milena on 16.11.2015.
 */
public class TransNet extends BorderPane {

    private VBox vboxmain;
    private Button btn1;
    private Button btn2;
    private TextArea textarea;

    public TransNet() {

        Label label = new Label("Transfer parameters");
        label.setFont(new Font("Times New Roman", 16));

        vboxmain = new VBox(2);
        //---------------Set buttons--------------//

        BorderPane bordpane1 = new BorderPane();

        //HBox btns = new HBox();

        //btn1 = new Button();
        //btn2 = new Button();
        //btn1.setText("Update");
        //btn2.setText("Optimize");

        //btns.getChildren().addAll(btn1, btn2);

        //btns.setMargin(btn1, new Insets(2,2,2,2));
        //btns.setMargin(btn2, new Insets(2,2,2,2));

        //bordpane1.setCenter(btns);

        //---------------Text with Label----------//

        BorderPane bordpane2 = new BorderPane();

        HBox hBox1 = new HBox();

        VBox vBox1 = new VBox();
        VBox vBox2 = new VBox();

        Label label11 = new Label();
        label11.setText("Syst. packages:");
        Label label12 = new Label();
        label12.setText("Ticks:");
        Label label13 = new Label();
        label13.setText("Average system time");

        vBox1.getChildren().addAll(label11,label12, label13);
        vBox1.setMargin(label11, new Insets(4));
        vBox1.setMargin(label12, new Insets(4));

        Label label21 = new Label();
        label21.setText("Result 1");
        Label label22 = new Label();
        label22.setText("Result 2");
        Label label23 = new Label();
        label22.setText("Result 2");
        vBox2.getChildren().addAll(label21,label22, label23);
        vBox2.setMargin(label21, new Insets(4));
        vBox2.setMargin(label22, new Insets(4));

        hBox1.getChildren().addAll(vBox1,vBox2);

        bordpane2.setCenter(hBox1);
        //---------------Text field details-------//

        BorderPane bordpane3 = new BorderPane();

        textarea = new TextArea();
        bordpane3.setCenter(textarea);

        //---------------Resume-------------------//

        vboxmain.getChildren().addAll(bordpane2,bordpane3);


        this.setTop(label);
        this.setCenter(vboxmain);
        this.setMargin(label, new Insets(0,5,5,5));

        set_visual_part();
    }
    public void set_visual_part(){
        this.setMinSize(300,400);
        this.setMaxSize(300,400);
        this.setPrefSize(300,400);

//        btn1.setMaxWidth(120);
 //       btn1.setMinWidth(120);
 //       btn1.setPrefWidth(120);
//        btn2.setMaxWidth(120);
 //       btn2.setMinWidth(120);
//        btn2.setPrefWidth(120);

        textarea.setPrefColumnCount(30);
        textarea.maxHeight(250);
        textarea.minHeight(250);
        textarea.prefHeight(250);
        textarea.maxWidth(200);
        textarea.minWidth(200);
        textarea.prefWidth(200);

        vboxmain.setMaxWidth(250);
        vboxmain.setMinWidth(250);
        vboxmain.setPrefWidth(250);
    }
}
