package timeline.base;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class RecordClipNode extends Rectangle implements IRecordClipGroup {

    private TreeSet<IRecordClip> clips;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final TYPE type;
    private Duration length;
    private BooleanProperty isInvalid = new SimpleBooleanProperty(false);
    private InvalidationListener invalidationListener;
    /**
     *  MaxClipGap will be used decide the allowed gap two colored clip node.
     * This is also used at TrackView and MediaPlayerView to seek at a approximated clip
     */
    public static final long MAX_CLIP_GAP = TimeUnit.MINUTES.toMillis(5);

    public RecordClipNode(TYPE type) {
        super();
        this.type = type;
        clips = new TreeSet<>();
        initialise();
    }

    public RecordClipNode(IRecordClip clip) {
        this(clip.getType());
        try {
            addClips(clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecordClipNode(TYPE type, long initialStartTime, long initialEndTime) {
        this(type);
        startTime = scaleTo24Hr(initialStartTime);
        endTime = scaleTo24Hr(initialEndTime);
    }

    private void initGraphics() {
        this.setFill(type.toPaint());
        this.setStroke(Color.WHITE);
        this.setStrokeType(StrokeType.CENTERED);
        this.setStrokeWidth(0.5);
        this.setStrokeLineCap(StrokeLineCap.BUTT);
        this.setStrokeLineJoin(StrokeLineJoin.MITER);
        this.setStrokeMiterLimit(10);
    }

    public void draw(double x,double y,double width,double height) {
        this.setWidth(width);
        this.setHeight(height);
        this.setX(x);
        this.setY(y);
    }

    private void initialise() {
        registerListeners();
        initGraphics();
    }

    private void registerListeners() {
        isInvalid.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (invalidationListener != null) {
                    invalidationListener.invalidated(isInvalid);
                }
                length = null;
                isInvalid.setValue(false);
            }
        });

        this.addEventHandler(MouseEvent.MOUSE_PRESSED,event -> {
            this.setCursor(Cursor.CLOSED_HAND);
        });

        this.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> this.setCursor(Cursor.OPEN_HAND));

        this.addEventHandler(MouseEvent.MOUSE_EXITED, event -> this.setCursor(Cursor.DEFAULT));
    }

//    private IRecordClip getClipAtX(double x) {
//        int index = (int) Math.floor(x/this.getWidth() * clips.size());
//        if ( index >=0 && index < clips.size()) {
//            IRecordClip[] clipArr = (IRecordClip[]) clips.toArray();
//            return clipArr[index];
//        }
//        System.out.println(index);
//        return null;
//    }

    public void setInvalidationListener(InvalidationListener listener) {
        this.invalidationListener = listener;
    }

    private Duration calcLength() {
        Duration duration = Duration.ZERO;
        try {
            startTime = scaleTo24Hr(clips.first().getStartTime());
            endTime = scaleTo24Hr(clips.last().getEndTime());
            duration = Duration.between(startTime,endTime);
        } catch (NoSuchElementException e) {
//            e.printStackTrace();
        }
        return duration;
    }

    public static LocalDateTime scaleTo24Hr(long time) {
        Instant instant = Instant.ofEpochMilli(time);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public void addClips(IRecordClip ...clips) throws MaxClipGapException {
        for (IRecordClip clip : clips) {
            if (clip.getType() != this.type)
                throw new IllegalArgumentException("Error: unsupported TYPE operation. +\n"+
                        "Required: "+this.type+" found: "+clip.getType());
            if (this.clips.size() > 0 && (clip.getStartTime() - this.clips.last().getEndTime()) > MAX_CLIP_GAP)
                throw new MaxClipGapException(MAX_CLIP_GAP);

            this.clips.add(clip);
        }
        isInvalid.setValue(true);
        calcLength();
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Duration getLength() {
        if (length == null)
            length = calcLength();
        return length;
    }

    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder();
        for (IRecordClip clip : clips) {
            sb.append(clip.toString()).append("\n");
        }
        return sb.toString();
    }

    public TreeSet<IRecordClip> getClips() {
        return this.clips;
    }

    @Override
    public int compareTo(IRecordClipGroup o) {
        return this.startTime.compareTo((o).getStartTime());
    }

}
