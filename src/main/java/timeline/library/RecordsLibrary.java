package timeline.library;

import timeline.base.IRecordClip;
import timeline.base.IRecordClipGroup;
import timeline.base.LibraryChangeListener;
import timeline.base.RecordClipNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RecordsLibrary implements IRecordsLibrary {

    private List<LibraryChangeListener> listeners;
    private List<RecordClipNode> clipNodes = new LinkedList<>();
    private LocalDate date;

    public RecordsLibrary() {

    }


    @Override
    public List<RecordClipNode> getRecordClipNodes() {
        return clipNodes;
    }

    @Override
    public List<IRecordClip> getRecordClips() {
        final List<IRecordClip> clips = new LinkedList<>();
        clipNodes.forEach(clipNode -> {
            clips.addAll(clipNode.getClips());
        });
        return clips;
    }

    @Override
    public synchronized void addChangeListener(LibraryChangeListener listener) {
        if (listeners == null)
            listeners = new ArrayList<>(2);
        listeners.add(listener);
    }

    @Override
    public void addClipNode(RecordClipNode ...clipNode) {
        clipNodes.addAll(Arrays.asList(clipNode));
    }

    @Override
    public void addClip(RecordClipNode clipNode, IRecordClip clip) throws Exception {
        throw new Exception("Unsupported Operation");
    }

    @Override
    public void createClipNodeWithClips(IRecordClip... clips) {

    }

    @Override
    public boolean isEmpty() {
        return this.clipNodes.isEmpty();
    }

    @Override
    public LocalDate getDate() throws EmptyLibraryException {
        if (date == null) {
            if (clipNodes.isEmpty())
                throw new EmptyLibraryException("getDate() performed on Empty Library");
            else {
                IRecordClipGroup clip = clipNodes.iterator().next();
                LocalDateTime clipStartTime = clip.getStartTime();
                date = LocalDate.of(clipStartTime.getYear(),clipStartTime.getMonth(),clipStartTime.getDayOfMonth());
            }
        }
        return date;
    }

}
