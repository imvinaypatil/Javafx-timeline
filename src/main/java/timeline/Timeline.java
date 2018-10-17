package timeline;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import timeline.base.IRecordClipGroup;
import timeline.base.RecordClipNode;
import timeline.base.Time;
import timeline.library.EmptyLibraryException;
import timeline.library.IRecordsLibrary;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static timeline.base.RecordClipNode.MAX_CLIP_GAP;

/**
 * This class implements a Record Visualiser component
 * used for track marking based on records.
 */
public class Timeline extends Region implements ITimeline {
    private SliderColorable slider = new SliderColorable();
    private IRecordsLibrary library;
    private List<TrackChangeListener> trackChangeListeners = new LinkedList<>();
    private ActiveClip ACTIVE_CLIP_NODE;

    public Timeline() {
        super();
        this.getChildren().add(slider);
        registerListeners();
        populateRecords();
    }

    public Timeline(double w, double h) {
        this();
        super.setPrefWidth(w);
        super.setPrefHeight(h);
    }

    public Timeline(IRecordsLibrary library) {
        this();
        this.library = library;
    }

    public Timeline(double w, double h, IRecordsLibrary library) {
        this(w,h);
        this.library = library;
    }

    @Override
    public void setRecordsLibrary(IRecordsLibrary library) {
        this.library = library;
    }

    @Override
    public void clearRecordsLibrary() {
        this.library = null;
        populateRecords();
    }

    @Override
    public IRecordsLibrary getRecordsLibrary() {
        return this.library;
    }

    @Override
    public void reset() {
        ACTIVE_CLIP_NODE = null;
        this.library = null;
        populateRecords();
    }

    @Override
    public DoubleProperty valueProperty() {
        return slider.valueProperty();
    }

    @Override
    public void setSliderValue(double value) {
        slider.setValue(value);
    }

    @Override
    public void enable() {
        this.setDisable(false);
    }

    @Override
    public void disable() {
        this.setDisable(true);
    }

    @Override
    public void populateRecords() {
        slider.setRecordClips(library);
    }

    @Override
    public void addTrackChangeListener(TrackChangeListener listener) {
        this.trackChangeListeners.add(listener);
    }

    @Override
    public void removeTrackChangeListener(TrackChangeListener listener) {
        this.trackChangeListeners.remove(listener);
    }

    @Override
    public ActiveClip getActiveClipNode() throws EmptyLibraryException {
        if (ACTIVE_CLIP_NODE == null) {
            Iterator<RecordClipNode> iterator = library.getRecordClipNodes().iterator();
            if (iterator.hasNext()) {
                RecordClipNode clipNode = iterator.next();
                ACTIVE_CLIP_NODE = new ActiveClip(clipNode,new Time(clipNode.getStartTime().getHour(),clipNode.getStartTime().getMinute()));
            } else {
                throw new EmptyLibraryException("No clips !");
            }
        }
        return ACTIVE_CLIP_NODE;
    }

    private void resize() {
        slider.setPrefWidth(this.getWidth());
    }

    private void pollAndActivateNodeAt(double timed) throws EmptyLibraryException {
        if (library != null) {
            final LocalDate libraryDate = library.getDate();
            Time time = Time.parseDouble(timed);
            final LocalDateTime localDateTime = LocalDateTime.of(libraryDate.getYear(),libraryDate.getMonth(),libraryDate.getDayOfMonth(),time.getHour(),time.getMinute());

            LocalDateTime dateTime = LocalDateTime.of
                    (libraryDate.getYear(), libraryDate.getMonth(), libraryDate.getDayOfMonth(), time.getHour(), time.getMinute())
                    .minusSeconds(MAX_CLIP_GAP/1000);
            Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();

            final Optional<RecordClipNode> recordClipGroup = library.getRecordClipNodes().stream().sequential()
                    .filter(clipNode -> clipNode.getClips().stream().sequential().anyMatch(clip -> Instant.ofEpochMilli(clip.getStartTime())
                            .isAfter(instant)))
                    .findFirst();
            recordClipGroup.ifPresent((clipGroup) -> ACTIVE_CLIP_NODE = new ActiveClip(clipGroup,time));
        }
    }

    private EventHandler<MouseEvent> trackChangeHandler = event -> {
        try {
            pollAndActivateNodeAt(slider.getValue());
            if (ACTIVE_CLIP_NODE != null)
                trackChangeListeners.forEach(t -> t.onChange(ACTIVE_CLIP_NODE));
            event.consume();
        } catch (EmptyLibraryException e) {
            e.printStackTrace();
        }
    };

    private void registerListeners() {
        widthProperty().addListener(observable -> resize());
        heightProperty().addListener(observable -> resize());

        this.slider.addEventHandler(MouseEvent.MOUSE_RELEASED, this.trackChangeHandler);
        this.slider.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.trackChangeHandler);

    }

    final public class ActiveClip {
        private final IRecordClipGroup clipGroup;
        private final Time time;

        ActiveClip(IRecordClipGroup clipGroup, Time time) {
            this.clipGroup = clipGroup;
            this.time = time;
        }

        IRecordClipGroup getClipGroup() {
            return clipGroup;
        }

        public Time getTime() {
            return time;
        }
    }
}
