package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Star extends ArrayList<Complex> {

    public Star() {
        add(new Complex());
    }

    public static void main(String[] args) {
        List<Integer> l = new ArrayList<Integer>();
        Collections.sort(l);
        l.subList(Math.min(l.size(), 3), l.size()).clear();
        System.out.println(l);
    }

    public boolean isPatientCovered(Patient patient) {
        for (Complex complex : this) {
            if (complex.isPatientCovered(patient))
                return true;
        }
        return false;
    }

    public void intersection(Collection<Complex> otherComplexes) {
        if (isEmpty())
            addAll(otherComplexes);
        Collection<Complex> newComplexes = Complex.intersection(this, otherComplexes);
        clear();
        addAll(newComplexes);
    }

    public void deleteNarrowComplexes() {
        Collection<Complex> toRemove = new ArrayList<Complex>();
        for (Complex c1 : this)
            for (Complex c2 : this)
                if (c1 != c2 && c1.contains(c2))
                    toRemove.add(c2);
        removeAll(toRemove);
    }

    public void leaveFirstElements(int n) {
        subList(Math.min(n, size()), size()).clear();
    }

}
