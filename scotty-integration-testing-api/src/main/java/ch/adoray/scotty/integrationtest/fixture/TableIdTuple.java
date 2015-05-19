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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((table == null) ? 0 : table.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TableIdTuple other = (TableIdTuple) obj;
        if (id != other.id)
            return false;
        if (table == null) {
            if (other.table != null)
                return false;
        } else if (!table.equals(other.table))
            return false;
        return true;
    }
}
