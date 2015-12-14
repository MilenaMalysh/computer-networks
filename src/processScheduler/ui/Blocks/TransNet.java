package processScheduler.ui.Blocks;

import javafx.beans.property.IntegerProperty;
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
import javafx.util.converter.NumberStringConverter;


import java.awt.*;

/**
 * Created by Milena on 16.11.2015.
 */
public class TransNet extends BorderPane {

    private final Label label24;
    private VBox vboxmain;
    private Button btn1;
    private Button btn2;
    private TextArea textarea;
    private final Label label21;
    private final Label label22;
    private final Label label23;

    public TransNet() {

        Label label = new Label("Transfer parameters");
        label.setFont(new Font("Times New Roman", 18));

        vboxmain = new VBox(2);

        BorderPane bordpane2 = new BorderPane();

        HBox hBox1 = new HBox();

        VBox vBox1 = new VBox();
        VBox vBox2 = new VBox();

        Label label11 = new Label();
        label11.setText("Data packages:");
        label11.setFont(new Font("Times New Roman", 16));
        Label label12 = new Label();
        label12.setText("Syst. packages:");
        label12.setFont(new Font("Times New Roman", 16));
        Label label13 = new Label();
        label13.setText("Ticks:");
        label13.setFont(new Font("Times New Roman", 16));
        Label label14 = new Label();
        label14.setText("Avg. msg. delivery");
        label14.setFont(new Font("Times New Roman", 16));

        vBox1.getChildren().addAll(label11,label12, label13, label14);
        vBox1.setMargin(label11, new Insets(4));
        vBox1.setMargin(label12, new Insets(4));
        vBox1.setMargin(label13, new Insets(4));
        vBox1.setMargin(label14, new Insets(4));

        label21 = new Label();
        label21.setFont(new Font("Times New Roman", 16));
        label22 = new Label();
        label22.setFont(new Font("Times New Roman", 16));
        label23 = new Label();
        label23.setFont(new Font("Times New Roman", 16));
        label24 = new Label();
        label24.setFont(new Font("Times New Roman", 16));

        vBox2.getChildren().addAll(label21, label22, label23, label24);
        vBox2.setMargin(label21, new Insets(4));
        vBox2.setMargin(label22, new Insets(4));
        vBox2.setMargin(label23, new Insets(4));
        vBox2.setMargin(label24, new Insets(4));

        hBox1.getChildren().addAll(vBox1,vBox2);

        bordpane2.setCenter(hBox1);
        //---------------Text field details-------//

        BorderPane bordpane3 = new BorderPane();

        //textarea = new TextArea();
        //bordpane3.setCenter(textarea);

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

        //textarea.setPrefColumnCount(20);
        //textarea.maxHeight(250);
        //textarea.minHeight(250);
        //textarea.prefHeight(250);
        //textarea.maxWidth(200);
        //textarea.minWidth(200);
        //textarea.prefWidth(200);

        vboxmain.setMaxWidth(250);
        vboxmain.setMinWidth(250);
        vboxmain.setPrefWidth(250);
    }

    public void bindLabels(IntegerProperty dataPkgs, IntegerProperty sysPkgs, IntegerProperty systemTime, IntegerProperty averageTime){
        label21.textProperty().unbind();
        label22.textProperty().unbind();
        label23.textProperty().unbind();
        label24.textProperty().unbind();
        label21.textProperty().bind(dataPkgs.asString());
        label22.textProperty().bind(sysPkgs.asString());
        label23.textProperty().bind(systemTime.asString());
        label24.textProperty().bind(averageTime.asString());
    }
}
