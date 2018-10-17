package timeline;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import javafx.util.Callback;
import timeline.converters.IndicatorPositionConverter;
import timeline.library.IRecordsLibrary;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SliderColorable extends Slider {

    private StyleableObjectProperty<IndicatorPosition> indicatorPosition = new SimpleStyleableObjectProperty<>(
            StyleableProperties.INDICATOR_POSITION,
            SliderColorable.this,
            "indicatorPosition",
            IndicatorPosition.LEFT);

    private static final String DEFAULT_STYLE_CLASS = "slider-colorable";

    // inherit the styleable properties from parent
    private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    private IRecordsLibrary recrodsLibrary;

    public StyleableObjectProperty<IndicatorPosition> indicatorPositionProperty() {
        return this.indicatorPosition;
    }

    public SliderColorable() {
        this(0,100,50);
        initialise();
    }

    public SliderColorable(double min, double max, double value) {
        super(min,max,value);
        initialise();
    }

    private void initialise() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        setTo24HourFormat();
        setValueFactory(slider -> Bindings.createStringBinding(() -> stepTo60(this.getValue()), this.valueProperty()));
    }

    public void setTo24HourFormat() {
        this.setShowTickLabels(true);
        this.setShowTickMarks(true);
        this.setMin(0.0);
        this.setMax(24.0);
        this.setBlockIncrement(1.0);
        this.setMinorTickCount(5);
        this.setMajorTickUnit(1.0);

        setIndicatorPosition(IndicatorPosition.RIGHT);
    }

    public String getHourAndMinute() {
        return stepTo60(getValue());
    }

    private String stepTo60(double value) {
        String s = String.format("%.2f", value);
        String[] ss = s.split("\\.");
        double min = Math.round((Double.parseDouble(ss[1]) / 100) * 60);
        DecimalFormat df = new DecimalFormat("#");
        return ss[0] + ":" + df.format(min);
    }

    public void setRecordClips(IRecordsLibrary library) {
        this.recrodsLibrary = library;
        this.requestLayout();
    }

    public IRecordsLibrary getRecrodsLibrary() {
        return this.recrodsLibrary;
    }

    private ObjectProperty<Callback<SliderColorable, StringBinding>> valueFactory;

    public final ObjectProperty<Callback<SliderColorable, StringBinding>> valueFactoryProperty() {
        if (valueFactory == null) {
            valueFactory = new SimpleObjectProperty<>(this, "valueFactory");
        }
        return valueFactory;
    }

    /**
     * @return the current slider value factory
     */
    public final Callback<SliderColorable, StringBinding> getValueFactory() {
        return valueFactory == null ? null : valueFactory.get();
    }

    /**
     * sets custom string binding for the slider text value
     *
     * @param valueFactory a callback to create the string value binding
     */
    public final void setValueFactory(final Callback<SliderColorable, StringBinding> valueFactory) {
        this.valueFactoryProperty().set(valueFactory);
    }

    public IndicatorPosition getIndicatorPosition() {
        return indicatorPosition == null ? IndicatorPosition.LEFT : indicatorPosition.get();
    }

    public void setIndicatorPosition(IndicatorPosition pos) {
        this.indicatorPosition.set(pos);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
       return new SliderColoredSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return SliderColorable.class.getResource("/css/controls/slider-colorable.css").toExternalForm();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        if (STYLEABLES == null) {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            styleables.addAll(getClassCssMetaData());
            styleables.addAll(SliderColorable.getClassCssMetaData());
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        return STYLEABLES;
    }

    public enum IndicatorPosition {
        LEFT, RIGHT
    }

    private static class StyleableProperties {
        private static final CssMetaData<SliderColorable, IndicatorPosition> INDICATOR_POSITION = new CssMetaData<SliderColorable, IndicatorPosition>(
                "-jfx-indicator-position",
                IndicatorPositionConverter.getInstance(),
                SliderColorable.IndicatorPosition.LEFT) {
            @Override
            public boolean isSettable(SliderColorable control) {
                return control.indicatorPosition == null || !control.indicatorPosition.isBound();
            }

            @Override
            public StyleableProperty<IndicatorPosition> getStyleableProperty(SliderColorable control) {
                return control.indicatorPositionProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
                    Control.getClassCssMetaData());
            Collections.addAll(styleables, INDICATOR_POSITION);
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
}
