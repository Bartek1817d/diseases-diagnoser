package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

public interface Selector<T> {
    public Selector<T> conjuction(Selector<T> selector);
}
