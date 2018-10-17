package ViewsTest;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import timeline.Timeline;
import timeline.SliderColorable;
import timeline.base.MaxClipGapException;
import timeline.base.RecordClip;
import timeline.base.RecordClipNode;
import timeline.base.TYPE;
import timeline.library.RecordsLibrary;

import java.nio.file.Paths;

public class ColoredSliderTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Timeline recordVisualiser = new Timeline(600,400);
        RecordsLibrary library = new RecordsLibrary();
        recordVisualiser.setRecordsLibrary(library);
        recordVisualiser.populateRecords();

        RecordClipNode recordClipNode = new RecordClipNode(TYPE.ALWAYS);
        RecordClipNode recordClipNode1 = new RecordClipNode(TYPE.TIMED);
        try {
            recordClipNode.addClips(new RecordClip(Paths.get("/one/1"),1538573159000L,1538576759034L,TYPE.ALWAYS));
//            recordClipNode.addClips(new RecordClip(Paths.get("/one/2"),1538586759034L,1538578989034L,TYPE.ALWAYS));
            recordClipNode1.addClips(new RecordClip(Paths.get("/two/1"),1538521299000L,1538521959034L,TYPE.TIMED));
        } catch (MaxClipGapException e) {
            e.printStackTrace();
        }

        RecordClipNode recordClipNode2 = new RecordClipNode(new RecordClip(Paths.get("/2"),1538571159000L,1538573159000L,TYPE.EMERGENCY));
        RecordClipNode recordClipNode3 = new RecordClipNode(new RecordClip(Paths.get("/3"),System.currentTimeMillis(),System.currentTimeMillis()+60000,TYPE.ALARM));


        library.addClipNode(recordClipNode,recordClipNode1,recordClipNode2,recordClipNode3);


        AnchorPane pane = new AnchorPane(recordVisualiser);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
//        recordVisualiser.setStyle("-fx-background-color: #ff4444;");

        AnchorPane.setTopAnchor(recordVisualiser, 30d);
        AnchorPane.setRightAnchor(recordVisualiser, 10d);
        AnchorPane.setBottomAnchor(recordVisualiser, 30d);
        AnchorPane.setLeftAnchor(recordVisualiser, 30d);

        Scene scene = new Scene(pane);

        primaryStage.setTitle("JavaFX Timeline Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}