package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class SelectBuilder {

    private boolean distinct;

    private final List<Object> columns = new ArrayList<>();

    private final List<String> tables = new ArrayList<>();

    private final List<ExpressionAndSeparator> joins = new ArrayList<>();

    private final List<ExpressionAndSeparator> wheres = new ArrayList<>();

    private final List<String> groupBys = new ArrayList<>();

    private final List<ExpressionAndSeparator> havings = new ArrayList<>();

    private final List<SelectBuilder> unions = new ArrayList<>();

    private final List<String> orderBys = new ArrayList<>();

    private int limit = 0;

    private int offset = 0;

    private final List<Object> parameters;

    public SelectBuilder() {
        parameters = new LinkedList<>();
    }

    public SelectBuilder(String table) {
        tables.add(table);
        parameters = new LinkedList<>();
    }

    /**
     * Copy constructor. Used by {@link #clone()}.
     *
     * @param other
     *            SelectBuilder being cloned.
     */
    protected SelectBuilder(SelectBuilder other) {

        this.distinct = other.distinct;

        for (Object column : other.columns) {
            if (column instanceof SubSelectBuilder) {
                this.columns.add(new SubSelectBuilder((SubSelectBuilder) column));
            } else {
                this.columns.add(column);
            }
        }

        this.tables.addAll(other.tables);
        this.joins.addAll(other.joins);
        this.wheres.addAll(other.wheres);
        this.groupBys.addAll(other.groupBys);
        this.havings.addAll(other.havings);

        this.unions.addAll(other.unions);

        this.orderBys.addAll(other.orderBys);
        this.parameters = new LinkedList<>(other.parameters);
    }

    public SelectBuilder column(String... names) {
        columns.addAll(Arrays.asList(names));
        return this;
    }

    public SelectBuilder column(SubSelectBuilder subSelect) {
        columns.add(subSelect);
        return this;
    }

    public SelectBuilder column(String name, boolean groupBy) {
        columns.add(name);
        if (groupBy) {
            groupBys.add(name);
        }
        return this;
    }

    public SelectBuilder limit(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
        return this;
    }

    public SelectBuilder limit(int limit) {
        return limit(limit, 0);
    }

    public SelectBuilder distinct() {
        this.distinct = true;
        return this;
    }

    public SelectBuilder from(String table) {
        tables.add(table);
        return this;
    }

    public List<SelectBuilder> getUnions() {
        return unions;
    }

    public SelectBuilder groupBy(String expr) {
        groupBys.add(expr);
        return this;
    }

    private SelectBuilder having(String expr, String separator) {
        havings.add(new Condition(expr, separator));
        return this;
    }

    public SelectBuilder having(Predicate predicate) {
        return andHaving(predicate);
    }

    public SelectBuilder andHaving(Predicate predicate) {
        predicate.init(this);
        having(predicate.toSql(), " AND ");
        return this;
    }

    public SelectBuilder orHaving(Predicate predicate) {
        predicate.init(this);
        having(predicate.toSql(), " OR ");
        return this;
    }

    public SelectBuilder parameters(Object value) {
        parameters.add(value);
        return this;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public SelectBuilder join(String join) {
        joins.add(new Join(join, " JOIN "));
        return this;
    }

    public SelectBuilder leftJoin(String join) {
        joins.add(new Join(join, " LEFT JOIN "));
        return this;
    }

    public SelectBuilder rightJoin(String join) {
        joins.add(new Join(join, " RIGHT JOIN "));
        return this;
    }

    public SelectBuilder fullOuterJoin(String join) {
        joins.add(new Join(join, " FULL OUTER JOIN "));
        return this;
    }


    public SelectBuilder orderBy(String name) {
        orderBys.add(name);
        return this;
    }

    /**
     * Adds an ORDER BY item with a direction indicator.
     *
     * @param name
     *            Name of the column by which to sort.
     * @param ascending
     *            If true, specifies the direction "asc", otherwise, specifies
     *            the direction "desc".
     */
    public SelectBuilder orderBy(String name, boolean ascending) {
        if (ascending) {
            orderBys.add(name + " ASC");
        } else {
            orderBys.add(name + " DESC");
        }
        return this;
    }



    @Override
    public String toString() {

        StringBuilder sql = new StringBuilder("SELECT ");

        if (distinct) {
            sql.append("distinct ");
        }

        if (columns.isEmpty()) {
            sql.append("*");
        } else {
            appendList(sql, columns, "", ", ");
        }

        appendList(sql, tables, " FROM ", ", ");
        appendList(sql, joins, !joins.isEmpty() ? joins.get(0).getSeparator() : " ");
        appendList(sql, wheres, " WHERE ");
        appendList(sql, groupBys, " GROUP BY ", ", ");
        appendList(sql, havings, " HAVING ");
        appendList(sql, orderBys, " ORDER BY ", ", ");
        if(limit > 0)
            sql.append(" LIMIT ").append(limit);
        if(offset > 0)
            sql.append(", ").append(offset);
        appendList(sql, unions, " UNION ", " UNION ");
        return sql.toString();
    }

    /**
     * Adds a "union" select builder. The generated SQL will union this query
     * with the result of the main query. The provided builder must have the
     * same columns as the parent select builder and must not use "order by" or
     * "for update".
     */
    public SelectBuilder union(SelectBuilder unionBuilder) {
        unions.add(unionBuilder);
        return this;
    }

    private SelectBuilder where(String expr, String separator) {
        wheres.add(new Condition(expr, separator));
        return this;
    }

    public SelectBuilder where(Predicate predicate) {
        return andWhere(predicate);
    }

    public SelectBuilder andWhere(Predicate predicate) {
        predicate.init(this);
        where(predicate.toSql(), " AND ");
        return this;
    }

    public SelectBuilder orWhere(Predicate predicate) {
        predicate.init(this);
        where(predicate.toSql(), " OR ");
        return this;
    }

    /**
     * Constructs a list of items with given separators.
     *
     * @param sql
     *            StringBuilder to which the constructed string will be
     *            appended.
     * @param list
     *            List of objects (usually strings) to join.
     * @param init
     *            String to be added to the start of the list, before any of the
     *            items.
     * @param sep
     *            Separator string to be added between items in the list.
     */
    protected void appendList(StringBuilder sql, List<?> list, String init, String sep) {

        boolean first = true;

        for (Object s : list) {
            if (first) {
                sql.append(init);
            } else {
                sql.append(sep);
            }
            sql.append(s);
            first = false;
        }
    }

    protected void appendList(StringBuilder sql, List<ExpressionAndSeparator> conditionList, String init) {
        boolean first = true;
        for (ExpressionAndSeparator s : conditionList) {
            if (first) {
                sql.append(init);
            } else {
                sql.append(s.getSeparator());
            }

            sql.append(s.getExpression());
            first = false;
        }
    }
}