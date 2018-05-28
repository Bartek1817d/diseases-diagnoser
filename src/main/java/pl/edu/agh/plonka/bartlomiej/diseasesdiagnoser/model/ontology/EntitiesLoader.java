package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology;

import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;

import java.util.HashMap;
import java.util.Map;

class EntitiesLoader {

    private final OWLOntology ontology;
    private final OWLObjectRenderer renderer;
    private final OWLDataFactory factory;
    private String lang;

    EntitiesLoader(OWLOntology ontology, OWLObjectRenderer renderer, OWLDataFactory factory, String lang) {
        this.ontology = ontology;
        this.renderer = renderer;
        this.factory = factory;
        this.lang = lang;
    }

    Map<String, Entity> loadClasses() {
        Map<String, Entity> classes = new HashMap<>();
        for (OWLClass owlClass : ontology.getClassesInSignature()) {
            Entity classEntity = loadClass(owlClass, classes);

            for (OWLClassExpression owlSuperClass : EntitySearcher.getSuperClasses(owlClass, ontology)) {
                if (owlSuperClass.isAnonymous())
                    continue;
                Entity superClassEntity = loadClass(owlSuperClass.asOWLClass(), classes);
                classEntity.addClass(superClassEntity);
            }
        }
        return classes;
    }

    private Entity loadClass(OWLEntity owlClass, Map<String, Entity> classes) {
        String classID = renderer.render(owlClass);
        Entity classEntity = classes.get(classID);
        if (classEntity == null) {
            classEntity = new Entity(classID);
            classes.put(classID, classEntity);
        }

        if (classEntity.getLabel() == null) {
            classEntity.setLabel(getLabel(owlClass));
            classEntity.setComment(getComment(owlClass));
        }

        return classEntity;
    }

    private String getLabel(OWLEntity owlClass) {
        return getProperty(owlClass, factory.getRDFSLabel());
    }

    private String getComment(OWLEntity owlClass) {
        return getProperty(owlClass, factory.getRDFSComment());
    }

    private String getProperty(OWLEntity owlClass, OWLAnnotationProperty annotationProperty) {
        for (OWLAnnotation annotation : EntitySearcher.getAnnotations(owlClass, ontology,
                annotationProperty)) {
            OWLAnnotationValue val = annotation.getValue();
            if (val instanceof OWLLiteral) {
                OWLLiteral label = (OWLLiteral) val;
                if (label.hasLang(lang))
                    return label.getLiteral();
            }
        }
        return null;
    }
}


