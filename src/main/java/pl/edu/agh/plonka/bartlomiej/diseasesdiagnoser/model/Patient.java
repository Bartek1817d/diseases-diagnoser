package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.HashSet;
import java.util.Set;

/**
 * Model class for a Patient.
 *
 * @author Bartłomiej Płonka
 */
public class Patient extends Entity implements Comparable<Patient> {

    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final IntegerProperty age = new SimpleIntegerProperty(-1);
    private final IntegerProperty height = new SimpleIntegerProperty(-1);
    private final IntegerProperty weight = new SimpleIntegerProperty(-1);
    private final StringProperty placeOfResidence = new SimpleStringProperty();
    private final SetProperty<Entity> symptoms = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> inferredSymptoms = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> diseases = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> inferredDieseases = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> tests = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> inferredTests = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> treatments = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> inferredTreatments = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> causes = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> inferredCauses = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> negativeTests = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private final SetProperty<Entity> previousAndCurrentDiseases = new SimpleSetProperty<Entity>(
            FXCollections.observableSet(new HashSet<Entity>()));
    private float evaluation;

    public Patient() {
        super();
    }

    /**
     * Default constructor.
     */
    public Patient(String id) {
        super(id);
    }

    /**
     * Constructor with some initial data.
     *
     * @param firstName
     * @param lastName
     */
    public Patient(String id, String firstName, String lastName) {
        super(id);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
    }

