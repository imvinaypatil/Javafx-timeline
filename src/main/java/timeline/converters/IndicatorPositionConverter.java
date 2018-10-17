package timeline.converters;

import com.sun.javafx.css.StyleConverterImpl;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
import timeline.SliderColorable;

public class IndicatorPositionConverter extends StyleConverterImpl<String, SliderColorable.IndicatorPosition> {
    public static StyleConverter<String, SliderColorable.IndicatorPosition> getInstance() {
        return IndicatorPositionConverter.Holder.INSTANCE;
    }

    private IndicatorPositionConverter() {
    }

    public SliderColorable.IndicatorPosition convert(ParsedValue<String, SliderColorable.IndicatorPosition> value, Font not_used) {
        String string = ((String)value.getValue()).toUpperCase();

        try {
            return SliderColorable.IndicatorPosition.valueOf(string);
        } catch (NullPointerException | IllegalArgumentException var5) {
            return SliderColorable.IndicatorPosition.LEFT;
        }
    }

    public String toString() {
        return "IndicatorPositionConverter";
    }

    private static class Holder {
        static final IndicatorPositionConverter INSTANCE = new IndicatorPositionConverter();

        private Holder() {
        }
    }
}
