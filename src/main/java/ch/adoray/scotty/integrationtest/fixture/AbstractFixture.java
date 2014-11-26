package ch.adoray.scotty.integrationtest.fixture;

import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;

import com.google.common.collect.Lists;
abstract class AbstractFixture {
    private List<TableIdTuple> tableIdTuples = Lists.newArrayList();

    public void cleanUp() {
        try {
            removeAllFixtureRows();
        } catch (SQLException e) {
            throw new RuntimeException("Error while cleaning up fixture." + e);
        }
    }

    private void removeAllFixtureRows() throws SQLException {
        ListIterator<TableIdTuple> li = tableIdTuples.listIterator(tableIdTuples.size());
        while (li.hasPrevious()) {
            TableIdTuple tuple = li.previous();
            DatabaseAccess.deleteRow(tuple.getTable(), tuple.getId());
        }
    }

    protected void addTableIdTuple(String table, long id) {
        tableIdTuples.add(new TableIdTuple(table, id));
    }

    public List<Long> getCreatedIdsByTable(String table) {
        return tableIdTuples.stream()//
            .filter(t -> t.getTable() == table)//
            .map(t -> new Long(t.getId()))//
            .collect(Collectors.toList());
    }
}
