package timeline.base;

public class MaxClipGapException extends RuntimeException {

    MaxClipGapException(long maxClipGap) {
        super("Error: clip doesn't belong to this group"+"\n"+
                "Reason: clip start time exceeds the defined "+ maxClipGap +" length.");
    }

}
