package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model;

import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Entity {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty label = new SimpleStringProperty();
    private final StringProperty comment = new SimpleStringProperty();
    private final SetProperty<Entity> classes = new SimpleSetProperty<>(
            FXCollections.observableSet(new HashSet<>()));

    public Entity() {
    }

    public Entity(String id) {
        this.id.set(id);
    }

    public String getID() {
        return id.get();
    }

    public void setID(String id) {
        this.id.set(id);
    }

    public String getLabel() {
        return label.get();
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public String getComment() {
        return comment.get();
    }

    public ObservableValue<String> getObservableComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    public Set<Entity> getClasses() {
        return classes.get();
    }

    public void setClasses(Set<Entity> classes) {
        this.classes.clear();
        this.classes.addAll(classes);
    }

    public void addClasses(Set<Entity> classes) {
        this.classes.addAll(classes);
    }

    public void addClass(Entity cls) {
        this.classes.add(cls);
    }

    @Override
    public String toString() {
        return label.get();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id.get() == null) ? 0 : id.get().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Entity other = (Entity) obj;
        if (id.get() == null) {
            return other.id.get() == null;
        } else {
            return id.get().equals(other.id.get());
        }
    }
}
