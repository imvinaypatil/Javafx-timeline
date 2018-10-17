package timeline.base;


import java.nio.file.Path;

public interface IRecordClip extends Comparable<IRecordClip> {
    public Path getPath();

    public long getStartTime();

    public long getEndTime();

    public TYPE getType();
}
