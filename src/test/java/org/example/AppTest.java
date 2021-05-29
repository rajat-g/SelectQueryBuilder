package org.example;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    @Test
    public void testWhereIn() {

        List<String> names = Arrays.asList("Larry", "Curly", "Moe");
        SelectBuilder selectBuilder = new SelectBuilder()
                .column("*")
                .from("Emp")
                .where(Predicates.in("name", names));
        assertEquals("SELECT * FROM Emp WHERE name in (?, ?, ?)", selectBuilder.toString());
        assertEquals(names, selectBuilder.getParameters());
    }

    @Test
    public void testSimpleTables() {
        //
        // Simple tables
        //
        SelectBuilder sb = new SelectBuilder("Employee");
        assertEquals("SELECT * FROM Employee", sb.toString());

        sb = new SelectBuilder("Employee e");
        assertEquals("SELECT * FROM Employee e", sb.toString());

        sb = new SelectBuilder("Employee e").column("name");
        assertEquals("SELECT name FROM Employee e", sb.toString());

        sb = new SelectBuilder("Employee e").column("name").column("age");
        assertEquals("SELECT name, age FROM Employee e", sb.toString());

        sb = new SelectBuilder("Employee e").column("name as n").column("age");
        assertEquals("SELECT name as n, age FROM Employee e", sb.toString());
    }

    @Test
    public void testWhere() {
        //
        // Where clauses
        //
        SelectBuilder sb = new SelectBuilder("Employee e").where(Predicates.like("name", "Bob%"));
        assertEquals("SELECT * FROM Employee e WHERE name like ?", sb.toString());
        assertEquals(Collections.singletonList("Bob%"), sb.getParameters());

        sb = new SelectBuilder("Employee e").where(Predicates.and(Predicates.like("name", "Bob%"), Predicates.gt("age", 37)));
        assertEquals("SELECT * FROM Employee e WHERE (name like ? AND age > ?)", sb.toString());
        assertEquals(Arrays.asList("Bob%", 37), sb.getParameters());

        sb = new SelectBuilder("Employee e").where(Predicates.eq("name", "Bob")).orWhere(Predicates.eq("name", "John"));
        assertEquals("SELECT * FROM Employee e WHERE name = ? OR name = ?", sb.toString());
        assertEquals(Arrays.asList("Bob", "John"), sb.getParameters());

        sb = new SelectBuilder("Employee e").where(Predicates.eq("name", "Bob")).where(Predicates.eq("name", "John"));
        assertEquals("SELECT * FROM Employee e WHERE name = ? AND name = ?", sb.toString());
        assertEquals(Arrays.asList("Bob", "John"), sb.getParameters());

        sb = new SelectBuilder("Employee e").where(Predicates.eq("name", "Bob")).andWhere(Predicates.eq("name", "John"));
        assertEquals("SELECT * FROM Employee e WHERE name = ? AND name = ?", sb.toString());
        assertEquals(Arrays.asList("Bob", "John"), sb.getParameters());

        sb = new SelectBuilder("Employee e").orWhere(Predicates.eq("name", "John"));
        assertEquals("SELECT * FROM Employee e WHERE name = ?", sb.toString());
        assertEquals(Arrays.asList("John"), sb.getParameters());

        sb = new SelectBuilder("Products").where(Predicates.between("Price", 10, 20));
        assertEquals("SELECT * FROM Products WHERE Price BETWEEN ? AND ?", sb.toString());
        assertEquals(Arrays.asList(10,20), sb.getParameters());

        sb = new SelectBuilder("suppliers");
        Predicate condition1 = Predicates.and(
                Predicates.eq("state", "California"), Predicates.neq("supplier_id", 900));
        Predicate condition2 = Predicates.eq("supplier_id", 100);
        sb.where(condition1).orWhere(condition2);
        assertEquals("SELECT * FROM suppliers WHERE (state = ? AND supplier_id <> ?)" +
                " OR supplier_id = ?", sb.toString());
        assertEquals(Arrays.asList("California", 900, 100), sb.getParameters());

    }

    @Test(expected = NullPointerException.class)
    public void testBetweenStartIsNullException() {
        new SelectBuilder("Products").where(Predicates.between("Price", null, 20));
    }

    @Test(expected = NullPointerException.class)
    public void testBetweenEndIsNullException() {
        new SelectBuilder("Products").where(Predicates.between("Price", 10, null));
    }

    @Test
    public void testJoin() {
        //
        // Join clauses
        //

        SelectBuilder sb = new SelectBuilder("Employee e").join("Department d on e.dept_id = d.id");
        assertEquals("SELECT * FROM Employee e JOIN Department d on e.dept_id = d.id", sb.toString());

        sb = new SelectBuilder("Employee e").join("Department d on e.dept_id = d.id")
                .where(Predicates.like("name", "Bob%"));
        assertEquals("SELECT * FROM Employee e JOIN Department d on e.dept_id = d.id WHERE name like ?",
                sb.toString());
        assertEquals(Arrays.asList("Bob%"), sb.getParameters());

        sb = new SelectBuilder().column("Orders.OrderID", "Customers.CustomerName", "Shippers.ShipperName")
                .from("Orders").join("Customers ON Orders.CustomerID = Customers.CustomerID")
                .join("Shippers ON Orders.ShipperID = Shippers.ShipperID");
        assertEquals("SELECT Orders.OrderID, Customers.CustomerName, Shippers.ShipperName FROM Orders" +
                " JOIN Customers ON Orders.CustomerID = Customers.CustomerID" +
                " JOIN Shippers ON Orders.ShipperID = Shippers.ShipperID", sb.toString());

        sb = new SelectBuilder().column("Orders.OrderID", "Customers.CustomerName", "Shippers.ShipperName")
                .from("Orders").leftJoin("Customers ON Orders.CustomerID = Customers.CustomerID")
                .rightJoin("Shippers ON Orders.ShipperID = Shippers.ShipperID");
        assertEquals("SELECT Orders.OrderID, Customers.CustomerName, Shippers.ShipperName FROM Orders" +
                " LEFT JOIN Customers ON Orders.CustomerID = Customers.CustomerID" +
                " RIGHT JOIN Shippers ON Orders.ShipperID = Shippers.ShipperID", sb.toString());

        sb = new SelectBuilder().column("Customers.CustomerName", "Orders.OrderID")
                .from("Customers").fullOuterJoin("Orders ON Customers.CustomerID=Orders.CustomerID");
        assertEquals("SELECT Customers.CustomerName, Orders.OrderID" +
                " FROM Customers" +
                " FULL OUTER JOIN Orders ON Customers.CustomerID=Orders.CustomerID", sb.toString());
    }

    @Test
    public void testOrderBy() {
        //
        // Order by clauses
        //

        SelectBuilder sb = new SelectBuilder("Employee e").orderBy("name");
        assertEquals("SELECT * FROM Employee e ORDER BY name", sb.toString());

        sb = new SelectBuilder("Employee e").orderBy("name desc").orderBy("age");
        assertEquals("SELECT * FROM Employee e ORDER BY name desc, age", sb.toString());

        sb = new SelectBuilder("Employee").where(Predicates.like("name", "Bob%")).orderBy("age");
        assertEquals("SELECT * FROM Employee WHERE name like ? ORDER BY age", sb.toString());
        assertEquals(Arrays.asList("Bob%"), sb.getParameters());

    }

    @Test
    public void testLimits() {

        SelectBuilder sb = new SelectBuilder()
                .from("test_table")
                .column("a")
                .column("b")
                .limit(10);

        assertEquals("SELECT a, b FROM test_table LIMIT 10", sb.toString());

        sb = sb.limit(10, 4);

        assertEquals("SELECT a, b FROM test_table LIMIT 10, 4", sb.toString());
    }

    @Test
    public void testGroupBy() {
        SelectBuilder sb = new SelectBuilder()
                .column("COUNT(CustomerID)").column("Country", true)
                .from("Customers");

        assertEquals("SELECT COUNT(CustomerID), Country FROM Customers GROUP BY Country", sb.toString());

        sb = new SelectBuilder()
                .column("COUNT(CustomerID)", "Country")
                .from("Customers")
                .groupBy("Country");

        assertEquals("SELECT COUNT(CustomerID), Country FROM Customers GROUP BY Country", sb.toString());

        sb = new SelectBuilder()
                .column("COUNT(CustomerID)", "Country")
                .from("Customers")
                .groupBy("Country")
                .orderBy("COUNT(CustomerID)", false);

        assertEquals("SELECT COUNT(CustomerID), Country FROM Customers" +
                " GROUP BY Country ORDER BY COUNT(CustomerID) DESC", sb.toString());
    }

    @Test
    public void testHaving() {
        SelectBuilder sb = new SelectBuilder()
                .column("COUNT(CustomerID)").column("Country", true)
                .from("Customers")
                .having(Predicates.gt("COUNT(CustomerID)", 5));

        assertEquals("SELECT COUNT(CustomerID), Country FROM Customers GROUP BY Country" +
                " HAVING COUNT(CustomerID) > ?", sb.toString());
        assertEquals(Arrays.asList(5), sb.getParameters());
    }

    @Test
    public void testUnions() {

        SelectBuilder sb = new SelectBuilder()
                .column("a")
                .column("b")
                .from("Foo")
                .where(Predicates.gt("a", 10))
                .orderBy("1");

        sb.union(new SelectBuilder()
                .column("c")
                .column("d")
                .from("Bar"));

        assertEquals("SELECT a, b FROM Foo WHERE a > ? ORDER BY 1 UNION SELECT c, d FROM Bar", sb.toString());
        assertEquals(Arrays.asList(10), sb.getParameters());
    }
}
