/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2026 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.select;

import java.io.Serializable;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

public class MySqlSelectIntoClause extends ASTNodeAccessImpl implements Serializable {

    public enum Position {
        BEFORE_FROM, TRAILING
    }

    public enum Type {
        OUTFILE, DUMPFILE
    }

    public enum FieldsKeyword {
        FIELDS, COLUMNS
    }

    private Position position = Position.TRAILING;
    private Type type;
    private StringValue fileName;
    private String characterSet;
    private FieldsKeyword fieldsKeyword;
    private StringValue fieldsTerminatedBy;
    private boolean fieldsOptionallyEnclosed;
    private StringValue fieldsEnclosedBy;
    private StringValue fieldsEscapedBy;
    private StringValue linesStartingBy;
    private StringValue linesTerminatedBy;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public MySqlSelectIntoClause withPosition(Position position) {
        this.setPosition(position);
        return this;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public StringValue getFileName() {
        return fileName;
    }

    public void setFileName(StringValue fileName) {
        this.fileName = fileName;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    public FieldsKeyword getFieldsKeyword() {
        return fieldsKeyword;
    }

    public void setFieldsKeyword(FieldsKeyword fieldsKeyword) {
        this.fieldsKeyword = fieldsKeyword;
    }

    public StringValue getFieldsTerminatedBy() {
        return fieldsTerminatedBy;
    }

    public void setFieldsTerminatedBy(StringValue fieldsTerminatedBy) {
        this.fieldsTerminatedBy = fieldsTerminatedBy;
    }

    public boolean isFieldsOptionallyEnclosed() {
        return fieldsOptionallyEnclosed;
    }

    public void setFieldsOptionallyEnclosed(boolean fieldsOptionallyEnclosed) {
        this.fieldsOptionallyEnclosed = fieldsOptionallyEnclosed;
    }

    public StringValue getFieldsEnclosedBy() {
        return fieldsEnclosedBy;
    }

    public void setFieldsEnclosedBy(StringValue fieldsEnclosedBy) {
        this.fieldsEnclosedBy = fieldsEnclosedBy;
    }

    public StringValue getFieldsEscapedBy() {
        return fieldsEscapedBy;
    }

    public void setFieldsEscapedBy(StringValue fieldsEscapedBy) {
        this.fieldsEscapedBy = fieldsEscapedBy;
    }

    public StringValue getLinesStartingBy() {
        return linesStartingBy;
    }

    public void setLinesStartingBy(StringValue linesStartingBy) {
        this.linesStartingBy = linesStartingBy;
    }

    public StringValue getLinesTerminatedBy() {
        return linesTerminatedBy;
    }

    public void setLinesTerminatedBy(StringValue linesTerminatedBy) {
        this.linesTerminatedBy = linesTerminatedBy;
    }

    public boolean hasFieldsClause() {
        return fieldsKeyword != null || fieldsTerminatedBy != null || fieldsEnclosedBy != null
                || fieldsEscapedBy != null;
    }

    public boolean hasLinesClause() {
        return linesStartingBy != null || linesTerminatedBy != null;
    }

    public StringBuilder appendTo(StringBuilder builder) {
        builder.append("INTO ").append(type);
        appendFileName(builder);
        appendCharacterSet(builder);
        appendFieldsClause(builder);
        appendLinesClause(builder);
        return builder;
    }

    private void appendFileName(StringBuilder builder) {
        if (fileName != null) {
            builder.append(" ").append(fileName);
        }
    }

    private void appendCharacterSet(StringBuilder builder) {
        if (characterSet != null) {
            builder.append(" CHARACTER SET ").append(characterSet);
        }
    }

    private void appendFieldsClause(StringBuilder builder) {
        if (!hasFieldsClause()) {
            return;
        }

        builder.append(" ").append(fieldsKeyword != null ? fieldsKeyword : FieldsKeyword.FIELDS);

        if (fieldsTerminatedBy != null) {
            builder.append(" TERMINATED BY ").append(fieldsTerminatedBy);
        }
        if (fieldsEnclosedBy != null) {
            builder.append(" ");
            if (fieldsOptionallyEnclosed) {
                builder.append("OPTIONALLY ");
            }
            builder.append("ENCLOSED BY ").append(fieldsEnclosedBy);
        }
        if (fieldsEscapedBy != null) {
            builder.append(" ESCAPED BY ").append(fieldsEscapedBy);
        }
    }

    private void appendLinesClause(StringBuilder builder) {
        if (!hasLinesClause()) {
            return;
        }

        builder.append(" LINES");
        if (linesStartingBy != null) {
            builder.append(" STARTING BY ").append(linesStartingBy);
        }
        if (linesTerminatedBy != null) {
            builder.append(" TERMINATED BY ").append(linesTerminatedBy);
        }
    }

    @Override
    public String toString() {
        return appendTo(new StringBuilder()).toString();
    }
}
