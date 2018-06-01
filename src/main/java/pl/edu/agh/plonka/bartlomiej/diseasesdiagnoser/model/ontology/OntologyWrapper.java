package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.ontology;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.google.common.collect.Range;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swrlapi.core.SWRLAPIOWLOntology;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.core.SWRLRuleRenderer;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Entity;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.Patient;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.*;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplPlain;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.BoundType.OPEN;

public class OntologyWrapper {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final Integer MIN_AGE = 0;
    private static final Integer MAX_AGE = 100;

    private static final Pattern diseasePattern = Pattern.compile("(?<diseaseID>\\w+)Disease(?<number>\\d+)");
    private static final Random random = new Random();


    private final OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
    private final EntitiesLoader entitiesLoader;
    private final RulesLoader rulesLoader;
    private Map<String, Entity> classes = new HashMap<>();
    private final OWLOntology ontology;
    private final OWLDataFactory factory;
    private final PrefixManager prefixManager;
    private final OWLDocumentFormat ontologyFormat;
    private final OWLOntologyManager ontologyManager;
    private final OWLReasoner reasoner;
    private final SWRLRuleEngine ruleEngine;
    private final SWRLAPIOWLOntology ruleOntology;
    private final SWRLRuleRenderer ruleRenderer;
    private OWLEntityRemover remover;
    private Map<String, Entity> symptoms = new HashMap<>();
    private Map<String, Entity> diseases = new HashMap<>();
    private Map<String, Entity> tests = new HashMap<>();
    private Map<String, Entity> treatments = new HashMap<>();
    private Map<String, Entity> causes = new HashMap<>();
    private Map<String, Rule> rules = new HashMap<>();
    private String lang = "en";
    private OntologyProperties properties;

