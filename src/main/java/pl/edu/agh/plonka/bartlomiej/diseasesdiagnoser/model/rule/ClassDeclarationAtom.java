package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;

public class ClassDeclarationAtom<T> extends OneArgumentAtom<T> {

    private Entity classEntity;

    public ClassDeclarationAtom(Entity classEntity) {
        this.classEntity = classEntity;
    }

    public ClassDeclarationAtom(Entity classEntity, String prefix) {
        this.classEntity = classEntity;
        this.prefix = prefix;
    }

    public ClassDeclarationAtom(Entity classEntity, T argument) {
        this.classEntity = classEntity;
        this.argument = argument;
    }

    public static void main(String[] args) {
        Entity e1 = new Entity("Entity");
        Entity c1 = new Entity("Class");
        AbstractAtom a1 = new ClassDeclarationAtom<Entity>(c1, e1);
        Entity e2 = new Entity("Entity");
        Entity c2 = new Entity("Class");
        //AbstractAtom a2 = new ClassDeclarationAtom<Entity>(c2, e2);

        System.out.println(a1);
    }

    public Entity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(Entity classEntity) {
        this.classEntity = classEntity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((classEntity == null) ? 0 : classEntity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        ClassDeclarationAtom other = (ClassDeclarationAtom) obj;
        if (classEntity == null) {
            if (other.classEntity != null)
                return false;
        } else if (!classEntity.equals(other.classEntity))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(prefix);
        str.append(':');
        str.append(classEntity.getID());
        str.append('(');
        if (argument instanceof Entity) {
            str.append(prefix);
            str.append(':');
            str.append(((Entity) argument).getID());
        } else
            str.append(argument);
        str.append(')');
        return str.toString();
    }
}
