package shaper.mapping.model.r2rml;

import java.util.Optional;

public class PredicateObjectMap {
    private PredicateMap predicateMap;

    // either objectMap or refObjectMap
    private Optional<ObjectMap> objectMap;
    private Optional<RefObjectMap> refObjectMap;

    private PredicateObjectMap(PredicateMap predicateMap) { this.predicateMap = predicateMap; }

    PredicateObjectMap(PredicateMap predicateMap, ObjectMap objectMap) {
        this(predicateMap);
        this.objectMap = Optional.of(objectMap);
        refObjectMap = Optional.empty();
    }

    PredicateObjectMap(PredicateMap predicateMap, RefObjectMap refObjectMap) {
        this(predicateMap);
        this.refObjectMap = Optional.of(refObjectMap);
        objectMap = Optional.empty();
    }

    public PredicateMap getPredicateMap() { return predicateMap; }
    public Optional<ObjectMap> getObjectMap() { return objectMap; }
    public Optional<RefObjectMap> getRefObjectMap() { return refObjectMap; }
}
