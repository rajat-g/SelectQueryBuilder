package org.example;

public abstract class ExpressionAndSeparator {

    String expression;
    String separator;

    public ExpressionAndSeparator(String expression, String separator) {
        this.expression = expression;
        this.separator = separator;
    }

    public String getExpression() {
        return expression;
    }

    public String getSeparator() {
        return separator;
    }


}
