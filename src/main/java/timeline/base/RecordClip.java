package timeline.base;

import java.nio.file.Path;

import static timeline.base.RecordClipNode.scaleTo24Hr;

public class RecordClip implements IRecordClip {

    private final Path path;
    private final long START_TIME;
    private final long END_TIME;
    private final TYPE type;

    public RecordClip(Path path, long start_time, long end_time, TYPE type) {
        this.path = path;
        START_TIME = start_time;
        END_TIME = end_time;
        this.type = type;
    }

    public Path getPath() {
        return path;
    }

    public long getStartTime() {
        return START_TIME;
    }

    public long getEndTime() {
        return END_TIME;
    }

    public TYPE getType() {
        return type;
    }

    public int compareTo(IRecordClip o) {
        return (int)(this.getStartTime() - o.getStartTime());
    }

    @Override
    public String toString() {
        return path+" "+" ST: "+scaleTo24Hr(START_TIME)+" ET: "+scaleTo24Hr(END_TIME)+" TYPE: "+type;
    }
}
