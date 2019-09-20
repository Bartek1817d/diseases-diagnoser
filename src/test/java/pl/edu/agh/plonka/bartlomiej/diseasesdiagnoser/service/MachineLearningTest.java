package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.service;

import com.google.common.collect.Sets;
import javafx.collections.ObservableList;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.CreateRuleException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.PartialStarCreationException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.exception.RuleAlreadyExistsException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology.OntologyWrapper;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.Rule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Supplier;

import static com.google.common.collect.Sets.symmetricDifference;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.nanoTime;
import static org.slf4j.LoggerFactory.getLogger;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Constants.*;

public class MachineLearningTest {

    private static final Logger LOG = getLogger(MachineLearningTest.class);

    private static OntologyWrapper ontology;
    private static MachineLearning machineLearning;
    private static PatientsService patientsService;
    private static final Random RAND = new Random(currentTimeMillis());

    @BeforeClass
    public static void setUp() throws Exception {
        patientsService = new PatientsService(new File("src/test/resources/human_diseases.owl"));
        ontology = patientsService.getOntology();
        machineLearning = new MachineLearning(ontology);
    }

    @Test
    public void sequentialCovering() throws Exception {
        PrintWriter results = new PrintWriter(new FileOutputStream(new File("src/test/resources/results.csv")));
        results.println("n,time");
        int maxN = 150;
        for (int n = 1; n <= maxN; n++) {
            LOG.info(Integer.toString(n));
            Set<Patient> patients = generatePatients(n);
            long start = nanoTime();
            machineLearning.sequentialCovering(patients);
            long stop = nanoTime();
            results.println(format(Locale.US, "%d,%f", n, (float)(stop - start) / 1000000000));
        }
        results.close();
    }

    @Test
    public void validateLearningRules() throws Exception {
        for (int i = 1; i <= 10; i++) {
            validateLearningRules(i);
        }
    }

    private void validateLearningRules(int n) throws PartialStarCreationException, RuleAlreadyExistsException, CreateRuleException {
        Set<Patient> patients = generatePatients(n);
        patientsService.deleteRules(patientsService.getRules());
        patientsService.deletePatients(patientsService.getPatients());

        Collection<Rule> newRules = machineLearning.sequentialCovering(patients);
        patientsService.addRules(newRules);

        OptionalDouble average = patients.stream().map(this::calculateDifference).mapToDouble(d -> d).average();
        LOG.info("{}, {}", n, average.getAsDouble());
    }

    private double calculateDifference(Patient patient) {
        Set<Entity> oldDiseases = new HashSet<>(patient.getDiseases());
        Set<Entity> oldCauses = new HashSet<>(patient.getCauses());
        Set<Entity> oldTests = new HashSet<>(patient.getTests());
        Set<Entity> oldTreatments = new HashSet<>(patient.getTreatments());

        patient.removeAllDiseases();
        patient.removeAllCauses();
        patient.removeAllTests();
        patient.removeAllTreatments();

        ontology.addPatient(patient);
        Set<Entity> newDiseases = new HashSet<>(patient.getInferredDiseases());
        Set<Entity> newCauses = new HashSet<>(patient.getInferredCauses());
        Set<Entity> newTests = new HashSet<>(patient.getInferredTests());
        Set<Entity> newTreatments = new HashSet<>(patient.getInferredTreatments());

        double difference = 0;
        difference += (double) symmetricDifference(oldDiseases, newDiseases).size() * 100 / oldDiseases.size();
        difference += (double) symmetricDifference(oldCauses, newCauses).size() * 100 / oldCauses.size();
        difference += (double) symmetricDifference(oldTests, newTests).size() * 100 / oldTests.size();
        difference += (double) symmetricDifference(oldTreatments, newTreatments).size() * 100 / oldTreatments.size();

        return difference / 4;
    }

    private Set<Patient> generatePatients(int count) {
        Set<Patient> patients = new HashSet<>(count);
        for (int i = 0; i < count; i++) {
            patients.add(generatePatient("patient" + (i + 1)));
        }
        return patients;
    }

    private Patient generatePatient(String id) {
        Patient patient = new Patient(id);
        patient.setAge(PATIENT_MIN_AGE + RAND.nextInt(PATIENT_MAX_AGE - PATIENT_MIN_AGE));
        patient.setHeight(PATIENT_MIN_HEIGHT + RAND.nextInt(PATIENT_MAX_HEIGHT - PATIENT_MIN_HEIGHT));
        patient.setWeight(PATIENT_MIN_WEIGHT + RAND.nextInt(PATIENT_MAX_WEIGHT - PATIENT_MIN_WEIGHT));
        patient.setSymptoms(selectRandomSubset(ontology.getSymptoms().values()));
        patient.setCauses(selectRandomSubset(ontology.getCauses().values()));
        patient.setDiseases(selectRandomSubset(ontology.getDiseases().values()));
        patient.setNegativeTests(selectRandomSubset(ontology.getTests().values()));
        patient.setTreatments(selectRandomSubset(ontology.getTreatments().values()));
        patient.setPreviousAndCurrentDiseases(selectRandomSubset(ontology.getDiseases().values()));
        patient.setTests(selectRandomSubset(ontology.getTests().values()));
        return patient;
    }

    private Collection<Entity> selectRandomSubset(Collection<Entity> set) {
        List<Entity> list = new ArrayList<>(set);
        int size = RAND.nextInt(list.size()) + 1;
        Collection<Entity> subSet = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            int nextIndex = RAND.nextInt(list.size());
            subSet.add(list.get(nextIndex));
        }
        return subSet;
    }
}