    public OntologyWrapper(String baseURL) throws OWLOntologyCreationException {
        ontologyManager = OWLManager.createOWLOntologyManager();
        factory = ontologyManager.getOWLDataFactory();
        ontology = ontologyManager.createOntology(IRI.create(baseURL));
        ontologyFormat = ontologyManager.getOntologyFormat(ontology);
        prefixManager = ontologyFormat.asPrefixOWLOntologyFormat();
        prefixManager.setDefaultPrefix(baseURL + "#");
        OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());
        remover = new OWLEntityRemover(Collections.singleton(ontology));
        ruleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);
        ruleOntology = ruleEngine.getSWRLAPIOWLOntology();
        ruleRenderer = ruleOntology.createSWRLRuleRenderer();
        properties = new OntologyProperties(factory, prefixManager);
        entitiesLoader = new EntitiesLoader(ontology, renderer, factory, reasoner, lang);
        rulesLoader = new RulesLoader(ruleOntology);
    }

    public OntologyWrapper(File ontologyFile) throws OWLOntologyCreationException {
        ontologyManager = OWLManager.createOWLOntologyManager();
        factory = ontologyManager.getOWLDataFactory();
        ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);
        ontologyFormat = ontologyManager.getOntologyFormat(ontology);
        prefixManager = ontologyFormat.asPrefixOWLOntologyFormat();
        String baseURL = ontology.getOntologyID().getOntologyIRI().get().toString();
        prefixManager.setDefaultPrefix(baseURL + "#");
        OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());
        remover = new OWLEntityRemover(Collections.singleton(ontology));
        ruleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);
        ruleOntology = ruleEngine.getSWRLAPIOWLOntology();
        ruleRenderer = ruleOntology.createSWRLRuleRenderer();
        properties = new OntologyProperties(factory, prefixManager);
        entitiesLoader = new EntitiesLoader(ontology, renderer, factory, reasoner, lang);
        rulesLoader = new RulesLoader(ruleOntology);
        loadData();
    }

    public static void main(String args[])
            throws OWLOntologyCreationException, SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        OntologyWrapper ontology = new OntologyWrapper(new File("res/human_diseases.owl"));
        System.out.println(ontology.generatePatientsFromRules());
    }

    public OWLDocumentFormat getOntologyFormat() {
        return ontologyFormat;
    }

    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLReasoner getReasoner() {
        return reasoner;
    }

    public PrefixManager getPrefixManager() {
        return prefixManager;
    }

    private void loadData() {
        classes = entitiesLoader.loadClasses();
        symptoms = entitiesLoader.loadInstances(properties.symptomClass, classes);
        diseases = entitiesLoader.loadInstances(properties.diseaseClass, classes);
        tests = entitiesLoader.loadInstances(properties.testingClass, classes);
        treatments = entitiesLoader.loadInstances(properties.treatmentClass, classes);
        causes = entitiesLoader.loadInstances(properties.causeClass, classes);
        rules = rulesLoader.loadRules(classes, symptoms, diseases, tests, treatments, causes);
    }

    public Map<String, Entity> getClasses() {
        return classes;
    }


    public Map<String, Entity> getSymptoms() {
        return symptoms;
    }

    public Map<String, Entity> getDiseases() {
        return diseases;
    }

    public Map<String, Entity> getTests() {
        return tests;
    }

    public Map<String, Entity> getTreatments() {
        return treatments;
    }

    public Map<String, Entity> getCauses() {
        return causes;
    }

    private Patient getPatient(OWLIndividual patientInd) {
        Patient patient = new Patient(renderer.render(patientInd));

        setPatientStringProperty(patientInd, properties.firstNameProperty, patient::setFirstName);
        setPatientStringProperty(patientInd, properties.lastNameProperty, patient::setLastName);
        setPatientIntegerProperty(patientInd, properties.ageProperty, patient::setAge);
        setPatientIntegerProperty(patientInd, properties.heightProperty, patient::setHeight);
        setPatientIntegerProperty(patientInd, properties.weightProperty, patient::setWeight);

        setPatientObjectProperty(patientInd, properties.symptomProperty, symptoms, patient::addSymptom);
        setPatientObjectProperty(patientInd, properties.diseaseProperty, diseases, patient::addDisease);
        setPatientObjectProperty(patientInd, properties.testProperty, tests, patient::addTest);
        setPatientObjectProperty(patientInd, properties.negativeTestProperty, tests, patient::addNegativeTest);
        setPatientObjectProperty(patientInd, properties.treatmentProperty, treatments, patient::addTreatment);
        setPatientObjectProperty(patientInd, properties.causeProperty, causes, patient::addCause);
        setPatientObjectProperty(patientInd, properties.previousOrCurrentDiseaseProperty, diseases, patient::addPreviousOrCurrentDisease);

        return patient;
    }

    private Patient getInferredPatient(Patient patient) {
        reasoner.flush();
        OWLNamedIndividual patientInd = factory.getOWLNamedIndividual(patient.getID(), prefixManager);
        Set<Entity> inferredSymptoms = new HashSet<Entity>();
        for (OWLNamedIndividual symptomInd : reasoner.getObjectPropertyValues(patientInd, properties.symptomProperty)
                .getFlattened()) {
            inferredSymptoms.add(symptoms.get(renderer.render(symptomInd)));
        }
        inferredSymptoms.removeAll(patient.getSymptoms());
        patient.setInferredSymptoms(inferredSymptoms);

        Set<Entity> inferredDiseases = new HashSet<Entity>();
        for (OWLNamedIndividual diseaseInd : reasoner.getObjectPropertyValues(patientInd, properties.diseaseProperty)
                .getFlattened()) {
            inferredDiseases.add(diseases.get(renderer.render(diseaseInd)));
        }
        inferredDiseases.removeAll(patient.getDiseases());
        patient.setInferredDiseases(inferredDiseases);

        Set<Entity> inferredTests = new HashSet<Entity>();
        for (OWLNamedIndividual testInd : reasoner.getObjectPropertyValues(patientInd, properties.testProperty).getFlattened()) {
            inferredTests.add(tests.get(renderer.render(testInd)));
        }
        inferredTests.removeAll(patient.getTests());
        patient.setInferredTests(inferredTests);

        Set<Entity> inferredTreatments = new HashSet<Entity>();
        for (OWLNamedIndividual treatmentInd : reasoner.getObjectPropertyValues(patientInd, properties.treatmentProperty)
                .getFlattened()) {
            inferredTreatments.add(treatments.get(renderer.render(treatmentInd)));
        }
        inferredTreatments.removeAll(patient.getTreatments());
        patient.setInferredTreatments(inferredTreatments);

        Set<Entity> inferredCauses = new HashSet<Entity>();
        for (OWLNamedIndividual causeInd : reasoner.getObjectPropertyValues(patientInd, properties.causeProperty).getFlattened()) {
            inferredCauses.add(causes.get(renderer.render(causeInd)));
        }
        inferredCauses.removeAll(patient.getCauses());
        patient.setInferredCauses(inferredCauses);

        return patient;
    }

    public void addPatient(Patient patient) {
        OWLNamedIndividual patientInd = factory.getOWLNamedIndividual(patient.getID(), prefixManager);
        // patient class assertion
        ontologyManager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(properties.patientClass, patientInd));
        if (patient.getFirstName() != null)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(properties.firstNameProperty, patientInd, patient.getFirstName()));
        if (patient.getLastName() != null)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(properties.lastNameProperty, patientInd, patient.getLastName()));
        if (patient.getAge() > 0)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(properties.ageProperty, patientInd, patient.getAge()));
        if (patient.getHeight() > 0)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(properties.heightProperty, patientInd, patient.getHeight()));
        if (patient.getWeight() > 0)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(properties.weightProperty, patientInd, patient.getWeight()));
        for (Entity symptom : patient.getSymptoms())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(properties.symptomProperty, patientInd,
                    factory.getOWLNamedIndividual(symptom.getID(), prefixManager)));
        for (Entity disease : patient.getDiseases())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(properties.diseaseProperty, patientInd,
                    factory.getOWLNamedIndividual(disease.getID(), prefixManager)));
        for (Entity test : patient.getTests())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(properties.testProperty, patientInd,
                    factory.getOWLNamedIndividual(test.getID(), prefixManager)));
        for (Entity test : patient.getNegativeTests())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(properties.negativeTestProperty,
                    patientInd, factory.getOWLNamedIndividual(test.getID(), prefixManager)));
        for (Entity treatment : patient.getTreatments())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(properties.treatmentProperty, patientInd,
                    factory.getOWLNamedIndividual(treatment.getID(), prefixManager)));
        for (Entity cause : patient.getCauses())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(properties.causeProperty, patientInd,
                    factory.getOWLNamedIndividual(cause.getID(), prefixManager)));
        for (Entity disease : patient.getPreviousAndCurrentDiseases())
            ontologyManager.addAxiom(ontology,
                    factory.getOWLObjectPropertyAssertionAxiom(properties.previousOrCurrentDiseaseProperty, patientInd,
                            factory.getOWLNamedIndividual(disease.getID(), prefixManager)));
    }

    public void addEntity(Entity ind) {
        OWLNamedIndividual owlInd = factory.getOWLNamedIndividual(ind.getID(), prefixManager);
        for (Entity cls : ind.getClasses()) {
            OWLClass owlClass = factory.getOWLClass(cls.getID(), prefixManager);
            ontologyManager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(owlClass, owlInd));
        }
        ontologyManager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(factory.getRDFSLabel(),
                owlInd.getIRI(), new OWLLiteralImplPlain(ind.getLabel(), lang)));
        if (ind.getComment() != null)
            ontologyManager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(factory.getRDFSComment(),
                    owlInd.getIRI(), new OWLLiteralImplPlain(ind.getComment(), lang)));

    }

    public List<Patient> getPatients() {
        List<Patient> patients = new ArrayList<>();
        Patient patient;
        for (OWLIndividual patientInd : EntitySearcher.getIndividuals(properties.patientClass, ontology)) {
            patient = getPatient(patientInd);
            patient = getInferredPatient(patient);
            patients.add(patient);
        }
        patients.addAll(generatePatientsFromRules());
        return patients;
    }

    public void deleteEntity(Entity entity) {
        OWLNamedIndividual entityID = factory.getOWLNamedIndividual(entity.getID(), prefixManager);
        entityID.accept(remover);
        ontologyManager.applyChanges(remover.getChanges());
        remover.reset();
    }

    public void deleteEntities(Collection<Entity> entities) {
        for (Entity entity : entities)
            deleteEntity(entity);
    }

    public Patient updatePatient(Patient patient) {
        deleteEntity(patient);
        addPatient(patient);
        return getInferredPatient(patient);
    }

    public void saveOntologyToFile(File file) throws OWLOntologyStorageException {
        ontologyManager.saveOntology(ontology, ontologyFormat, IRI.create(file));
    }

    public boolean containsID(String id) {
        return ontology.containsEntityInSignature(IRI.create(prefixManager.getDefaultPrefix(), id));
    }

    public Map<String, Rule> getRules() {
        return rules;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Collection<Patient> generatePatientsFromRules() {
        Collection<Patient> patients = new ArrayList<>();
        for (Rule rule : rules.values()) {
            Patient patient = generatePatientFromRule(rule);
            if (patient != null)
                patients.add(patient);
        }
        return patients;
    }

    private Patient generatePatientFromRule(Rule rule) {
        Matcher diseaseMatcher = diseasePattern.matcher(rule.getName());
        if (diseaseMatcher.find()) {
            // System.out.println(rule);
            String diseaseID = diseaseMatcher.group("diseaseID");
            String number = diseaseMatcher.group("number");
            Map<String, Entity> variables = new HashMap<>();
            Patient patient = parseVariables(rule, diseaseID, number, variables);
            parseRuleBodyAtoms(rule, variables);
            parseRuleHeadAtoms(rule, variables);
            return patient;
        } else
            return null;
    }

    private Patient parseVariables(Rule rule, String diseaseID, String number, Map<String, Entity> variables) {
        Patient patient = null;
        for (AbstractAtom atom : rule.getBodyAtoms()) {
            if (atom instanceof ClassDeclarationAtom
                    && ((ClassDeclarationAtom) atom).getArgument() instanceof Variable) {
                Variable var = (Variable) (((ClassDeclarationAtom) atom).getArgument());
                if (((ClassDeclarationAtom) atom).getClassEntity().equals(classes.get("Patient"))) {
                    patient = new Patient(diseaseID + number, diseaseID, number);
                    variables.put(var.getName(), patient);
                } else
                    variables.put(var.getName(), var.getParentClass());
            }
        }

        return patient;
    }

    private void parseRuleBodyAtoms(Rule rule, Map<String, Entity> variables) {
        for (AbstractAtom atom : rule.getBodyAtoms()) {
            if (atom instanceof TwoArgumentsAtom) {
                TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) atom;
                String predicate = twoArgumentsAtom.getPredicate();
                switch (predicate) {
                    case "hasSymptom": {
                        addEntityToPatient(twoArgumentsAtom, variables, "addSymptom");
                        break;
                    }
                    case "negativeTest": {
                        addEntityToPatient(twoArgumentsAtom, variables, "addNegativeTest");
                        break;
                    }
                    case "hadOrHasDisease": {
                        addEntityToPatient(twoArgumentsAtom, variables, "addPreviousOrCurrentDisease");
                        break;
                    }
                    case "hasDisease": {
                        addEntityToPatient(twoArgumentsAtom, variables, "addDisease");
                        break;
                    }
                    case "age": {
                        setPatientAge(rule, twoArgumentsAtom, variables);
                        break;
                    }
                }

            }
        }
    }

    private void parseRuleHeadAtoms(Rule rule, Map<String, Entity> variables) {
        for (AbstractAtom atom : rule.getHeadAtoms()) {
            if (atom instanceof TwoArgumentsAtom) {
                TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) atom;
                String predicate = twoArgumentsAtom.getPredicate();
                if (predicate.equals("hasDisease")) {
                    String pName = ((Variable) twoArgumentsAtom.getArgument1()).getName();
                    Patient p = (Patient) variables.get(pName);
                    p.addDisease((Entity) twoArgumentsAtom.getArgument2());
                }
            }
        }
    }

    private void addEntityToPatient(TwoArgumentsAtom atom, Map<String, Entity> variables, String patientMethod) {
        try {
            addEntityToPatient(atom, variables, Patient.class.getMethod(patientMethod, Entity.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void addEntityToPatient(TwoArgumentsAtom atom, Map<String, Entity> variables, Method patientMethod) {
        String pName = ((Variable) atom.getArgument1()).getName();
        Patient p = (Patient) variables.get(pName);
        try {
            patientMethod.invoke(p, (Entity) atom.getArgument2());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setPatientAge(Rule rule, TwoArgumentsAtom atom, Map<String, Entity> variables) {
        Variable patientVariable = (Variable) atom.getArgument1();
        Variable ageVariable = (Variable) atom.getArgument2();
        Patient p = (Patient) variables.get(patientVariable.getName());
        Range<Integer> ageRange = calculateAgeRange(rule, ageVariable);
        p.setAge(selectNumberFromRange(ageRange));
    }

    private Range<Integer> calculateAgeRange(Rule rule, Variable ageVariable) {
        Range<Integer> ageRange = Range.all();
        for (AbstractAtom atom : rule.getBodyAtoms()) {
            if (atom instanceof TwoArgumentsAtom) {
                TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) atom;
                if (atom.getPrefix().equals("swrlb")
                        && twoArgumentsAtom.getArgument1().equals(ageVariable)
                        && twoArgumentsAtom.getArgument2() instanceof Integer) {
                    int ageBound = (int) twoArgumentsAtom.getArgument2();
                    ageRange = intersection(ageRange, atom.getPredicate(), ageBound);
                }
            }
        }
        return ageRange;
    }

    private Range<Integer> intersection(Range<Integer> range, String operator, Integer bound) {
        switch (operator) {
            case "equal":
                return range.intersection(Range.singleton(bound));
            case "greaterThan":
                return range.intersection(Range.greaterThan(bound));
            case "greaterThanOrEqual":
                return range.intersection(Range.atLeast(bound));
            case "lessThan":
                return range.intersection(Range.lessThan(bound));
            case "lessThanOrEqual":
                return range.intersection(Range.atMost(bound));
            default:
                return range;
        }
    }

    private Integer selectNumberFromRange(Range<Integer> range) {
        if (range.hasUpperBound() && range.hasLowerBound()) {
            if (range.upperBoundType() == OPEN && range.lowerBoundType() == OPEN && range.upperEndpoint() - range.lowerEndpoint() <= 1)
                return null;
            if (range.upperEndpoint().equals(range.lowerEndpoint()))
                return range.upperEndpoint();
        }
        int lowerBound = MIN_AGE;
        int upperBound = MAX_AGE;
        if (range.hasUpperBound()) {
            switch (range.upperBoundType()) {
                case OPEN:
                    upperBound = range.upperEndpoint() - 1;
                    break;
                case CLOSED:
                    upperBound = range.upperEndpoint();
            }
        }
        if (range.hasLowerBound()) {
            switch (range.lowerBoundType()) {
                case OPEN:
                    lowerBound = range.lowerEndpoint() + 1;
                    break;
                case CLOSED:
                    lowerBound = range.lowerEndpoint();
            }
        }
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

    private void setPatientStringProperty(OWLIndividual patientInd, OWLDataProperty property, Consumer<String> setter) {
        Iterator<OWLLiteral> it = EntitySearcher.getDataPropertyValues(patientInd, property, ontology).iterator();
        if (it.hasNext())
            setter.accept(renderer.render(it.next()));
    }

    private void setPatientIntegerProperty(OWLIndividual patientInd, OWLDataProperty property, Consumer<Integer> setter) {
        Iterator<OWLLiteral> it = EntitySearcher.getDataPropertyValues(patientInd, property, ontology).iterator();
        if (it.hasNext())
            setter.accept(Integer.parseInt(renderer.render(it.next())));
    }

    private void setPatientObjectProperty(OWLIndividual patientInd, OWLObjectProperty property, Map<String, Entity> entities, Consumer<Entity> setter) {
        for (OWLIndividual entityInd : EntitySearcher.getObjectPropertyValues(patientInd, property, ontology)) {
            Entity entity = entities.get(renderer.render(entityInd));
            if (entity != null)
                setter.accept(entity);
        }
    }
}
