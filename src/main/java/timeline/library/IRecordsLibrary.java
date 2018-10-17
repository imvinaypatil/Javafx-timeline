package timeline.library;


import timeline.base.IRecordClip;
import timeline.base.LibraryChangeListener;
import timeline.base.RecordClipNode;

import java.time.LocalDate;
import java.util.List;

public interface IRecordsLibrary {
    List<RecordClipNode> getRecordClipNodes();
    List<IRecordClip> getRecordClips();
    void addChangeListener(LibraryChangeListener listener);
    void addClipNode(RecordClipNode... clipNode);
    void addClip(RecordClipNode clipNode, IRecordClip clip) throws Exception;
    void createClipNodeWithClips(IRecordClip... clips);
    boolean isEmpty();
    LocalDate getDate() throws EmptyLibraryException;
}
