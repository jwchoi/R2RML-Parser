package shaper.mapping.model.r2rml;

import java.net.URI;
import java.util.*;

public class R2RMLModel {
    private Map<String, String> prefixMap;
    private Set<TriplesMap> triplesMaps;
    private List<LogicalTable> logicalTables;

    R2RMLModel() {
        prefixMap = new HashMap<>();
        logicalTables = new ArrayList<>();
        triplesMaps = new HashSet<>();
    }

    public void addPrefixMap(String prefix, String uri) { prefixMap.put(prefix, uri); }

    public void addLogicalTable(LogicalTable logicalTable) { logicalTables.add(logicalTable); }

    public void addTriplesMap(TriplesMap triplesMap) { triplesMaps.add(triplesMap); }

    public boolean isIRI(String template) {
        for (String key : prefixMap.keySet())
            if (template.startsWith(key + ":") || template.startsWith(prefixMap.get(key))) return true;

        return template.startsWith("http") ? true : false;
    }

    public Set<TriplesMap> getTriplesMaps() { return triplesMaps; }

    public LogicalTable getLogicalTableBy(URI iri) {
        for (LogicalTable logicalTable: logicalTables) {
            Optional<URI> iriOfLogicalTable = logicalTable.getUri();
            if (iriOfLogicalTable.isPresent()) {
                if (iriOfLogicalTable.get().equals(iri))
                    return logicalTable;
            }
        }

        return null;
    }

    public Optional<String> getRelativeIRI(URI iri) {
        Optional<String> relativeIRI = Optional.empty();

        for (String key : prefixMap.keySet()) {
            String value = prefixMap.get(key);
            String absoluteIRI = iri.toString();
            if (absoluteIRI.startsWith(value)) {
                relativeIRI = Optional.of(absoluteIRI.replace(value, key + ":"));
                break;
            }
        }

        return relativeIRI;
    }

    public Map<String, String> getPrefixMap() { return prefixMap; }
}
