package processScheduler.ui.Blocks;

import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import processScheduler.logic.Message;


import java.awt.*;

/**
 * Created by Milena on 16.11.2015.
 */
public class TransNet extends BorderPane {

    private final Label label24;
    private final Label label25;
    private final Label label26;
    private final Label label27;
    private final Label label28;
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
        Label label15 = new Label();
        label15.setText("Sent messages");
        label15.setFont(new Font("Times New Roman", 16));
        Label label16 = new Label();
        label16.setText("Delivered messages");
        label16.setFont(new Font("Times New Roman", 16));
        Label label17 = new Label();
        label17.setText("Informative data");
        label17.setFont(new Font("Times New Roman", 16));
        Label label18 = new Label();
        label18.setText("System data");
        label18.setFont(new Font("Times New Roman", 16));

        vBox1.getChildren().addAll(label11, label12, label13, label14, label15, label16, label17, label18);
        vBox1.setMargin(label11, new Insets(4));
        vBox1.setMargin(label12, new Insets(4));
        vBox1.setMargin(label13, new Insets(4));
        vBox1.setMargin(label14, new Insets(4));
        vBox1.setMargin(label15, new Insets(4));
        vBox1.setMargin(label16, new Insets(4));
        vBox1.setMargin(label17, new Insets(4));
        vBox1.setMargin(label18, new Insets(4));

        label21 = new Label();
        label21.setFont(new Font("Times New Roman", 16));
        label22 = new Label();
        label22.setFont(new Font("Times New Roman", 16));
        label23 = new Label();
        label23.setFont(new Font("Times New Roman", 16));
        label24 = new Label();
        label24.setFont(new Font("Times New Roman", 16));
        label25 = new Label();
        label25.setFont(new Font("Times New Roman", 16));
        label26 = new Label();
        label26.setFont(new Font("Times New Roman", 16));
        label27 = new Label();
        label27.setFont(new Font("Times New Roman", 16));
        label28 = new Label();
        label28.setFont(new Font("Times New Roman", 16));

        vBox2.getChildren().addAll(label21, label22, label23, label24, label25, label26, label27, label28);
        vBox2.setMargin(label21, new Insets(4));
        vBox2.setMargin(label22, new Insets(4));
        vBox2.setMargin(label23, new Insets(4));
        vBox2.setMargin(label24, new Insets(4));
        vBox2.setMargin(label25, new Insets(4));
        vBox2.setMargin(label26, new Insets(4));
        vBox2.setMargin(label27, new Insets(4));
        vBox2.setMargin(label28, new Insets(4));

        hBox1.getChildren().addAll(vBox1, vBox2);

        bordpane2.setCenter(hBox1);
        //---------------Text field details-------//

        BorderPane bordpane3 = new BorderPane();

        //textarea = new TextArea();
        //bordpane3.setCenter(textarea);

        //---------------Resume-------------------//

        vboxmain.getChildren().addAll(bordpane2, bordpane3);


        this.setTop(label);
        this.setCenter(vboxmain);
        this.setMargin(label, new Insets(0, 5, 5, 5));

        set_visual_part();
    }

    public void set_visual_part() {
        this.setMinSize(300, 450);
        this.setMaxSize(300, 450);
        this.setPrefSize(300, 450);

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

    public void bindLabels(IntegerProperty dataPkgs,
                           IntegerProperty sysPkgs,
                           IntegerProperty systemTime,
                           IntegerProperty averageTime,
                           IntegerProperty informationalData,
                           IntegerProperty systemData,
                           ObservableList<Message> sentMessages,
                           ObservableList<Message> receivedMessages) {
        label21.textProperty().unbind();
        label22.textProperty().unbind();
        label23.textProperty().unbind();
        label24.textProperty().unbind();
        label25.textProperty().unbind();
        label26.textProperty().unbind();
        label27.textProperty().unbind();
        label28.textProperty().unbind();
        label21.textProperty().bind(dataPkgs.asString());
        label22.textProperty().bind(sysPkgs.asString());
        label23.textProperty().bind(systemTime.asString());
        label24.textProperty().bind(averageTime.asString());
        label27.textProperty().bind(informationalData.asString());
        label28.textProperty().bind(systemData.asString());
        sentMessages.addListener(new ListChangeListener<Message>() {
            @Override
            public void onChanged(Change<? extends Message> c) {
                label25.setText(String.valueOf(sentMessages.size()));
            }
        });
        receivedMessages.addListener(new ListChangeListener<Message>() {
            @Override
            public void onChanged(Change<? extends Message> c) {
                label26.setText(String.valueOf(receivedMessages.size()));
            }
        });
    }
}
