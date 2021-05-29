package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Predicates {

    private Predicates() {
    }

    /**
     * Joins a series of predicates with AND.
     */
    public static Predicate and(Predicate... predicates) {
        return join("AND", Arrays.asList(predicates));
    }

    /**
     * Joins a series of predicates with AND.
     */
    public static Predicate and(List<Predicate> predicates) {
        return join("AND", predicates);
    }

    /**
     * Adds an equals clause to a creator.
     *
     * @param expr
     *            SQL expression to be compared for equality.
     * @param value
     *            Value to which the SQL expression is compared.
     */
    private static Predicate eq(final String expr, final Object value) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                creator.parameters(value);
            }
            public String toSql() {
                return String.format("%s = ?", expr);
            }
        };
    }

    private static Predicate between(final String expr, final Object start, final Object end) {
        return new Predicate() {
            @Override
            public void init(SelectBuilder creator) {
                Objects.requireNonNull(start, "start must not be null!");
                Objects.requireNonNull(end, "end must not be null!");
                creator.parameters(start);
                creator.parameters(end);
            }

            @Override
            public String toSql() {
                return String.format("%s BETWEEN ? AND ?", expr);
            }
        };
    }


    /**
     * Adds an IN clause to a creator.
     *
     * @param expr
     *            SQL expression to be tested for inclusion.
     * @param values
     *            Values for the IN clause.
     */
    private static Predicate in(final String expr, final List<?> values) {

        return new Predicate() {

            private String sql;

            public void init(SelectBuilder creator) {

                StringBuilder sb = new StringBuilder();
                sb.append(expr).append(" in (");
                boolean first = true;
                for (Object value : values) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append("?");
                    creator.parameters(value);
                    first = false;
                }

                sb.append(")");

                sql = sb.toString();

            }

            public String toSql() {
                return sql;
            }
        };
    }


    /**
     * Adds an IN clause to a creator.
     *
     * @param expr
     *            SQL expression to be tested for inclusion.
     * @param values
     *            Values for the IN clause.
     */
    private static Predicate in(final String expr, final Object... values) {
        return in(expr, Arrays.asList(values));
    }


    /**
     * Factory for 'and' and 'or' predicates.
     */
    private static Predicate join(final String joinWord, final List<Predicate> preds) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                for (Predicate p : preds) {
                    p.init(creator);
                }
            }
            public String toSql() {
                StringBuilder sb = new StringBuilder()
                        .append("(");
                boolean first = true;
                for (Predicate p : preds) {
                    if (!first) {
                        sb.append(" ").append(joinWord).append(" ");
                    }
                    sb.append(p.toSql());
                    first = false;
                }
                return sb.append(")").toString();
            }
        };
    }


    /**
     * Adds a not equals clause to a creator.
     *
     * @param expr
     *            SQL expression to be compared for equality.
     * @param value
     *            Value to which the SQL expression is compared.
     */
    private static Predicate neq(final String expr, final Object value) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                creator.parameters(value);
            }
            public String toSql() {
                return String.format("%s <> ?", expr);
            }
        };
    }


    /**
     * Inverts the sense of the given child predicate. In SQL terms, this
     * surrounds the given predicate with "not (...)".
     *
     * @param childPredicate
     *            Predicate whose sense is to be inverted.
     */
    private static Predicate not(final Predicate childPredicate) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                childPredicate.init(creator);
            }
            public String toSql() {
                return "not (" + childPredicate.toSql() + ")";
            }
        };
    }

    /**
     * Joins a series of predicates with OR.
     */
    public static Predicate or(Predicate... predicates) {
        return join("OR", Arrays.asList(predicates));
    }

    /**
     * Joins a series of predicates with OR.
     */
    public static Predicate or(List<Predicate> predicates) {
        return join("OR", predicates);
    }



    private static Predicate isNull(final String expr) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                //no value need to be added to parameters
            }
            public String toSql() {
                return String.format("%s is null", expr);
            }
        };
    }


    private static Predicate isNotNull(final String expr) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                //no value need to be added to parameters
            }
            public String toSql() {
                return String.format("%s is not null", expr);
            }
        };
    }

    private static Predicate gt(final String expr, final Object value) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                creator.parameters(value);
            }
            public String toSql() {
                return String.format("%s > ?", expr);
            }
        };
    }

    private static Predicate gte(final String expr, final Object value) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                creator.parameters(value);
            }
            public String toSql() {
                return String.format("%s >= ?", expr);
            }
        };
    }

    private static Predicate lt(final String expr, final Object value) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                creator.parameters(value);
            }
            public String toSql() {
                return String.format("%s < ?", expr);
            }
        };
    }

    private static Predicate lte(final String expr, final Object value) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                creator.parameters(value);
            }
            public String toSql() {
                return String.format("%s <= ?", expr);
            }
        };
    }

    private static Predicate like(final String expr, final Object value) {
        return new Predicate() {
            public void init(SelectBuilder creator) {
                creator.parameters(value);
            }
            public String toSql() {
                return String.format("%s like ?",expr);
            }
        };
    }

    public static Predicate operation(SearchOperation searchOperation, final String expr, final Object value) {
        return operation(searchOperation, expr, value, null);
    }

    public static Predicate operation(SearchOperation searchOperation, final String expr, final Object... value) {
        return operation(searchOperation, expr, Arrays.asList(value), null);
    }

    public static Predicate operation(SearchOperation searchOperation, final String expr, final List<?> value) {
        return operation(searchOperation, expr, value, null);
    }


    public static Predicate operation(SearchOperation searchOperation, final String expr, final Object value, final Object otherValue) {
        switch (searchOperation) {
            case EQUALS:
                return eq(expr, value);
            case NOT_EQUALS:
                return neq(expr, value);
            case GREATER_THAN:
                return gt(expr, value);
            case GREATER_THAN_EQUALS:
                return gte(expr, value);
            case LESS_THAN:
                return lt(expr, value);
            case LESS_THAN_EQUALS:
                return lte(expr, value);
            case LIKE:
                return like(expr, value);
            case IN: {
                if(value instanceof List) {
                    return in(expr,(List) value);
                } else {
                    if(otherValue != null) {
                        return in(expr, value, otherValue);
                    } else {
                        return in(expr, value);
                    }
                }
            }
            case BETWEEN:
                return between(expr, value, otherValue);
            case NULL:
                return isNull(expr);
            case NOT_NULL:
                return isNotNull(expr);
            default:
                 throw new IllegalArgumentException("Search Operation not supported!");
        }
    }


}
