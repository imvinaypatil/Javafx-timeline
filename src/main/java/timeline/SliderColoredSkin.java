package timeline;

import com.sun.javafx.scene.control.skin.SliderSkin;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import timeline.base.RecordClipNode;

import java.time.temporal.ChronoField;

/**
 * Custom SliderSkin Implementation for multicolored track
 * Used for marking track region for representation of RecordFiles presence.
 */
public class SliderColoredSkin extends SliderSkin {

    private final Pane mouseHandlerPane = new Pane();
    private static final PseudoClass MIN_VALUE = PseudoClass.getPseudoClass("min");
    private static final PseudoClass MAX_VALUE = PseudoClass.getPseudoClass("max");

    private Text sliderValue;
    private StackPane coloredTrack;
    private Pane recordsGroup;
    private StackPane thumb;
    private StackPane track;
    private StackPane animatedThumb;

    private Timeline timeline;

    private double indicatorRotation;
    private double horizontalRotation;
    private double shifting;
    private boolean isValid = false;

    public SliderColoredSkin(SliderColorable slider) {
        super(slider);

        track = (StackPane) getSkinnable().lookup(".track");
        thumb = (StackPane) getSkinnable().lookup(".thumb");

        coloredTrack = new StackPane();
        coloredTrack.getStyleClass().add("colored-track");
        coloredTrack.setMouseTransparent(true);

        recordsGroup = new AnchorPane();
        recordsGroup.setMouseTransparent(true);
        recordsGroup.setPickOnBounds(false);

        sliderValue = new Text();
        sliderValue.getStyleClass().setAll("slider-value");

        animatedThumb = new StackPane();
        animatedThumb.getStyleClass().add("animated-thumb");
        animatedThumb.getChildren().add(sliderValue);
        animatedThumb.setMouseTransparent(true);
        animatedThumb.setScaleX(0);
        animatedThumb.setScaleY(0);

        getChildren().add(getChildren().indexOf(thumb), coloredTrack);
        getChildren().add(getChildren().indexOf(thumb), animatedThumb);
        getChildren().add(getChildren().indexOf(thumb), recordsGroup);
        getChildren().add(0, mouseHandlerPane);

        registerChangeListener(slider.valueFactoryProperty(),"VALUE_FACTORY");
        initListeners();

    }

    private void delegateToTrack(MouseEvent event) {
        if(!event.isConsumed()) {
            track.fireEvent(event);
        }
    }

    private void refreshSliderValueBinding() {
        sliderValue.textProperty().unbind();
        if (((SliderColorable)getSkinnable()).getValueFactory() != null) {
            sliderValue.textProperty().bind(((SliderColorable)getSkinnable()).getValueFactory().call((SliderColorable)getSkinnable()));
        } else {
            sliderValue.textProperty().bind(Bindings.createStringBinding(() -> {
                if (getSkinnable().getLabelFormatter() != null) {
                    return getSkinnable().getLabelFormatter().toString(getSkinnable().getValue());
                } else {
                    return String.valueOf(Math.round(getSkinnable().getValue()));
                }
            },getSkinnable().valueProperty()));
        }

    }

    private void updateValueStyleClass() {
        getSkinnable().pseudoClassStateChanged(MIN_VALUE, getSkinnable().getMin() == getSkinnable().getValue());
        getSkinnable().pseudoClassStateChanged(MAX_VALUE, getSkinnable().getMax() == getSkinnable().getValue());
    }

    private void initAnimation(Orientation orientation) {
        double thumbPos, thumbNewPos;
        DoubleProperty layoutProperty;

        if (orientation == Orientation.HORIZONTAL) {
            if (((SliderColorable) getSkinnable()).getIndicatorPosition() == SliderColorable.IndicatorPosition.RIGHT) {
                thumbPos = thumb.getLayoutY() - thumb.getHeight();
                thumbNewPos = thumbPos - shifting + 20;
            } else {
                double height = animatedThumb.prefHeight(animatedThumb.prefWidth(-1));
                thumbPos = thumb.getLayoutY() - height / 2;
                thumbNewPos = thumb.getLayoutY() - height - thumb.getHeight();
            }
            layoutProperty = animatedThumb.translateYProperty();
        } else {
            if (((SliderColorable) getSkinnable()).getIndicatorPosition() == SliderColorable.IndicatorPosition.RIGHT) {
                thumbPos = thumb.getLayoutX() - thumb.getWidth();
                thumbNewPos = thumbPos - shifting;
            } else {
                double width = animatedThumb.prefWidth(-1);
                thumbPos = thumb.getLayoutX() - width / 2;
                thumbNewPos = thumb.getLayoutX() - width - thumb.getWidth();
            }
            layoutProperty = animatedThumb.translateXProperty();
        }

        clearAnimation();

        timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(animatedThumb.scaleXProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(animatedThumb.scaleYProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(layoutProperty, thumbPos, Interpolator.EASE_BOTH)),
                new KeyFrame(
                        Duration.seconds(0.2),
                        new KeyValue(animatedThumb.scaleXProperty(), 1, Interpolator.EASE_BOTH),
                        new KeyValue(animatedThumb.scaleYProperty(), 1, Interpolator.EASE_BOTH),
                        new KeyValue(layoutProperty, thumbNewPos, Interpolator.EASE_BOTH)));
    }

