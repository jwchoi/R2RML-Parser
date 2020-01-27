package shaper.mapping.model.r2rml;

import java.net.URI;
import java.util.List;

public class SubjectMap extends TermMap {
    private List<URI> classIRIs;

    SubjectMap(List<URI> classIRIs) {
        this.classIRIs = classIRIs;
    }

    public List<URI> getClassIRIs() { return classIRIs; }
}
