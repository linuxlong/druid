package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.IllegalSQLObjectViolation;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;

public class WallVisitorUtils {

    public static void check(WallVisitor visitor, SQLBinaryOpExpr x) {
        if (Boolean.TRUE == getValue(x)) {
            visitor.getViolations().add(new IllegalSQLObjectViolation(SQLUtils.toSQLString(x)));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getValue(SQLBinaryOpExpr x) {
        x.getLeft().setParent(x);
        x.getRight().setParent(x);
        Object leftResult = getValue(x.getLeft());
        Object rightResult = getValue(x.getRight());

        if (x.getOperator() == SQLBinaryOperator.BooleanOr) {
            if (Boolean.TRUE == leftResult || Boolean.TRUE == rightResult) {
                return true;
            }
        }

        if (x.getOperator() == SQLBinaryOperator.BooleanAnd) {
            if (Boolean.FALSE == leftResult || Boolean.FALSE == rightResult) {
                return false;
            }

            if (Boolean.TRUE == leftResult && Boolean.TRUE == rightResult) {
                return true;
            }
        }

        if (x.getOperator() == SQLBinaryOperator.Equality) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return true;
            }

            if (leftResult == null || rightResult == null) {
                return null;
            }

            return leftResult.equals(rightResult);
        }

        if (x.getOperator() == SQLBinaryOperator.NotEqual || x.getOperator() == SQLBinaryOperator.LessThanOrGreater) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }

            if (leftResult == null || rightResult == null) {
                return null;
            }

