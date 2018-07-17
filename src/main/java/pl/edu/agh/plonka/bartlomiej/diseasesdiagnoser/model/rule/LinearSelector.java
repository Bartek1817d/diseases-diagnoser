package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class LinearSelector<T extends Comparable<?>> implements Selector<T> {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private Range<T> range = Range.all();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static LinearSelector lessThanSelector(Comparable value) {
        LinearSelector selector = new LinearSelector();
        selector.range = Range.lessThan(value);
        return selector;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static LinearSelector atMostSelector(Comparable value) {
        LinearSelector selector = new LinearSelector();
        selector.range = Range.atMost(value);
        return selector;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static LinearSelector greaterThanSelector(Comparable value) {
        LinearSelector selector = new LinearSelector();
        selector.range = Range.greaterThan(value);
        return selector;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static LinearSelector atLeastSelector(Comparable value) {
        LinearSelector selector = new LinearSelector();
        selector.range = Range.atLeast(value);
        return selector;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static LinearSelector equalSelector(Comparable value) {
        LinearSelector selector = new LinearSelector();
        selector.range = Range.singleton(value);
        return selector;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        LinearSelector<Integer> s1 = LinearSelector.atLeastSelector(10);
        LinearSelector<Integer> s2 = LinearSelector.lessThanSelector(12);
        System.out.println(s1.conjunction(s2));
        System.out.println(s1);
        System.out.println(s2);
    }

    public boolean contains(T value) {
        return range.contains(value);
    }

    public boolean contains(LinearSelector<T> selector) {
        return range.encloses(selector.range);
    }

    public boolean hasLowerBound() {
        return range.hasLowerBound();
    }

    public boolean hasUpperBound() {
        return range.hasUpperBound();
    }

    public BoundType lowerBoundType() {
        return range.lowerBoundType();
    }

    public BoundType upperBoundType() {
        return range.upperBoundType();
    }

    public T lowerEndpoint() {
        return range.lowerEndpoint();
    }

    public T upperEndpoint() {
        return range.upperEndpoint();
    }

    @Override
    public boolean contains(Selector selector) {
        if (selector == null)
            return true;
        if (selector instanceof LinearSelector) {
            LinearSelector<T> linearSelector = (LinearSelector) selector;
            return contains(linearSelector);
        }
        return false;
    }

    @Override
    public String toString() {
        return range.toString();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Selector conjunction(Selector selector) {
        if (!(selector instanceof LinearSelector))
            return null;
        LinearSelector<T> resultSelector = new LinearSelector<T>();
        resultSelector.range = range.intersection(((LinearSelector) selector).range);
        return resultSelector;
    }

    @Override
    public boolean covers(Collection<T> entities) {
        return range.containsAll(entities);
    }

    @Override
    public boolean covers(T entity) {
        return entity != null && range.contains(entity);
    }
}
