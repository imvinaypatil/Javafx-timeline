package timeline.base;

import javafx.scene.paint.Color;

public enum TYPE {
    ALWAYS, MOTION, TIMED, EMERGENCY, ALARM, MISC;

    @Override
    public String toString() {
        return super.toString();
    }

    public Color toPaint() {
        switch (this) {
            case ALWAYS: return Color.valueOf("MAGENTA");
            case MOTION: return Color.valueOf("DODGERBLUE");
            case TIMED: return Color.valueOf("YELLOWGREEN");
            case EMERGENCY: return Color.valueOf("RED");
            case ALARM: return Color.valueOf("MEDIUMVIOLETRED");
            case MISC: return Color.valueOf("LIGHTCYAN");
            default: return Color.valueOf("SNOW");
        }
    }

    public static TYPE getType(String type) {
        type = type.toUpperCase();
        switch (type) {
            case "ALWAYS": return ALWAYS;
            case "MOTION": return MOTION;
            case "TIMED": return TIMED;
            case "EMERGENCY": return EMERGENCY;
            case "ALARM": return ALARM;
            default: return MISC;
        }
    }
}