            return !leftResult.equals(rightResult);
        }

        if (x.getOperator() == SQLBinaryOperator.GreaterThan) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }

            if (leftResult == null || rightResult == null) {
                return null;
            }

            if (leftResult instanceof Comparable) {
                return (((Comparable) leftResult).compareTo(rightResult) > 0);
            }
        }

        if (x.getOperator() == SQLBinaryOperator.GreaterThanOrEqual) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }

            if (leftResult == null || rightResult == null) {
                return null;
            }

            if (leftResult instanceof Comparable) {
                return ((Comparable) leftResult).compareTo(rightResult) >= 0;
            }
        }

        if (x.getOperator() == SQLBinaryOperator.LessThan) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }

            if (leftResult == null || rightResult == null) {
                return null;
            }

            if (leftResult instanceof Comparable) {
                return (((Comparable) leftResult).compareTo(rightResult) < 0);
            }
        }

        if (x.getOperator() == SQLBinaryOperator.LessThanOrEqual) {
            if (x.getLeft() instanceof SQLNullExpr && x.getRight() instanceof SQLNullExpr) {
                return false;
            }

            if (leftResult == null || rightResult == null) {
                return null;
            }

            if (leftResult instanceof Comparable) {
                return ((Comparable) leftResult).compareTo(rightResult) <= 0;
            }
        }

        if (x.getOperator() == SQLBinaryOperator.Like) {
            if (x.getRight() instanceof SQLCharExpr) {
                String text = ((SQLCharExpr) x.getRight()).getText();

                if (text.length() >= 0) {
                    for (char ch : text.toCharArray()) {
                        if (ch != '%') {
                            return null;
                        }
                    }

                    return true;
                }

            }
        }

        return null;
    }

    public static Object getValue(SQLExpr x) {
        if (x instanceof SQLBinaryOpExpr) {
            return getValue((SQLBinaryOpExpr) x);
        }

        if (x instanceof MySqlBooleanExpr) {
            return ((MySqlBooleanExpr) x).getValue();
        }

        if (x instanceof SQLNumericLiteralExpr) {
            return ((SQLNumericLiteralExpr) x).getNumber();
        }

        if (x instanceof SQLCharExpr) {
            return ((SQLCharExpr) x).getText();
        }

        if (x instanceof SQLNCharExpr) {
            return ((SQLNCharExpr) x).getText();
        }

        if (x instanceof SQLNotExpr) {
            Object result = getValue(((SQLNotExpr) x).getExpr());
            if (result != null && result instanceof Boolean) {
                return !((Boolean) result).booleanValue();
            }
        }

        if (x instanceof SQLQueryExpr) {
            if (isSimpleCountTableSource(((SQLQueryExpr) x).getSubQuery())) {
                return Integer.valueOf(1);
            }
        }

        if (x instanceof SQLMethodInvokeExpr) {
            return getValue((SQLMethodInvokeExpr) x);
        }

        return null;
    }

    public static Object getValue(SQLMethodInvokeExpr x) {
        String methodName = x.getMethodName();
        if ("len".equalsIgnoreCase(methodName) || "length".equalsIgnoreCase(methodName)) {
            Object firstValue = null;
            if (x.getParameters().size() > 0) {
                firstValue = (getValue(x.getParameters().get(0)));
            }

            if (firstValue instanceof String) {
                return ((String) firstValue).length();
            }
        }
        
        if ("if".equalsIgnoreCase(methodName) && x.getParameters().size() == 3) {
            SQLExpr first = x.getParameters().get(0);
            Object firstResult = getValue(first);
            
            if (Boolean.TRUE == firstResult) {
                return getValue(x.getParameters().get(1));
            }
            
            if (Boolean.FALSE == firstResult) {
                getValue(x.getParameters().get(2));
            }
        }

        return null;
    }

    public static boolean isSimpleCountTableSource(SQLTableSource tableSource) {
        if (!(tableSource instanceof SQLSubqueryTableSource)) {
            return false;
        }

        SQLSubqueryTableSource subQuery = (SQLSubqueryTableSource) tableSource;

        return isSimpleCountTableSource(subQuery.getSelect());
    }

    public static boolean isSimpleCountTableSource(SQLSelect select) {
        SQLSelectQuery query = select.getQuery();

        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;

            boolean allawTrueWhere = false;

            if (queryBlock.getWhere() == null) {
                allawTrueWhere = true;
            } else {
                Object whereValue = getValue(queryBlock.getWhere());
                if (whereValue == Boolean.TRUE) {
                    allawTrueWhere = true;
                } else if (whereValue == Boolean.FALSE) {
                    return false;
                }
            }
            boolean simpleCount = false;
            if (queryBlock.getSelectList().size() == 1) {
                SQLExpr selectItemExpr = queryBlock.getSelectList().get(0).getExpr();
                if (selectItemExpr instanceof SQLAggregateExpr) {
                    if (((SQLAggregateExpr) selectItemExpr).getMethodName().equalsIgnoreCase("COUNT")) {
                        simpleCount = true;
                    }
                }
            }

            if (allawTrueWhere && simpleCount) {
                return true;
            }
        }

        return false;
    }

    public static void check(WallVisitor visitor, SQLMethodInvokeExpr x) {
        String methodName = x.getMethodName();

        if (visitor.getPermitFunctions().contains(methodName.toLowerCase())) {
            visitor.getViolations().add(new IllegalSQLObjectViolation(visitor.toSQL(x)));
        }

    }

    public static void check(WallVisitor visitor, SQLExprTableSource x) {
        SQLExpr expr = x.getExpr();

        String tableName = null;
        if (expr instanceof SQLPropertyExpr) {
            SQLPropertyExpr propExpr = (SQLPropertyExpr) expr;

            if (propExpr.getOwner() instanceof SQLIdentifierExpr) {
                String ownerName = ((SQLIdentifierExpr) propExpr.getOwner()).getName();
                
                ownerName = form(ownerName);

                if (visitor.getPermitSchemas().contains(ownerName.toLowerCase())) {
                    visitor.getViolations().add(new IllegalSQLObjectViolation(visitor.toSQL(x)));
                }
            }

            tableName = propExpr.getName();
        }

        if (expr instanceof SQLIdentifierExpr) {
            tableName = ((SQLIdentifierExpr) expr).getName();
        }

        if (tableName != null) {
            tableName = form(tableName);
            if (visitor.getPermitTables().contains(tableName)) {
                visitor.getViolations().add(new IllegalSQLObjectViolation(visitor.toSQL(x)));
            }
        }
    }

    public static boolean queryBlockFromIsNull(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLTableSource from = ((SQLSelectQueryBlock) query).getFrom();

            if (from == null) {
                return true;
            }

            if (from instanceof SQLExprTableSource) {
                SQLExpr fromExpr = ((SQLExprTableSource) from).getExpr();
                if (fromExpr instanceof SQLName) {
                    String name = fromExpr.toString();

                    name = form(name);

                    if (name.equalsIgnoreCase("DUAL")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String form(String name) {
        if (name.startsWith("\"") && name.endsWith("\"")) {
            name = name.substring(1, name.length() - 1);
        }
        if (name.startsWith("`") && name.endsWith("`")) {
            name = name.substring(1, name.length() - 1);
        }
        
        name = name.toLowerCase();
        return name;
    }
}