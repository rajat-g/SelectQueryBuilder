package org.example;

public class SubSelectBuilder extends SelectBuilder {

    private String alias;

    public SubSelectBuilder(String alias) {
        this.alias = alias;
    }

    protected SubSelectBuilder(SubSelectBuilder other) {
        super(other);
        this.alias = other.alias;
    }

    @Override
    public String toString() {
        return "(" +
                super.toString() +
                ") as " +
                alias;
    }
}

