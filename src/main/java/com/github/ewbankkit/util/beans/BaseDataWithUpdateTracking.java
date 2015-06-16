/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Base class for objects that have updates tracked.
 * Fields that are to be tracked must be annotated with ColumName.
 */
public abstract class BaseDataWithUpdateTracking extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:50 BaseDataWithUpdateTracking.java NSI";

    private final boolean checkForEquality;
    private final DirtyFieldsHolder dirtyFieldsHolder;

    /**
     * Constructor.
     */
    protected BaseDataWithUpdateTracking() {
        this(false);
    }

    /**
     * Constructor.
     */
    protected BaseDataWithUpdateTracking(boolean checkForEquality) {
        this.checkForEquality = checkForEquality;
        dirtyFieldsHolder = new DirtyFieldsHolder(this, checkForEquality);
    }

    /**
     * Is equality checked when evaluating whether a tracked field has changed.
     */
    public boolean checkForEquality() {
        return checkForEquality;
    }

    /**
     * Clear any tracked updates.
     */
    public void clearTrackedUpdates() {
        dirtyFieldsHolder.clearDirtyFields();
    }

    /**
     * Has any tracked field been updated?
     */
    public boolean hasTrackedUpdates() {
        return dirtyFieldsHolder.hasDirtyFields();
    }

    /**
     * Return the SQL statement snippet for UPDATE values.
     */
    @NotInToString
    public CharSequence getUpdateValuesSnippet() {
        return dirtyFieldsHolder.getUpdateValuesSnippet();
    }

    /**
     * Set UPDATE parameters.
     * Return the next parameter index.
     */
    public int setUpdateParameters(PreparedStatement statement, int initialParameterIndex) throws SQLException {
        return dirtyFieldsHolder.setUpdateParameters(statement, initialParameterIndex);
    }

    /**
     * Set the specified tracked field to the specified value.
     */
    protected void setTrackedField(String fieldName, boolean value) {
        setTrackedField(fieldName, Boolean.valueOf(value));
    }

    /**
     * Set the specified tracked field to the specified value.
     */
    protected void setTrackedField(String fieldName, byte value) {
        setTrackedField(fieldName, Byte.valueOf(value));
    }

    /**
     * Set the specified tracked field to the specified value.
     */
    protected void setTrackedField(String fieldName, char value) {
        setTrackedField(fieldName, Character.valueOf(value));
    }

    /**
     * Set the specified tracked field to the specified value.
     */
    protected void setTrackedField(String fieldName, double value) {
        setTrackedField(fieldName, Double.valueOf(value));
    }

    /**
     * Set the specified tracked field to the specified value.
     */
    protected void setTrackedField(String fieldName, float value) {
        setTrackedField(fieldName, Float.valueOf(value));
    }

    /**
     * Set the specified tracked field to the specified value.
     */
    protected void setTrackedField(String fieldName, int value) {
        setTrackedField(fieldName, Integer.valueOf(value));
    }

    /**
     * Set the specified tracked field to the specified value.
     */
    protected void setTrackedField(String fieldName, long value) {
        setTrackedField(fieldName, Long.valueOf(value));
    }

    /**
     * Set the specified tracked field to the specified value.
     */
    protected void setTrackedField(String fieldName, Object value) {
        dirtyFieldsHolder.setDirtyField(fieldName, value);
    }

    /**
     * Set the specified tracked field to the specified value.
     */
    protected void setTrackedField(String fieldName, short value) {
        setTrackedField(fieldName, Short.valueOf(value));
    }
}