    public float getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(float evaluation) {
        this.evaluation = evaluation;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public int getAge() {
        return age.get();
    }

    public void setAge(Integer age) {
        this.age.set(age);
    }

    public int getHeight() {
        return height.get();
    }

    public void setHeight(int height) {
        this.height.set(height);
    }

    public int getWeight() {
        return weight.get();
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
    }

    public String getPlaceOfResidence() {
        return placeOfResidence.get();
    }

    public void setPlaceOfResidence(String placeOfResidence) {
        this.placeOfResidence.set(placeOfResidence);
    }

    public Set<Entity> getSymptoms() {
        return symptoms.get();
    }

    public void setSymptoms(Set<Entity> symptoms) {
        this.symptoms.clear();
        this.symptoms.addAll(symptoms);
    }

    public void addSymptoms(Set<Entity> symptoms) {
        this.symptoms.addAll(symptoms);
    }

    public void addSymptom(Entity symptom) {
        this.symptoms.add(symptom);
    }

    public Set<Entity> getInferredSymptoms() {
        return inferredSymptoms.get();
    }

    public void setInferredSymptoms(Set<Entity> inferredSymptoms) {
        this.inferredSymptoms.clear();
        this.inferredSymptoms.addAll(inferredSymptoms);
    }

    public void addInferredSymptoms(Set<Entity> inferredSymptoms) {
        this.inferredSymptoms.addAll(inferredSymptoms);
    }

    public void addInferredSymptom(Entity inferredSymptom) {
        this.symptoms.add(inferredSymptom);
    }

    public void clearInferredSymptoms() {
        inferredSymptoms.clear();
    }

    public Set<Entity> getDiseases() {
        return diseases.get();
    }

    public void setDiseases(Set<Entity> diseases) {
        this.diseases.clear();
        this.diseases.addAll(diseases);
    }

    public void addDiseases(Set<Entity> diseases) {
        this.diseases.addAll(diseases);
    }

    public void addDisease(Entity disease) {
        this.diseases.add(disease);
    }

    public Set<Entity> getInferredDiseases() {
        return inferredDieseases.get();
    }

    public void setInferredDiseases(Set<Entity> inferredDieseases) {
        this.inferredDieseases.clear();
        this.inferredDieseases.addAll(inferredDieseases);
    }

    public void addInferredDiseases(Set<Entity> inferredDieseases) {
        this.diseases.addAll(diseases);
    }

    public void addInferredDisease(Entity inferredDiesease) {
        this.diseases.add(inferredDiesease);
    }

    public void clearInferredDiseases() {
        inferredDieseases.clear();
    }

    public Set<Entity> getTests() {
        return tests.get();
    }

    public void setTests(Set<Entity> tests) {
        this.tests.clear();
        this.tests.addAll(tests);
    }

    public void addTests(Set<Entity> tests) {
        this.tests.addAll(tests);
    }

    public void addTest(Entity test) {
        this.tests.add(test);
    }

    public Set<Entity> getInferredTests() {
        return inferredTests.get();
    }

    public void setInferredTests(Set<Entity> inferredTests) {
        this.inferredTests.clear();
        this.inferredTests.addAll(inferredTests);
    }

    public void addInferredTests(Set<Entity> inferredTests) {
        this.inferredTests.addAll(inferredTests);
    }

    public void addInfrredTest(Entity inferredTest) {
        this.inferredTests.add(inferredTest);
    }

    public void clearInferredTests() {
        inferredTests.clear();
    }

    public Set<Entity> getTreatments() {
        return treatments.get();
    }

    public void setTreatments(Set<Entity> treatments) {
        this.treatments.clear();
        this.treatments.addAll(treatments);
    }

    public void addTreatments(Set<Entity> treatments) {
        this.treatments.addAll(treatments);
    }

    public void addTreatment(Entity treatment) {
        this.treatments.add(treatment);
    }

    public Set<Entity> getInferredTreatments() {
        return inferredTreatments.get();
    }

    public void setInferredTreatments(Set<Entity> inferredTreatments) {
        this.inferredTreatments.clear();
        this.inferredTreatments.addAll(inferredTreatments);
    }

    public void addInferredTreatments(Set<Entity> inferredTreatments) {
        this.inferredTreatments.addAll(inferredTreatments);
    }

    public void addInferredTreatment(Entity inferredTreatment) {
        this.inferredTreatments.add(inferredTreatment);
    }

    public void clearInferredTreatments() {
        inferredTreatments.clear();
    }

    public Set<Entity> getCauses() {
        return causes.get();
    }

    public void setCauses(Set<Entity> causes) {
        this.causes.clear();
        this.causes.addAll(causes);
    }

    public void addCauses(Set<Entity> causes) {
        this.causes.addAll(causes);
    }

    public void addCause(Entity cause) {
        this.causes.add(cause);
    }

    public Set<Entity> getInferredCauses() {
        return inferredCauses.get();
    }

    public void setInferredCauses(Set<Entity> inferredCauses) {
        this.inferredCauses.clear();
        this.inferredCauses.addAll(inferredCauses);
    }

    public void addInferredCauses(Set<Entity> inferredCauses) {
        this.inferredCauses.addAll(inferredCauses);
    }

    public void addInferredCause(Entity inferredCause) {
        this.inferredCauses.add(inferredCause);
    }

    public Set<Entity> getNegativeTests() {
        return negativeTests.get();
    }

    public void setNegativeTests(Set<Entity> negativeTests) {
        this.negativeTests.clear();
        this.negativeTests.addAll(negativeTests);
    }

    public void addNegativeTests(Set<Entity> negativeTests) {
        this.negativeTests.addAll(negativeTests);
    }

    public void addNegativeTest(Entity negativeTest) {
        this.negativeTests.add(negativeTest);
    }

    public Set<Entity> getPreviousAndCurrentDiseases() {
        return previousAndCurrentDiseases.get();
    }

    public void setPreviousAndCurrentDiseases(Set<Entity> previousAndCurrentDiseases) {
        this.previousAndCurrentDiseases.clear();
        this.previousAndCurrentDiseases.addAll(previousAndCurrentDiseases);
    }

    public void addPreviousAndCurrentDiseases(Set<Entity> previousAndCurrentDiseases) {
        this.previousAndCurrentDiseases.addAll(previousAndCurrentDiseases);
    }

    public void addPreviousAndCurrentDiseases(Entity previousAndCurrentDisease) {
        this.previousAndCurrentDiseases.add(previousAndCurrentDisease);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Patient data:");
        str.append("\n\tFirst Name: ");
        str.append(getFirstName());
        str.append("\n\tLast Name: ");
        str.append(getLastName());
        str.append("\n\tAge: ");
        str.append(getAge());
        str.append("\n\nSymptoms:");
        for (Entity symptom : getSymptoms()) {
            str.append("\n\t");
            str.append(symptom.toString());
        }
        str.append("\n\nNegative tests:");
        for (Entity test : getNegativeTests()) {
            str.append("\n\t");
            str.append(test.toString());
        }
        str.append("\n\nPrevious and current diseases:");
        for (Entity disease : getPreviousAndCurrentDiseases()) {
            str.append("\n\t");
            str.append(disease.toString());
        }
        str.append("\n\nDiseases:");
        for (Entity disease : getDiseases()) {
            str.append("\n\t");
            str.append(disease.toString());
        }
        return str.toString();
    }

    /**
     * if evaluation1 > evaluation2 -> patient1 > patient2
     */
    @Override
    public int compareTo(Patient patient) {
        return (int) Math.signum(evaluation - patient.evaluation);
    }
}