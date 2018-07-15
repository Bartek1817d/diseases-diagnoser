package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

public interface Selector {
    Selector conjunction(Selector selector);

    boolean contains(Selector selector);
}
