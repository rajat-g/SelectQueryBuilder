# SelectQueryBuilder

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/93d76063235941f2b89cff62550edaf8)](https://www.codacy.com/gh/rajat-g/SelectQueryBuilder/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=rajat-g/SelectQueryBuilder&amp;utm_campaign=Badge_Grade)  [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rajat-g_SelectQueryBuilder&metric=alert_status)](https://sonarcloud.io/dashboard?id=rajat-g_SelectQueryBuilder)  [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=rajat-g_SelectQueryBuilder&metric=coverage)](https://sonarcloud.io/dashboard?id=rajat-g_SelectQueryBuilder)


Examples
=======
Some usage examples:

Example 1:
```
List<String> names = Arrays.asList("Larry", "Curly", "Moe");
SelectBuilder selectBuilder = new SelectBuilder()
                                    .column("*")
                                    .from("Emp")
                                    .where(Predicates.in("name", names));
```
The output is:
```
SELECT * FROM Emp WHERE name in (?, ?, ?)
```

Example 2:
```
SelectBuilder sb = new SelectBuilder("suppliers");
Predicate condition1 = Predicates.and(Predicates.eq("state", "California"),
                                      Predicates.neq("supplier_id", 900));
Predicate condition2 = Predicates.eq("supplier_id", 100);
sb.where(condition1).orWhere(condition2);
```
The output is:
```
SELECT * FROM suppliers WHERE (state = ? AND supplier_id <> ?) OR supplier_id = ?
```

Example 3:
```
SelectBuilder sb = new SelectBuilder().column("Customers.CustomerName", "Orders.OrderID")
                .from("Customers").fullOuterJoin("Orders ON Customers.CustomerID=Orders.CustomerID");
```
The output is:
```
SELECT Customers.CustomerName, Orders.OrderID FROM Customers FULL OUTER JOIN Orders ON Customers.CustomerID=Orders.CustomerID
```
