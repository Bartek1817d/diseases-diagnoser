package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class PositiveIntegerStringBindingTest {

    private static final Integer VALUE = 10;

    private PositiveIntegerStringBinding binding;
    private ObservableValue<Number> observableValue;

    @Before
    public void setUp() {
        observableValue = spy(new SimpleIntegerProperty(VALUE));
        binding = new PositiveIntegerStringBinding(observableValue);
    }

    @Test
    public void dispose() {
        binding.dispose();
        verify(observableValue).removeListener(any(InvalidationListener.class));
    }

    @Test
    public void computeValue() {
        String stringValue = binding.computeValue();
        assertEquals(VALUE.toString(), stringValue);
    }

    @Test
    public void getDependencies() {
        ObservableList<ObservableValue<?>> dependencies = binding.getDependencies();
        assertEquals(VALUE, dependencies.get(0).getValue());
    }
}