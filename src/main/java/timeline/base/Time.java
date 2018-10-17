package timeline.base;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

public class Time {

    private final int hour;
    private final int minute;

    public Time(int h, int m) {
        hour = h;
        minute = m;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public Boolean isInside(LocalDateTime anotherTime) {
        if (anotherTime.getHour() < this.getHour()) return false;
        else return anotherTime.getHour() == this.getHour() && anotherTime.getMinute() > this.minute;
    }

    public Boolean isInsideOrEquals(LocalDateTime anotherTime) {
        if (anotherTime.getHour() < this.getHour()) return false;
        else return anotherTime.getHour() == this.getHour() && anotherTime.getMinute() >= this.minute;
    }

    public Boolean isInside(Time anotherTime) {
        if (anotherTime.getHour() < this.getHour()) return false;
        else return anotherTime.getHour() == this.getHour() && anotherTime.getMinute() > this.minute;
    }

    public static Time parseDouble(double d) {
        String s = String.format("%.2f", d);
        String[] ss = s.split("\\.");
        int minute = (int) Math.round((Double.parseDouble(ss[1]) / 100) * 60);
        int hour = Integer.parseInt(ss[0]);
        if (hour == 24 && minute == 0) {
            hour = 23;
            minute = 59;
        }
        return new Time(hour,minute);
    }

    public static String stepTo60(double value) {
        String s = String.format("%.2f", value);
        String[] ss = s.split("\\.");
        double min = Math.round((Double.parseDouble(ss[1]) / 100) * 60);
        DecimalFormat df = new DecimalFormat("#");
        return ss[0] + ":" + df.format(min);
    }
}
