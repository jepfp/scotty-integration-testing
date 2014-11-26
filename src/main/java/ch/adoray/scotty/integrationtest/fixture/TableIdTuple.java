package ch.adoray.scotty.integrationtest.fixture;

class TableIdTuple {
    private String table;
    private long id;

    public TableIdTuple(String table, long id){
        this.table = table;
        this.id = id;
    }

    public String getTable() {
        return table;
    }

    public long getId() {
        return id;
    }
}
