package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.google.common.collect.Range;
import org.apache.commons.lang3.StringUtils;
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
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.core.SWRLRuleRenderer;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.model.rule.*;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplPlain;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OntologyWrapper {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
    private final Map<String, Entity> classes = new HashMap<String, Entity>();
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
    private Map<String, Entity> symptoms = new HashMap<String, Entity>();
    private Map<String, Entity> diseases = new HashMap<String, Entity>();
    private Map<String, Entity> tests = new HashMap<String, Entity>();
    private Map<String, Entity> treatments = new HashMap<String, Entity>();
    private Map<String, Entity> causes = new HashMap<String, Entity>();
    private Map<String, Rule> rules = new HashMap<String, Rule>();
    private String lang = "en";
    private OWLDataProperty firstNameProperty;
    private OWLDataProperty lastNameProperty;
    private OWLDataProperty ageProperty;
    private OWLDataProperty heightProperty;
    private OWLDataProperty weightProperty;

    private OWLObjectProperty symptomProperty;
    private OWLObjectProperty diseaseProperty;
    private OWLObjectProperty testProperty;
    private OWLObjectProperty negativeTestProperty;
    private OWLObjectProperty treatmentProperty;
    private OWLObjectProperty causeProperty;
    private OWLObjectProperty previousOrCurrentDiseaseProperty;

    private OWLClass diseaseClass;
    private OWLClass symptomClass;
    private OWLClass causeClass;
    private OWLClass treatmentClass;
    private OWLClass testingClass;
    private OWLClass patientClass;

    private Collection<Patient> assertedPatients = new HashSet<Patient>();

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
        initProperties();
        initOWLClasses();
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
        initProperties();
        initOWLClasses();
        loadData();
    }

    public static void main(String args[])
            throws OWLOntologyCreationException, SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        OntologyWrapper ontology = new OntologyWrapper(new File("res/human_diseases.owl"));
        ontology.loadRules();
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

    private void initProperties() {
        firstNameProperty = factory.getOWLDataProperty("firstName", prefixManager);
        lastNameProperty = factory.getOWLDataProperty("lastName", prefixManager);
        ageProperty = factory.getOWLDataProperty("age", prefixManager);
        heightProperty = factory.getOWLDataProperty("height", prefixManager);
        weightProperty = factory.getOWLDataProperty("weight", prefixManager);
        symptomProperty = factory.getOWLObjectProperty("hasSymptom", prefixManager);
        diseaseProperty = factory.getOWLObjectProperty("hasDisease", prefixManager);
        testProperty = factory.getOWLObjectProperty("shouldMakeTest", prefixManager);
        negativeTestProperty = factory.getOWLObjectProperty("negativeTest", prefixManager);
        treatmentProperty = factory.getOWLObjectProperty("shouldBeTreatedWith", prefixManager);
        causeProperty = factory.getOWLObjectProperty("causeOfDisease", prefixManager);
        previousOrCurrentDiseaseProperty = factory.getOWLObjectProperty("hadOrHasDisease", prefixManager);
    }

    private void initOWLClasses() {
        diseaseClass = factory.getOWLClass("Disease", prefixManager);
        symptomClass = factory.getOWLClass("Symptom", prefixManager);
        causeClass = factory.getOWLClass("Cause", prefixManager);
        treatmentClass = factory.getOWLClass("Treatment", prefixManager);
        testingClass = factory.getOWLClass("Testing", prefixManager);
        patientClass = factory.getOWLClass("Patient", prefixManager);
    }

    private void loadData() {
        loadClasses();
        loadSymptoms();
        loadDiseases();
        loadTests();
        loadTreatments();
        loadCauses();
        loadRules();
        generatePatientsFromRules();
    }

    public Map<String, Entity> loadClasses() {
        for (OWLClass owlClass : ontology.getClassesInSignature()) {
            String classID = renderer.render(owlClass);
            Entity classEntity = classes.get(classID);
            if (classEntity == null) {
                classEntity = new Entity(classID);
                classes.put(classID, classEntity);
            }
            if (classEntity.getLabel() == null) {
                for (OWLAnnotation annotation : EntitySearcher.getAnnotations(owlClass, ontology,
                        factory.getRDFSLabel())) {
                    OWLAnnotationValue val = annotation.getValue();
                    if (val instanceof OWLLiteral) {
                        OWLLiteral label = (OWLLiteral) val;
                        if (label.hasLang(lang))
                            classEntity.setLabel(label.getLiteral());
                    }
                }
                for (OWLAnnotation annotation : EntitySearcher.getAnnotations(owlClass, ontology,
                        factory.getRDFSComment())) {
                    OWLAnnotationValue val = annotation.getValue();
                    if (val instanceof OWLLiteral) {
                        OWLLiteral comment = (OWLLiteral) val;
                        if (comment.hasLang(lang))
                            classEntity.setComment(comment.getLiteral());
                    }
                }
            }
            for (OWLClassExpression owlSuperClass : EntitySearcher.getSuperClasses(owlClass, ontology)) {
                if (owlSuperClass.isAnonymous())
                    continue;
                String superClassID = renderer.render(owlSuperClass);
                Entity superClassEntity = classes.get(superClassID);
                if (superClassEntity == null) {
                    superClassEntity = new Entity(superClassID);
                    classes.put(superClassID, superClassEntity);
                }
                classEntity.addClass(superClassEntity);
            }
        }
        return classes;
    }

    public Map<String, Entity> getClasses() {
        return classes;
    }

    private Map<String, Entity> loadInstances(OWLClass owlClass) {
        Map<String, Entity> instances = new HashMap<String, Entity>();
        for (OWLNamedIndividual owlInstance : reasoner.getInstances(owlClass, false).getFlattened()) {
            String instanceID = renderer.render(owlInstance);
            Entity instance = new Entity(instanceID);
            for (OWLClassExpression owlParentClass : EntitySearcher.getTypes(owlInstance, ontology))
                instance.addClass(classes.get(renderer.render(owlParentClass)));
            for (OWLAnnotation annotation : EntitySearcher.getAnnotations(owlInstance, ontology,
                    factory.getRDFSLabel())) {
                OWLAnnotationValue val = annotation.getValue();
                if (val instanceof OWLLiteral) {
                    OWLLiteral label = (OWLLiteral) val;
                    if (label.hasLang(lang))
                        instance.setLabel(label.getLiteral());
                }
            }
            for (OWLAnnotation annotation : EntitySearcher.getAnnotations(owlInstance, ontology,
                    factory.getRDFSComment())) {
                OWLAnnotationValue val = annotation.getValue();
                if (val instanceof OWLLiteral) {
                    OWLLiteral comment = (OWLLiteral) val;
                    if (comment.hasLang(lang))
                        instance.setComment(comment.getLiteral());
                }
            }
            instances.put(instanceID, instance);
        }
        return instances;
    }

    public Map<String, Entity> loadSymptoms() {
        symptoms = loadInstances(symptomClass);
        return symptoms;
    }

    public Map<String, Entity> getSymptoms() {
        return symptoms;
    }

    public Map<String, Entity> loadDiseases() {
        diseases = loadInstances(diseaseClass);
        return diseases;
    }

    public Map<String, Entity> getDiseases() {
        return diseases;
    }

    public Map<String, Entity> loadTests() {
        tests = loadInstances(testingClass);
        return tests;
    }

    public Map<String, Entity> getTests() {
        return tests;
    }

    public Map<String, Entity> loadTreatments() {
        treatments = loadInstances(treatmentClass);
        return treatments;
    }

    public Map<String, Entity> getTreatments() {
        return treatments;
    }

    public Map<String, Entity> loadCauses() {
        causes = loadInstances(causeClass);
        return causes;
    }

    public Map<String, Entity> getCauses() {
        return causes;
    }

    public Set<OWLNamedIndividual> getClassInstances(String classID) {
        OWLDataFactory factory = ontologyManager.getOWLDataFactory();
        OWLClass owlClass = factory.getOWLClass(classID, prefixManager);
        reasoner.flush();
        return reasoner.getInstances(owlClass, true).getFlattened();
    }

    private Patient getPatient(OWLIndividual patientInd) {
        Patient patient = new Patient(renderer.render(patientInd));
        Iterator<OWLLiteral> it;
        it = EntitySearcher.getDataPropertyValues(patientInd, firstNameProperty, ontology).iterator();
        if (it.hasNext())
            patient.setFirstName(renderer.render(it.next()));
        it = EntitySearcher.getDataPropertyValues(patientInd, lastNameProperty, ontology).iterator();
        if (it.hasNext())
            patient.setLastName(renderer.render(it.next()));
        it = EntitySearcher.getDataPropertyValues(patientInd, ageProperty, ontology).iterator();
        if (it.hasNext())
            patient.setAge(Integer.parseInt(renderer.render(it.next())));
        it = EntitySearcher.getDataPropertyValues(patientInd, heightProperty, ontology).iterator();
        if (it.hasNext())
            patient.setHeight(Integer.parseInt(renderer.render(it.next())));
        it = EntitySearcher.getDataPropertyValues(patientInd, weightProperty, ontology).iterator();
        if (it.hasNext())
            patient.setWeight(Integer.parseInt(renderer.render(it.next())));

        for (OWLIndividual owlSymptom : EntitySearcher.getObjectPropertyValues(patientInd, symptomProperty, ontology)) {
            Entity symptom = symptoms.get(renderer.render(owlSymptom));
            if (symptom != null)
                patient.addSymptom(symptom);
        }
        for (OWLIndividual owlDisease : EntitySearcher.getObjectPropertyValues(patientInd, diseaseProperty, ontology)) {
            Entity disease = diseases.get(renderer.render(owlDisease));
            if (disease != null)
                patient.addDisease(disease);
        }
        for (OWLIndividual owlTest : EntitySearcher.getObjectPropertyValues(patientInd, testProperty, ontology)) {
            Entity test = tests.get(renderer.render(owlTest));
            if (test != null)
                patient.addTest(test);
        }
        for (OWLIndividual owlTest : EntitySearcher.getObjectPropertyValues(patientInd, negativeTestProperty,
                ontology)) {
            Entity test = tests.get(renderer.render(owlTest));
            if (test != null)
                patient.addNegativeTest(test);
        }
        for (OWLIndividual owlTreatment : EntitySearcher.getObjectPropertyValues(patientInd, treatmentProperty,
                ontology)) {
            Entity treatment = treatments.get(renderer.render(owlTreatment));
            if (treatment != null)
                patient.addTreatment(treatment);
        }
        for (OWLIndividual owlCause : EntitySearcher.getObjectPropertyValues(patientInd, causeProperty, ontology)) {
            Entity cause = causes.get(renderer.render(owlCause));
            if (cause != null)
                patient.addTreatment(cause);
        }
        for (OWLIndividual owlCause : EntitySearcher.getObjectPropertyValues(patientInd,
                previousOrCurrentDiseaseProperty, ontology)) {
            Entity disease = diseases.get(renderer.render(owlCause));
            if (disease != null)
                patient.addPreviousAndCurrentDiseases(disease);
        }
        return patient;
    }

    private Patient getInferredPatient(Patient patient) {
        reasoner.flush();
        OWLNamedIndividual patientInd = factory.getOWLNamedIndividual(patient.getID(), prefixManager);
        Set<Entity> inferredSymptoms = new HashSet<Entity>();
        for (OWLNamedIndividual symptomInd : reasoner.getObjectPropertyValues(patientInd, symptomProperty)
                .getFlattened()) {
            inferredSymptoms.add(symptoms.get(renderer.render(symptomInd)));
        }
        inferredSymptoms.removeAll(patient.getSymptoms());
        patient.setInferredSymptoms(inferredSymptoms);

        Set<Entity> inferredDiseases = new HashSet<Entity>();
        for (OWLNamedIndividual diseaseInd : reasoner.getObjectPropertyValues(patientInd, diseaseProperty)
                .getFlattened()) {
            inferredDiseases.add(diseases.get(renderer.render(diseaseInd)));
        }
        inferredDiseases.removeAll(patient.getDiseases());
        patient.setInferredDiseases(inferredDiseases);

        Set<Entity> inferredTests = new HashSet<Entity>();
        for (OWLNamedIndividual testInd : reasoner.getObjectPropertyValues(patientInd, testProperty).getFlattened()) {
            inferredTests.add(tests.get(renderer.render(testInd)));
        }
        inferredTests.removeAll(patient.getTests());
        patient.setInferredTests(inferredTests);

        Set<Entity> inferredTreatments = new HashSet<Entity>();
        for (OWLNamedIndividual treatmentInd : reasoner.getObjectPropertyValues(patientInd, treatmentProperty)
                .getFlattened()) {
            inferredTreatments.add(treatments.get(renderer.render(treatmentInd)));
        }
        inferredTreatments.removeAll(patient.getTreatments());
        patient.setInferredTreatments(inferredTreatments);

        Set<Entity> inferredCauses = new HashSet<Entity>();
        for (OWLNamedIndividual causeInd : reasoner.getObjectPropertyValues(patientInd, causeProperty).getFlattened()) {
            inferredCauses.add(causes.get(renderer.render(causeInd)));
        }
        inferredCauses.removeAll(patient.getCauses());
        patient.setInferredCauses(inferredCauses);

        return patient;
    }

    public void addPatient(Patient patient) {
        OWLNamedIndividual patientInd = factory.getOWLNamedIndividual(patient.getID(), prefixManager);
        // patient class assertion
        ontologyManager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(patientClass, patientInd));
        if (patient.getFirstName() != null)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(firstNameProperty, patientInd, patient.getFirstName()));
        if (patient.getLastName() != null)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(lastNameProperty, patientInd, patient.getLastName()));
        if (patient.getAge() > 0)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(ageProperty, patientInd, patient.getAge()));
        if (patient.getHeight() > 0)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(heightProperty, patientInd, patient.getHeight()));
        if (patient.getWeight() > 0)
            ontologyManager.addAxiom(ontology,
                    factory.getOWLDataPropertyAssertionAxiom(weightProperty, patientInd, patient.getWeight()));
        for (Entity symptom : patient.getSymptoms())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(symptomProperty, patientInd,
                    factory.getOWLNamedIndividual(symptom.getID(), prefixManager)));
        for (Entity disease : patient.getDiseases())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(diseaseProperty, patientInd,
                    factory.getOWLNamedIndividual(disease.getID(), prefixManager)));
        for (Entity test : patient.getTests())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(testProperty, patientInd,
                    factory.getOWLNamedIndividual(test.getID(), prefixManager)));
        for (Entity test : patient.getNegativeTests())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(negativeTestProperty,
                    patientInd, factory.getOWLNamedIndividual(test.getID(), prefixManager)));
        for (Entity treatment : patient.getTreatments())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(treatmentProperty, patientInd,
                    factory.getOWLNamedIndividual(treatment.getID(), prefixManager)));
        for (Entity cause : patient.getCauses())
            ontologyManager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(causeProperty, patientInd,
                    factory.getOWLNamedIndividual(cause.getID(), prefixManager)));
        for (Entity disease : patient.getPreviousAndCurrentDiseases())
            ontologyManager.addAxiom(ontology,
                    factory.getOWLObjectPropertyAssertionAxiom(previousOrCurrentDiseaseProperty, patientInd,
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

    public Set<Patient> getPatients() {
        Set<Patient> patients = new HashSet<Patient>();
        Patient patient;
        for (OWLIndividual patientInd : EntitySearcher.getIndividuals(patientClass, ontology)) {
            patient = getPatient(patientInd);
            patient = getInferredPatient(patient);
            patients.add(patient);
        }
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

    @SuppressWarnings("unchecked")
    private AbstractAtom parseSWRLAtom(SWRLAtom swrlAtom) {
        String str = swrlAtom.toString();
        Pattern atomPattern = Pattern
                .compile("^(?<atomType>\\p{Alpha}+)\\(<\\S+#(?<atomID>\\w+)> (?<atomArguments>.+)\\)$");
        Pattern argumentPattern = Pattern.compile(
                "((?<argumentType>\\p{Alpha}*)\\(?<\\S+#(?<argumentID>\\w+)>\\)?)|(\"(?<value>\\d+)\"\\^\\^xsd:(?<valueType>[a-z]+))");
        Matcher atomMatcher = atomPattern.matcher(str);
        if (atomMatcher.find()) {

            String atomType = atomMatcher.group("atomType");
            String atomID = atomMatcher.group("atomID");
            String atomArguments = atomMatcher.group("atomArguments");
            Matcher argumentMatcher = argumentPattern.matcher(atomArguments);

            // class declaration
            if (atomType.equals("ClassAtom")) {
                if (argumentMatcher.find()) {
                    String argumentType = argumentMatcher.group("argumentType");
                    String argumentID = argumentMatcher.group("argumentID");
                    if (argumentType.equals("Variable"))
                        return new ClassDeclarationAtom<Variable>(classes.get(atomID), new Variable(argumentID));
                    if (symptoms.containsKey(argumentID))
                        return new ClassDeclarationAtom<Entity>(classes.get(atomID), symptoms.get(argumentID));
                    if (diseases.containsKey(argumentID))
                        return new ClassDeclarationAtom<Entity>(classes.get(atomID), diseases.get(argumentID));
                    if (tests.containsKey(argumentID))
                        return new ClassDeclarationAtom<Entity>(classes.get(atomID), tests.get(argumentID));
                    if (treatments.containsKey(argumentID))
                        return new ClassDeclarationAtom<Entity>(classes.get(atomID), treatments.get(argumentID));
                    if (causes.containsKey(argumentID))
                        return new ClassDeclarationAtom<Entity>(classes.get(atomID), causes.get(argumentID));
                }

            } else if (atomType.equals("ObjectPropertyAtom") || atomType.equals("DataPropertyAtom")
                    || atomType.equals("BuiltInAtom")) { // property
                int i = 0;
                @SuppressWarnings("rawtypes")
                TwoArgumentsAtom atom;
                if (atomType.equals("BuiltInAtom"))
                    atom = new TwoArgumentsAtom<>(atomID, "swrlb");
                else
                    atom = new TwoArgumentsAtom<>(atomID);
                while (argumentMatcher.find()) {
                    i += 1;
                    String argumentType = argumentMatcher.group("argumentType");
                    String argumentID = argumentMatcher.group("argumentID");
                    String value = argumentMatcher.group("value");
                    String valueType = argumentMatcher.group("valueType");
                    if (argumentType != null && argumentType.equals("Variable") && argumentID != null) {
                        switch (i) {
                            case 1:
                                atom.setArgument1(new Variable(argumentID));
                                continue;
                            case 2:
                                atom.setArgument2(new Variable(argumentID));
                                return atom;
                        }
                    } else if (argumentType != null && argumentType.equals("") && argumentID != null) {
                        Entity entity = null;
                        if (symptoms.containsKey(argumentID))
                            entity = symptoms.get(argumentID);
                        else if (diseases.containsKey(argumentID))
                            entity = diseases.get(argumentID);
                        else if (tests.containsKey(argumentID))
                            entity = tests.get(argumentID);
                        else if (treatments.containsKey(argumentID))
                            entity = treatments.get(argumentID);
                        else if (causes.containsKey(argumentID))
                            entity = causes.get(argumentID);

                        switch (i) {
                            case 1:
                                atom.setArgument1(entity);
                                break;
                            case 2:
                                atom.setArgument2(entity);
                                break;
                        }
                    } else if (valueType != null && value != null && StringUtils.isNumeric(value)) {
                        int intVal = Integer.parseInt(value);
                        switch (i) {
                            case 1:
                                atom.setArgument1(intVal);
                                break;
                            case 2:
                                atom.setArgument2(intVal);
                                break;
                        }
                    }
                }
                if (i == 2 && atom.getArgument1() != null && atom.getArgument2() != null) {
                    return atom;
                }
            }
        }
        return null;
    }

    public Map<String, Rule> loadRules() {
        for (SWRLAPIRule swrlRule : ruleOntology.getSWRLRules()) {
            Rule rule = new Rule(swrlRule.getRuleName());
            for (SWRLAtom atom : swrlRule.getBody())
                rule.addBodyAtom(parseSWRLAtom(atom));
            for (SWRLAtom atom : swrlRule.getHead())
                rule.addHeadAtom(parseSWRLAtom(atom));
            rules.put(rule.getName(), rule);
        }
        return rules;
    }

    public Map<String, Rule> getRules() {
        return rules;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Collection<Patient> generatePatientsFromRules() {
        assertedPatients.clear();
        Pattern diseasePattern = Pattern.compile("(?<diseaseID>\\w+)Disease(?<number>\\d+)");
        for (Rule rule : rules.values()) {
            Matcher diseaseMatcher = diseasePattern.matcher(rule.getName());
            if (diseaseMatcher.find()) {
                // System.out.println(rule);
                String diseaseID = diseaseMatcher.group("diseaseID");
                String number = diseaseMatcher.group("number");
                Map<String, Entity> variables = new HashMap<String, Entity>();
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
                for (AbstractAtom atom : rule.getBodyAtoms()) {
                    if (atom instanceof TwoArgumentsAtom) {
                        TwoArgumentsAtom twoArgumentsAtom = (TwoArgumentsAtom) atom;
                        String predicate = twoArgumentsAtom.getPredicate();
                        if (predicate.equals("hasSymptom")) {
                            String pName = ((Variable) twoArgumentsAtom.getArgument1()).getName();
                            Patient p = (Patient) variables.get(pName);
                            p.addSymptom((Entity) twoArgumentsAtom.getArgument2());
                        } else if (predicate.equals("negativeTest")) {
                            String pName = ((Variable) twoArgumentsAtom.getArgument1()).getName();
                            Patient p = (Patient) variables.get(pName);
                            p.addNegativeTest((Entity) twoArgumentsAtom.getArgument2());
                        } else if (predicate.equals("hadOrHasDisease")) {
                            String pName = ((Variable) twoArgumentsAtom.getArgument1()).getName();
                            Patient p = (Patient) variables.get(pName);
                            p.addPreviousAndCurrentDiseases((Entity) twoArgumentsAtom.getArgument2());
                        } else if (predicate.equals("hasDisease")) {
                            String pName = ((Variable) twoArgumentsAtom.getArgument1()).getName();
                            Patient p = (Patient) variables.get(pName);
                            p.addDisease((Entity) twoArgumentsAtom.getArgument2());
                        } else if (predicate.equals("age")) {
                            Variable patientVariable = (Variable) twoArgumentsAtom.getArgument1();
                            Variable ageVariable = (Variable) twoArgumentsAtom.getArgument2();
                            Patient p = (Patient) variables.get(patientVariable.getName());
                            Range<Integer> ageRange = Range.all();
                            for (AbstractAtom atom2 : rule.getBodyAtoms()) {
                                if (atom2 instanceof TwoArgumentsAtom) {
                                    TwoArgumentsAtom<Variable, Integer> twoArgumentsAtom2 = (TwoArgumentsAtom) atom2;
                                    if (twoArgumentsAtom.getPrefix().equals("swrlb")
                                            && twoArgumentsAtom2.getArgument1().equals(ageVariable)
                                            && twoArgumentsAtom2.getArgument2() instanceof Integer) {
                                        int ageBound = twoArgumentsAtom2.getArgument2();
                                        switch (twoArgumentsAtom2.getPredicate()) {
                                            case "equal":
                                                ageRange = ageRange.intersection(Range.singleton(ageBound));
                                                break;
                                            case "greaterThan":
                                                ageRange = ageRange.intersection(Range.greaterThan(ageBound));
                                                break;
                                            case "greaterThanOrEqual":
                                                ageRange = ageRange.intersection(Range.atLeast(ageBound));
                                                break;
                                            case "lessThan":
                                                ageRange = ageRange.intersection(Range.lessThan(ageBound));
                                                break;
                                            case "lessThanOrEqual":
                                                ageRange = ageRange.intersection(Range.atMost(ageBound));
                                                break;
                                        }
                                    }
                                }
                            }
                            if (ageRange.hasUpperBound() && ageRange.hasLowerBound()
                                    && ageRange.upperEndpoint() == ageRange.lowerEndpoint())
                                p.setAge(ageRange.upperEndpoint());
                            else {
                                int lowerBound = 0;
                                int upperBound = 10;
                                if (ageRange.hasUpperBound()) {
                                    switch (ageRange.upperBoundType()) {
                                        case OPEN:
                                            upperBound = ageRange.upperEndpoint();
                                            break;
                                        case CLOSED:
                                            upperBound = ageRange.upperEndpoint() + 1;
                                    }
                                }
                                if (ageRange.hasLowerBound()) {
                                    switch (ageRange.lowerBoundType()) {
                                        case OPEN:
                                            lowerBound = ageRange.lowerEndpoint() + 1;
                                            break;
                                        case CLOSED:
                                            lowerBound = ageRange.lowerEndpoint();
                                    }
                                }
                                Random random = new Random();
                                p.setAge(lowerBound + random.nextInt(upperBound - lowerBound));
                            }

                        }

                    }
                }
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
                if (patient != null)
                    assertedPatients.add(patient);
            }
        }
        return assertedPatients;
    }

    public Collection<Patient> getAssertedPatients() {
        return assertedPatients;
    }

    public void addAssertedPatient(Patient patient) {
        assertedPatients.add(patient);
    }

}
