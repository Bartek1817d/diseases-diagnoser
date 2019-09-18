package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.HashSet;

public class NominalSelector<T> extends HashSet<T> implements Selector<T> {

    private static final long serialVersionUID = 640758287916192919L;
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public NominalSelector() {
        super();
    }

    public NominalSelector(Collection<T> collection) {
        super(collection);
    }

    @Override
    public Selector conjunction(Selector selector) {
        if (!(selector instanceof NominalSelector))
            return null;
        NominalSelector<T> resultSelector = new NominalSelector<>(this);
        resultSelector.retainAll((NominalSelector<T>) selector);
        return resultSelector;
    }

    @Override
    public boolean contains(Selector selector) {
        if (selector == null)
            return false;
        if (selector instanceof NominalSelector) {
            NominalSelector<?> nominalSelector = (NominalSelector) selector;
            return containsAll(nominalSelector);
        }
        return false;
    }

    @Override
    public boolean covers(Collection<T> entities) {
        if (isEmpty())
            return true;
        if (CollectionUtils.isEmpty(entities))
            return false;
        return entities.containsAll(this);
    }

    @Override
    public boolean covers(T entity) {
        throw new NotImplementedException();
    }
}