    private void clearAnimation() {
        if (timeline != null) {
            timeline.stop();
            timeline.getKeyFrames().clear();
            timeline = null;
        }
    }

    private void initListeners() {
        mouseHandlerPane.setOnMousePressed(this::delegateToTrack);
        mouseHandlerPane.setOnMouseReleased(this::delegateToTrack);
        mouseHandlerPane.setOnMouseDragged(this::delegateToTrack);

        track.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            timeline.setRate(1);
            timeline.play();
        });
        track.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            timeline.setRate(-1);
            timeline.play();
        });
        thumb.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            timeline.setRate(1);
            timeline.play();
        });
        thumb.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            timeline.setRate(-1);
            timeline.play();
        });

        refreshSliderValueBinding();
        updateValueStyleClass();

        getSkinnable().valueProperty().addListener(observable -> updateValueStyleClass());
        getSkinnable().orientationProperty().addListener(observable -> initAnimation(getSkinnable().getOrientation()));

    }

    private void inialiseVariables() {
        shifting = 30 + thumb.getWidth();
        if (getSkinnable().getOrientation() != Orientation.HORIZONTAL) {
            horizontalRotation = -90;
        }
        if (((SliderColorable)getSkinnable()).getIndicatorPosition() != SliderColorable.IndicatorPosition.LEFT) {
            indicatorRotation = 180;
            shifting = -shifting;
        }
        final double rotationAngle = 45;
        sliderValue.setRotate(rotationAngle+indicatorRotation+3*horizontalRotation);
        animatedThumb.setRotate(-rotationAngle+indicatorRotation+horizontalRotation);
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("VALUE_FACTORY".equals(p)) {
            refreshSliderValueBinding();
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x,y,w,h);
        if (!isValid) {
            inialiseVariables();
            initAnimation(getSkinnable().getOrientation());
            isValid = true;
        }

        double prefWidth = animatedThumb.prefWidth(-1);
        animatedThumb.resize(prefWidth, animatedThumb.prefHeight(prefWidth));

        boolean horizontal = getSkinnable().getOrientation() == Orientation.HORIZONTAL;
        double width, height, layoutX, layoutY;
        if (horizontal) {
            width = track.getWidth();
            height = track.getHeight();
            layoutX = track.getLayoutX();
            layoutY = track.getLayoutY();
            animatedThumb.setLayoutX(thumb.getLayoutX() + thumb.getWidth() / 2 - animatedThumb.getWidth() / 2);
        } else {
            height = track.getLayoutBounds().getMaxY() + track.getLayoutY() - thumb.getLayoutY() - snappedBottomInset();
            width = track.getWidth();
            layoutX = track.getLayoutX();
            layoutY = thumb.getLayoutY();
            animatedThumb.setLayoutY(thumb.getLayoutY() + thumb.getHeight() / 2 - animatedThumb.getHeight() / 2);
        }

        coloredTrack.resizeRelocate(layoutX, layoutY, width, height);
//        recordsGroup.resizeRelocate(layoutX,layoutY,width,height);
        mouseHandlerPane.resizeRelocate(x, y, w, h);
        drawClipNodes();

    }

    private void drawClipNodes() {
        SliderColorable slider = (SliderColorable)this.getSkinnable();

        if (slider.getRecrodsLibrary() != null && !slider.getRecrodsLibrary().isEmpty()) {
            double majorUnit = track.getWidth()/(slider.getMajorTickUnit()*slider.getMax());
            double minorUnit = majorUnit/getSkinnable().getMinorTickCount();
            for (RecordClipNode clip : slider.getRecrodsLibrary().getRecordClipNodes()) {
                double startH = clip.getStartTime().getLong(ChronoField.HOUR_OF_DAY);
                double startM = clip.getStartTime().getMinute();
                double x = (startH * majorUnit)+ ( (startM / 12)*minorUnit );
                double y = track.getLayoutY();
                double width = (clip.getLength().toMinutes()*(majorUnit+minorUnit))/(12+59);
                double height = 15;
                clip.draw(x,y,width,height);
//                System.out.println(clip.toString()+" Stime: "+clip.getStartTime().toString()+" Etime: "+clip.getEndTime());
            }

            recordsGroup.getChildren().setAll(((SliderColorable)getSkinnable()).getRecrodsLibrary().getRecordClipNodes());
        } else {
            recordsGroup.getChildren().remove(0,recordsGroup.getChildren().size());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        clearAnimation();
    }

}
