package timeline;


import javafx.beans.property.DoubleProperty;
import timeline.library.EmptyLibraryException;
import timeline.library.IRecordsLibrary;

public interface ITimeline {
    void setRecordsLibrary(IRecordsLibrary library);
    void clearRecordsLibrary();
    IRecordsLibrary getRecordsLibrary();
    void reset();
    DoubleProperty valueProperty();
    void setSliderValue(double val);
    void enable();
    void disable();
    void populateRecords();
    void addTrackChangeListener(TrackChangeListener listener);
    void removeTrackChangeListener(TrackChangeListener listener);
    Timeline.ActiveClip getActiveClipNode() throws EmptyLibraryException;

    @FunctionalInterface
    interface TrackChangeListener {
        void onChange(Timeline.ActiveClip track);
    }
}
