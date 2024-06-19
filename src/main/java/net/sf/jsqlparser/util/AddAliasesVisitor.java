/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util;

import java.util.LinkedList;
import java.util.List;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.TableStatement;
import net.sf.jsqlparser.statement.select.Values;
import net.sf.jsqlparser.statement.select.WithItem;

/**
 * Add aliases to every column and expression selected by a select - statement. Existing aliases are
 * recognized and preserved. This class standard uses a prefix of A and a counter to generate new
 * aliases (e.g. A1, A5, ...). This behaviour can be altered.
 *
 * @author tw
 */
public class AddAliasesVisitor<T> implements SelectVisitor<T>, SelectItemVisitor<T> {

    private static final String NOT_SUPPORTED_YET = "Not supported yet.";
    private final List<String> aliases = new LinkedList<String>();
    private boolean firstRun = true;
    private int counter = 0;
    private String prefix = "A";

    @Override
    public T visit(ParenthesedSelect parenthesedSelect) {
        parenthesedSelect.getSelect().accept(this);
        return null;
    }

    @Override
    public T visit(PlainSelect plainSelect) {
        firstRun = true;
        counter = 0;
        aliases.clear();
        for (SelectItem<?> item : plainSelect.getSelectItems()) {
            item.accept(this);
        }
        firstRun = false;
        for (SelectItem<?> item : plainSelect.getSelectItems()) {
            item.accept(this);
        }
        return null;
    }

    @Override
    public T visit(SetOperationList setOpList) {
        for (Select select : setOpList.getSelects()) {
            select.accept(this);
        }
        return null;
    }

    @Override
    public T visit(SelectItem<?> selectExpressionItem) {
        if (firstRun) {
            if (selectExpressionItem.getAlias() != null) {
                aliases.add(selectExpressionItem.getAlias().getName().toUpperCase());
            }
        } else {
            if (selectExpressionItem.getAlias() == null) {
                while (true) {
                    String alias = getNextAlias().toUpperCase();
                    if (!aliases.contains(alias)) {
                        aliases.add(alias);
                        selectExpressionItem.setAlias(new Alias(alias));
                        break;
                    }
                }
            }
        }
        return null;
    }

    protected String getNextAlias() {
        counter++;
        return prefix + counter;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public T visit(WithItem withItem) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

    @Override
    public T visit(Values aThis) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

    @Override
    public T visit(LateralSubSelect lateralSubSelect) {
        lateralSubSelect.getSelect().accept(this);
        return null;
    }

    @Override
    public T visit(TableStatement tableStatement) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }
}
