package org.example;

public interface Predicate {


    public void init(SelectBuilder creator);

    /**
     * Returns an SQL expression representing the predicate. Parameters may be
     * included preceded by a colon.
     */
    public String toSql();

}
