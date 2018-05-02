package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class NominalSelector<T> extends HashSet<T> implements Selector<T> {

    private static final long serialVersionUID = 640758287916192919L;

    public NominalSelector() {
        super();
    }

    public NominalSelector(Collection<T> collection) {
        super(collection);
    }

    public static void main(String[] args) {
        Selector<Integer> s1 = new NominalSelector<Integer>(Arrays.asList(4, 6, 2, 7, 2));
        Selector<Integer> s2 = new NominalSelector<Integer>(Arrays.asList(4, 6, 2));
        System.out.println(s1.conjuction(s2));
        System.out.println(s1);
        System.out.println(s2);
    }

    @Override
    public Selector<T> conjuction(Selector<T> selector) {
        if (!(selector instanceof NominalSelector))
            return null;
        NominalSelector<T> resultSelector = new NominalSelector<T>(this);
        resultSelector.retainAll((NominalSelector<T>) selector);
        return resultSelector;
    }

}
