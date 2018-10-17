package timeline.base;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public interface IRecordClipGroup extends Comparable<IRecordClipGroup> {
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    Duration getLength();
    Set<IRecordClip> getClips();
    String toString();
}
