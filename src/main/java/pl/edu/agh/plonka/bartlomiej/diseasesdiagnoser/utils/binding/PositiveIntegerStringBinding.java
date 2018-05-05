package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding;

import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PositiveIntegerStringBinding extends StringBinding {

    private final ObservableValue<Number> observableValue;

    public PositiveIntegerStringBinding(final ObservableValue<Number> observableValue) {
        this.observableValue = observableValue;
        bind(observableValue);
    }

    @Override
    public void dispose() {
        super.unbind(observableValue);
    }

    @Override
    protected String computeValue() {
        final Number value = observableValue.getValue();
        return (value == null || value.intValue() < 0) ? "" : value.toString();
    }

    @Override
    public ObservableList<ObservableValue<?>> getDependencies() {
        return FXCollections.<ObservableValue<?>>singletonObservableList(observableValue);
    }
}
