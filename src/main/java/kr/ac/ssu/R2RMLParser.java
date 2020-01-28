package kr.ac.ssu;

import org.apache.jena.rdf.model.*;
import shaper.mapping.PrefixMap;

import java.io.*;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class R2RMLParser {

    public static void main( String[] args ) {
        R2RMLParser parser = new R2RMLParser("input/R2RMLTC0006a.ttl", Lang.TTL);

        Map<String, String> prefixMap = parser.getPrefixes();
        Set<String> keySet = prefixMap.keySet();
        for (String key: keySet)
            System.out.println("prefix: " + key + ", URI: " + prefixMap.get(key));

        Set<URI> logicalTablesWithURI = parser.getLogicalTables();
        for (URI logicalTable: logicalTablesWithURI) {
            System.out.println("logical table: " + logicalTable);

            System.out.println("sql query: " + parser.getSQLQuery(logicalTable.toString()));
            System.out.println("table name: " + parser.getTableName(logicalTable.toString()));

            Set<URI> sqlVersions = parser.getSQLVersions(logicalTable.toString());
            for (URI sqlVersion: sqlVersions)
                System.out.println("sql version: " + sqlVersion);
        }

        Set<String> triplesMaps = parser.getTriplesMaps();
        for (String triplesMap: triplesMaps) {
            System.out.println("triples map: " + triplesMap);

            // logical table
            String logicalTable = parser.getLogicalTable(triplesMap);
            System.out.println("logical table: " + logicalTable);

            System.out.println("sql query: " + parser.getSQLQuery(logicalTable));
            System.out.println("table name: " + parser.getTableName(logicalTable));

            Set<URI> sqlVersions = parser.getSQLVersions(logicalTable);
            for (URI sqlVersion: sqlVersions)
                System.out.println("sql version: " + sqlVersion);

            // subject map
            String subjectMap = parser.getSubjectMap(triplesMap);
            System.out.println("subject map: " + subjectMap);

            Set<URI> classes = parser.getClasses(subjectMap);
            for (URI classIRI: classes)
                System.out.println("class: " + classIRI);

            System.out.println("constant: " + parser.getIRIConstant(subjectMap));
            System.out.println("column: " + parser.getColumn(subjectMap));
            System.out.println("template: " + parser.getTemplate(subjectMap));
            System.out.println("term type: " + parser.getTermType(subjectMap));
            System.out.println("inverse expression: " + parser.getInverseExpression(subjectMap));

            // predicate object map
            Set<String> predicateObjectMaps = parser.getPredicateObjectMaps(triplesMap);
            for (String predicateObjectMap: predicateObjectMaps) {
                System.out.println("predicate object map: " + predicateObjectMap);

                // predicate or predicate map
                URI predicate = parser.getPredicate(predicateObjectMap);
                if (predicate == null) {
                    String predicateMap = parser.getPredicateMap(predicateObjectMap);
                    predicate = parser.getIRIConstant(predicateMap);
                }
                System.out.println("predicate: " + predicate);

                // object or object map
                URI IRIObject = parser.getIRIObject(predicateObjectMap);
                System.out.println("object: " + IRIObject + ", which is an IRI");
                String literalObject = parser.getLiteralObject(predicateObjectMap);
                System.out.println("object: " + literalObject + ", which is a literal");
                if (IRIObject == null && literalObject == null) {
                    String objectMap = parser.getObjectMap(predicateObjectMap);
                    System.out.println(objectMap);

                    URI IRIConstant = parser.getIRIConstant(objectMap);
                    System.out.println("constant: " + IRIConstant + ", which is an IRI");
                    String literalConstant = parser.getLiteralConstant(objectMap);
                    System.out.println("constant: " + literalConstant + ", which is a literal");

                    System.out.println("column: " + parser.getColumn(objectMap));
                    System.out.println("template: " + parser.getTemplate(objectMap));
                    System.out.println("term type: " + parser.getTermType(objectMap));
                    System.out.println("inverse expression: " + parser.getInverseExpression(objectMap));
                    System.out.println("language tag: " + parser.getLanguage(objectMap));
                    System.out.println("datatype: " + parser.getDatatype(objectMap));

                    // referencing object map
                    String parentTriplesMap = parser.getParentTriplesMap(objectMap);
                    System.out.println("parent triples map: " + parentTriplesMap);
                    if (parentTriplesMap != null) {
                        Set<String> joinConditions = parser.getJoinConditions(objectMap);
                        for (String joinCondition: joinConditions) {
                            System.out.println("child: " + parser.getChild(joinCondition));
                            System.out.println("parent: " + parser.getParent(joinCondition));
                        }
                    }
                }
            }
        }
    }

    public enum Lang {
//        RDF_XML("RDF/XML"), N_TRIPLE("N-TRIPLE"), N3("N3"),
        TURTLE("TURTLE"), TTL("TTL");

        private String lang;

        Lang(String lang) { this.lang = lang; }

        @Override
        public String toString() { return lang; }
    }

    private Optional<URI> base;
    private Model model;

    public R2RMLParser(String pathname, Lang lang) {
        File r2rmlFile = new File(pathname);
        base = getBase(r2rmlFile);
        model = getModel(r2rmlFile, base, lang.toString());
    }

    public String getSQLQuery(String logicalTable) {
        Resource s = createResource(logicalTable);
        Property p = createRRProperty("sqlQuery");

        Set<String> set = getLiteralObjectsOf(s, p);

        return set.size() > 0 ? set.toArray(new String[0])[0].trim() : null;
    }

    public String getTableName(String logicalTable) {
        Resource s = createResource(logicalTable);
        Property p = createRRProperty("tableName");

        Set<String> set = getLiteralObjectsOf(s, p);

        return set.size() > 0 ? set.toArray(new String[0])[0] : null;
    }

    public Set<URI> getClasses(String subjectMap) {
        Resource s = createResource(subjectMap);
        Property p = createRRProperty("class");

        return getIRIObjectsOf(s, p);
    }

    public Set<URI> getSQLVersions(String logicalTable) {
        Resource s = createResource(logicalTable);
        Property p = createRRProperty("sqlVersion");

        return getIRIObjectsOf(s, p);
    }

    private Set<String> getLiteralObjectsOf(Resource s, Property p) {
        Set<String> set = new TreeSet<>();

        NodeIterator iterator = model.listObjectsOfProperty(s, p);
        while (iterator.hasNext()) {
            RDFNode o = iterator.next();
            if (o.isLiteral()) set.add(o.asLiteral().toString().replace("\\\"", "\""));
        }

        return set;
    }

    private Set<URI> getIRIObjectsOf(Resource s, Property p) {
        Set<URI> set = new TreeSet<>();

        NodeIterator iterator = model.listObjectsOfProperty(s, p);
        while (iterator.hasNext()) {
            RDFNode o = iterator.next();
            if (o.isURIResource()) set.add(URI.create(o.asResource().getURI()));
        }

        return set;
    }

    public Set<String> getTriplesMaps() {
        Property p; // rr:logicalTable, rr:subjectMap

        p = createRRProperty("logicalTable");
        Set<String> subjectsOfLogicalTable = getResourceSubjectsOf(p);

        p = createRRProperty("subjectMap");
        Set<String> subjectsOfSubjectMap = getResourceSubjectsOf(p);

        subjectsOfLogicalTable.retainAll(subjectsOfSubjectMap);

        return subjectsOfLogicalTable;
    }

    private boolean isURI(String str) {
        try { return new org.apache.jena.ext.xerces.util.URI(str) != null; }
        catch (org.apache.jena.ext.xerces.util.URI.MalformedURIException e) { return false; }
    }

    private Resource createResource(String str) {
        return isURI(str) ? model.createResource(str) : model.createResource(AnonId.create(str));
    }

    private Property createRRProperty(String localName) {
        return model.createProperty(PrefixMap.getURI("rr").toString(), localName);
    }

    public String getInverseExpression(String termMap) {
        Resource s = createResource(termMap);
        Property p = createRRProperty("inverseExpression");

        Set<String> objects = getLiteralObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new String[0])[0] : null;
    }

    public URI getDatatype(String termMap) {
        Resource s = createResource(termMap);
        Property p = createRRProperty("datatype");

        Set<URI> objects = getIRIObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new URI[0])[0] : null;
    }

    public String getLanguage(String termMap) {
        Resource s = createResource(termMap);
        Property p = createRRProperty("language");

        Set<String> objects = getLiteralObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new String[0])[0] : null;
    }

    public String getLiteralObject(String predicateObjectMap) {
        Resource s = createResource(predicateObjectMap);
        Property p = createRRProperty("object");

        Set<String> objects = getLiteralObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new String[0])[0] : null;
    }

    public URI getIRIObject(String predicateObjectMap) {
        Resource s = createResource(predicateObjectMap);
        Property p = createRRProperty("object");

        Set<URI> objects = getIRIObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new URI[0])[0] : null;
    }

    public Set<URI> getGraphs(String subjectMapOrPredicateObjectMap) {
        Resource s = createResource(subjectMapOrPredicateObjectMap);
        Property p = createRRProperty("graph");

        return getIRIObjectsOf(s, p);
    }

    public URI getPredicate(String predicateObjectMap) {
        Resource s = createResource(predicateObjectMap);
        Property p = createRRProperty("predicate");

        Set<URI> objects = getIRIObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new URI[0])[0] : null;
    }

    public URI getTermType(String termMap) {
        Resource s = createResource(termMap);
        Property p = createRRProperty("termType");

        Set<URI> objects = getIRIObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new URI[0])[0] : null;
    }

    public String getTemplate(String termMap) {
        Resource s = createResource(termMap);
        Property p = createRRProperty("template");

        Set<String> objects = getLiteralObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new String[0])[0] : null;
    }

    public String getColumn(String termMap) {
        Resource s = createResource(termMap);
        Property p = createRRProperty("column");

        Set<String> objects = getLiteralObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new String[0])[0] : null;
    }

    // when the termMap is a subject map, object map, predicate map, or graph map
    public URI getIRIConstant(String termMap) {
        Resource s = createResource(termMap);
        Property p = createRRProperty("constant");

        Set<URI> objects = getIRIObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new URI[0])[0] : null;
    }

    // when the termMap is an object map
    public String getLiteralConstant(String termMap) {
        Resource s = createResource(termMap);
        Property p = createRRProperty("constant");

        Set<String> objects = getLiteralObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new String[0])[0] : null;
    }

    public String getParentTriplesMap(String refObjectMap) {
        Resource s = createResource(refObjectMap);
        Property p = createRRProperty("parentTriplesMap");

        Set<String> objects = getResourceObjectsOf(s, p);

        return objects.size() > 0 ? objects.toArray(new String[0])[0] : null;
    }

    public String getObjectMap(String predicateObjectMap) {
        Resource s = createResource(predicateObjectMap);
        Property p = createRRProperty("objectMap");

        Set<String> objects = getResourceObjectsOf(s, p);

        return objects.toArray(new String[0])[0];
    }

    public Set<String> getGraphMaps(String subjectMapOrPredicateObjectMap) {
        Resource s = createResource(subjectMapOrPredicateObjectMap);
        Property p = createRRProperty("graphMap");

        return getResourceObjectsOf(s, p);
    }

    public String getPredicateMap(String predicateObjectMap) {
        Resource s = createResource(predicateObjectMap);
        Property p = createRRProperty("predicateMap");

        Set<String> objects = getResourceObjectsOf(s, p);

        return objects.toArray(new String[0])[0];
    }

    public String getChild(String joinCondition) {
        Resource s = createResource(joinCondition);
        Property p = createRRProperty("child");

        Set<String> objects = getLiteralObjectsOf(s, p);

        return objects.toArray(new String[0])[0];
    }

    public String getParent(String joinCondition) {
        Resource s = createResource(joinCondition);
        Property p = createRRProperty("parent");

        Set<String> objects = getLiteralObjectsOf(s, p);

        return objects.toArray(new String[0])[0];
    }

    public Set<String> getJoinConditions(String refObjectMap) {
        Resource s = createResource(refObjectMap);
        Property p = createRRProperty("joinCondition");

        return getResourceObjectsOf(s, p);
    }

    public Set<String> getPredicateObjectMaps(String triplesMap) {
        Resource s = createResource(triplesMap);
        Property p = createRRProperty("predicateObjectMap");

        return getResourceObjectsOf(s, p);
    }

    public String getSubjectMap(String triplesMap) {
        Resource s = createResource(triplesMap);
        Property p = createRRProperty("subjectMap");

        Set<String> objects = getResourceObjectsOf(s, p);

        return objects.toArray(new String[0])[0];
    }

    public String getLogicalTable(String triplesMap) {
        Resource s = createResource(triplesMap);
        Property p = createRRProperty("logicalTable");

        Set<String> objects = getResourceObjectsOf(s, p);

        return objects.toArray(new String[0])[0];
    }

    public Set<URI> getLogicalTables() {
        Set<URI> set = new TreeSet<>();

        Property p; // rr:tableName, rr:sqlQuery, rr:sqlVersion

        p = createRRProperty("tableName");
        set.addAll(getIRISubjectsOf(p));

        p = createRRProperty("sqlQuery");
        set.addAll(getIRISubjectsOf(p));

        p = createRRProperty("sqlVersion");
        set.addAll(getIRISubjectsOf(p));

        return set;
    }

    private Set<String> getResourceObjectsOf(Resource s, Property p) {
        Set<String> set = new TreeSet<>();

        NodeIterator iterator = model.listObjectsOfProperty(s, p);

        while (iterator.hasNext()) {
            RDFNode node = iterator.next();

            if (node.isURIResource())
                set.add(node.asResource().getURI());
            else
                set.add(node.asResource().getId().getBlankNodeId().getLabelString());
        }

        return set;
    }

    private Set<String> getResourceSubjectsOf(Property p) {
        Set<String> set = new TreeSet<>();

        ResIterator iterator = model.listSubjectsWithProperty(p);

        while (iterator.hasNext()) {
            Resource resource = iterator.next();
            String uri = resource.getURI();

            if (uri != null)
                set.add(uri);
            else
                set.add(resource.getId().getBlankNodeId().getLabelString());
        }

        return set;
    }

    private Set<URI> getIRISubjectsOf(Property p) {
        Set<URI> set = new TreeSet<>();

        ResIterator iterator = model.listSubjectsWithProperty(p);

        while (iterator.hasNext()) {
            Resource resource = iterator.next();
            String uri = resource.getURI();

            if (uri != null) set.add(URI.create(uri));
        }

        return set;
    }

    public Map<String, String> getPrefixes() {
        return model.getNsPrefixMap();
    }

    private Model getModel(File r2rmlFile, Optional<URI> base, String lang) {
        Model model = ModelFactory.createDefaultModel();

        try(InputStream inputStream = new FileInputStream(r2rmlFile)) {
            if (base.isPresent())
                model.read(inputStream, base.get().toString(), lang);
            else
                model.read(inputStream, null, lang);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }

    private Optional<URI> getBase(File r2rmlFile) {
        Optional<URI> base = Optional.empty();
        try(LineNumberReader reader = new LineNumberReader(new FileReader(r2rmlFile))) {
            for (String line = reader.readLine().trim(); line != null; line = reader.readLine().trim()) {
                if (line.startsWith("@base")
                        || line.regionMatches(true, 0, "BASE", 0, 4)) {
                    base = Optional.of(URI.create(line.substring(line.indexOf("<") + 1, line.lastIndexOf(">"))));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return base;
    }
}
