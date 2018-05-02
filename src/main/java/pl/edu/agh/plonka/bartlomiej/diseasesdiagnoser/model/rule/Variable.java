package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;

public class Variable {
    private String name;
    private Entity parentClass;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, Entity parentClass) {
        this.name = name;
        this.parentClass = parentClass;
    }

    public static void main(String args[]) {
        Variable var = new Variable("patient");
        System.out.println(var);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Entity getParentClass() {
        return parentClass;
    }

    public void setParentClass(Entity parentClass) {
        this.parentClass = parentClass;
    }

    @Override
    public String toString() {
        return '?' + name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Variable other = (Variable) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
