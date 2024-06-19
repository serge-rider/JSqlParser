/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdaptersTest {

    /**
     * Test extracting JDBC named parameters using adapters
     */
    @Test
    public void testAdapters() throws JSQLParserException {
        String sql = "SELECT * FROM MYTABLE WHERE COLUMN_A = :paramA AND COLUMN_B <> :paramB";
        Statement stmnt = CCJSqlParserUtil.parse(sql);

        final Stack<Pair<String, String>> params = new Stack<>();
        stmnt.accept(new StatementVisitorAdapter() {
            @Override
            public Object visit(Select select) {
                select.accept(new SelectVisitorAdapter() {
                    @Override
                    public Object visit(PlainSelect plainSelect) {
                        plainSelect.getWhere().accept(new ExpressionVisitorAdapter() {
                            @Override
                            protected void visitBinaryExpression(BinaryExpression expr) {
                                if (!(expr instanceof AndExpression)) {
                                    params.push(new Pair<>(null, null));
                                }
                                super.visitBinaryExpression(expr);
                            }

                            @Override
                            public Void visit(Column column) {
                                params.push(new Pair<>(column.getColumnName(),
                                        params.pop().getRight()));
                                return null;
                            }

                            @Override
                            public Void visit(JdbcNamedParameter parameter) {
                                params.push(new Pair<>(params.pop().getLeft(),
                                        parameter.getName()));
                                return null;
                            }
                        });
                        return null;
                    }
                });
                return null;
            }
        });

        assertEquals(2, params.size());
        Pair<String, String> param2 = params.pop();
        assertEquals("COLUMN_B", param2.getLeft());
        assertEquals("paramB", param2.getRight());
        Pair<String, String> param1 = params.pop();
        assertEquals("COLUMN_A", param1.getLeft());
        assertEquals("paramA", param1.getRight());
    }

    private static class Pair<L, R> {

        private final L left;
        private final R right;

        private Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }

        public boolean isEmpty() {
            return left == null && right == null;
        }

        public boolean isFull() {
            return left != null && right != null;
        }

        @Override
        public String toString() {
            String sb = "Pair{" + "left=" + left +
                    ", right=" + right +
                    '}';
            return sb;
        }
    }
}
