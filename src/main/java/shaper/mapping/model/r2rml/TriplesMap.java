package shaper.mapping.model.r2rml;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TriplesMap {

    private URI uri;
    private String blankNodeID;
    private LogicalTable logicalTable;
    private SubjectMap subjectMap;
    private List<PredicateObjectMap> predicateObjectMaps;

    private TriplesMap(LogicalTable logicalTable, SubjectMap subjectMap) {
        this.logicalTable = logicalTable;
        this.subjectMap = subjectMap;
        predicateObjectMaps = new ArrayList<>();
    }

    TriplesMap(URI uri, LogicalTable logicalTable, SubjectMap subjectMap) {
        this(logicalTable, subjectMap);
        this.uri = uri;
    }

    TriplesMap(String blankNodeID, LogicalTable logicalTable, SubjectMap subjectMap) {
        this(logicalTable, subjectMap);
        this.blankNodeID = blankNodeID;
    }

    public void addPredicateObjectMap(PredicateObjectMap predicateObjectMap) { predicateObjectMaps.add(predicateObjectMap); }

    public LogicalTable getLogicalTable() { return logicalTable; }

    public SubjectMap getSubjectMap() { return subjectMap; }

    public URI getUri() { return uri; }

    public List<PredicateObjectMap> getPredicateObjectMaps() { return predicateObjectMaps; }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TriplesMap)
            return uri.equals(((TriplesMap) obj).uri);

        return super.equals(obj);
    }
}
