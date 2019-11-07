package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.CreateRuleException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.RuleAlreadyExistsException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;

import java.io.File;
import java.util.Collection;

public class PatientsService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private OntologyWrapper ontology;
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private ObservableList<Rule> rules = FXCollections.observableArrayList();

    public PatientsService(String url) throws OWLOntologyCreationException {
        createKnowledgeBase(url);
    }

    public PatientsService(File file) throws OWLOntologyCreationException {
        createKnowledgeBase(file);
    }

    public void createKnowledgeBase(String url) throws OWLOntologyCreationException {
        ontology = new OntologyWrapper(url);
        patients.clear();
        rules.clear();
    }

    public void createKnowledgeBase(File file) throws OWLOntologyCreationException {
        ontology = new OntologyWrapper(file);
        patients.setAll(ontology.getPatients());
        rules.setAll(ontology.getRules());
    }

    public void saveKnowledgeBase(File file) throws OWLOntologyStorageException {
        ontology.saveOntologyToFile(file);
    }

    public ObservableList<Patient> getPatients() {
        return patients;
    }

    public void addPatient(Patient patient) {
        ontology.addPatient(patient);
        patients.add(patient);
    }

    public void addPatients(Collection<Patient> patients) {
        patients.forEach(p -> {
            ontology.addPatient(p);
            this.patients.add(p);
        });

    }

    public void deletePatient(Patient patient) {
        patients.remove(patient);
        ontology.deleteEntity(patient);
    }

    public void deletePatients(Collection<Patient> patients) {
        this.patients.removeAll(patients);
        ontology.deletePatients(patients);
    }

    public Collection<Patient> updatePatients(Collection<Patient> patients) {
        patients.forEach(p -> ontology.updatePatient(p));
        return patients;
    }

    public ObservableList<Rule> getRules() {
        return rules;
    }

    public void addRule(Rule rule) throws RuleAlreadyExistsException, CreateRuleException {
        if (rules.contains(rule))
            throw new RuleAlreadyExistsException(rule);
        ontology.addRule(rule);
        rules.add(rule);
        updatePatients(patients);
    }

    public void addRules(Collection<Rule> rules) throws RuleAlreadyExistsException, CreateRuleException {
        for (Rule rule : rules)
            addRule(rule);
    }

    public void deleteRule(Rule rule) {
        this.ontology.deleteRule(rule);
        this.rules.remove(rule);
        updatePatients(patients);
    }

    public void deleteRules(Collection<Rule> rules) {
        this.ontology.deleteRules(rules);
        this.rules.removeAll(rules);
        updatePatients(patients);
    }

    public void editPatient(Patient patient) {
        ontology.updatePatient(patient);
    }

    public OntologyWrapper getOntology() {
        return ontology;
    }

    public void changeLanguage(String language) {
        ontology.changeLanguage(language);
    }
}
