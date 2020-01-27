package shaper.mapping.model.r2rml;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class LogicalTable {

    private Optional<URI> uri;

    private Optional<String> tableName; // rr:tableName
    private Optional<String> sqlQuery; // rr:sqlQuery
    private Set<URI> sqlVersions; // rr:sqlVersion, which is an IRI

    LogicalTable() {
        uri = Optional.empty();

        tableName = Optional.empty();
        sqlQuery = Optional.empty();
        sqlVersions = new TreeSet<>();
    }

    public void setUri(URI uri) {
        this.uri = Optional.ofNullable(uri);
    }

    public void setTableName(String tableName) {
        this.tableName = Optional.ofNullable(tableName);
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = Optional.ofNullable(sqlQuery);
    }

    public void addSqlVersion(URI sqlVersion) {
        sqlVersions.add(sqlVersion);
    }

    public Optional<String> getSqlQuery() { return sqlQuery; }

    public Optional<URI> getUri() { return uri; }
}
