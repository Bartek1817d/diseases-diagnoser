package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.OntologyWrapper;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;

public class PatientsService {

    private OntologyWrapper ontology;
    private ObservableList<Patient> patients;

    public PatientsService(OntologyWrapper ontology) {
        this.ontology = ontology;
        this.patients = FXCollections.observableList(ontology.getPatients());
    }

    public ObservableList<Patient> getPatients() {
        return patients;
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
        ontology.addPatient(patient);
    }

    public void deletePatient(Patient patient) {
        patients.remove(patient);
        ontology.deleteEntity(patient);
    }

    public void editPatient(Patient patient) {
        ontology.updatePatient(patient);
    }
}
