package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

public class LinearSelector<T extends Comparable<?>> implements Selector<T> {

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
        System.out.println(s1.conjuction(s2));
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
    public String toString() {
        return range.toString();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Selector<T> conjuction(Selector<T> selector) {
        if (!(selector instanceof LinearSelector))
            return null;
        LinearSelector<T> resultSelector = new LinearSelector<T>();
        resultSelector.range = range.intersection(((LinearSelector) selector).range);
        return resultSelector;
    }

}
