/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import static com.github.ewbankkit.util.beans.BaseData.arrayIsEmpty;
import static com.github.ewbankkit.util.beans.BaseData.arrayIsNotEmpty;
import static com.github.ewbankkit.util.beans.BaseData.objectsEqual;
import static com.github.ewbankkit.util.beans.BaseData.stringIsBlank;
import static com.github.ewbankkit.util.beans.BaseData.stringIsNotBlank;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.github.ewbankkit.util.A1;
import com.github.ewbankkit.util.AccessibleObjectUtil;
import com.github.ewbankkit.util.F1;
import com.github.ewbankkit.util.FieldUtil;
import com.github.ewbankkit.util.dbhelpers.BaseHelper;

/**
 * Dirty fields holder.
 * Dirty fields must be annotated with ColumName.
 */
/* package-private */ class DirtyFieldsHolder {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:53 DirtyFieldsHolder.java NSI";

    private static final Map<Class<?>, DirtyFieldsProcessor> typeToDirtyFieldsProcessorMap = new HashMap<Class<?>, DirtyFieldsProcessor>();

    private final boolean checkForEquality;
    private final Collection<Field> dirtyFields = new LinkedHashSet<Field>(); // Eliminate duplicates.
    private final Object object;

    /**
     * Constructor.
     */
    public DirtyFieldsHolder(Object object, boolean checkForEquality) {
        this.checkForEquality = checkForEquality;
        this.object = object;
    }

    /**
     * Clear the current dirty fields.
     */
    public void clearDirtyFields() {
        dirtyFields.clear();
    }

    /**
     * Are there any dirty field?
     */
    public boolean hasDirtyFields() {
        return !dirtyFields.isEmpty();
    }

    /**
     * Set the specified dirty field to the specified value.
     */
    public void setDirtyField(String fieldName, final Object value) {
        try {
            Field field = FieldUtil.getField(object, fieldName);
            if (field == null) {
                throw new NoSuchFieldException(fieldName);
            }

            Boolean addField = AccessibleObjectUtil.withToggledAccessibility(field, new F1<Field, Boolean>() {
                @Override
                public Boolean apply(Field field) throws Exception {
                    if (checkForEquality && objectsEqual(value, field.get(object))) {
                        return Boolean.FALSE;
                    }
                    field.set(object, value);
                    return Boolean.TRUE;
                }});
            if (Boolean.TRUE.equals(addField)) {
                dirtyFields.add(field);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Return the SQL statement snippet for UPDATE values.
     */
    public CharSequence getUpdateValuesSnippet() {
        return getDirtyFieldsProcessor().getUpdateValuesSnippet(this);
    }

    /**
     * Set UPDATE parameters.
     * Return the next parameter index.
     */
    public int setUpdateParameters(PreparedStatement statement, int initialParameterIndex) throws SQLException {
        return getDirtyFieldsProcessor().setUpdateParameters(statement, initialParameterIndex, this);
    }

    /**
     * Return the dirty fields processor
     */
    private DirtyFieldsProcessor getDirtyFieldsProcessor() {
        synchronized(DirtyFieldsHolder.class) {
            Class<?> clazz = object.getClass();
            DirtyFieldsProcessor dirtyFieldsProcessor = DirtyFieldsHolder.typeToDirtyFieldsProcessorMap.get(clazz);
            if (dirtyFieldsProcessor == null) {
                dirtyFieldsProcessor = new DirtyFieldsProcessor(clazz);
                DirtyFieldsHolder.typeToDirtyFieldsProcessorMap.put(clazz, dirtyFieldsProcessor);
            }

            return dirtyFieldsProcessor;
        }
    }

    /**
     * Dirty field processor.
     */
    private static class DirtyFieldsProcessor {
        private final Map<Field, String> fieldToColumnNameMap;

        /**
         * Constructor.
         */
        public DirtyFieldsProcessor(Class<?> clazz) {
            Map<Field, String> fieldToColumnNameMap = new HashMap<Field, String>();

            List<Class<?>> classes = new ArrayList<Class<?>>();
            while (clazz != null) {
                classes.add(clazz);
                clazz = clazz.getSuperclass();
            }
            if (!classes.isEmpty()) {
                Map<String, Option<String>> fieldNameToColumnNameOverrideMap = new HashMap<String, Option<String>>();
                // Overrides are built from the bottom up.
                for (int i = classes.size() - 1; i >= 0; i--) {
                    ColumnNameOverride columnNameOverride = classes.get(i).getAnnotation(ColumnNameOverride.class);
                    if ((columnNameOverride != null) && arrayIsNotEmpty(columnNameOverride.value())) {
                        // The overrides are specified as an array of strings of the form fieldName/columnName.
                        for (String override : columnNameOverride.value()) {
                            if (stringIsBlank(override)) {
                                continue;
                            }
                            String[] parts = override.split("/");
                            if (arrayIsEmpty(parts) || (parts.length > 2)) {
                                continue;
                            }
                            Option<String> columnNameMaybe = Option.none();
                            if (parts.length == 2) {
                                if (stringIsNotBlank(parts[1])) {
                                    columnNameMaybe = Option.some(parts[1]);
                                }
                            }
                            fieldNameToColumnNameOverrideMap.put(parts[0], columnNameMaybe);
                        }
                    }
                }

                // Column names are built from the top down.
                for (int i = 0; i < classes.size(); i++) {
                    for (Field field : classes.get(i).getDeclaredFields()) {
                        ColumnName columnName = field.getAnnotation(ColumnName.class);
                        if (columnName != null) {
                            String columnNameValue = columnName.value();
                            if (stringIsNotBlank(columnNameValue)) {
                                String fieldName = field.getName();
                                Option<String> columnNameOverrideMaybe = fieldNameToColumnNameOverrideMap.get(fieldName);
                                if (columnNameOverrideMaybe == null) {
                                    // No override.
                                    fieldToColumnNameMap.put(field, columnNameValue);
                                }
                                else if (columnNameOverrideMaybe.isSome()) {
                                    fieldToColumnNameMap.put(field, columnNameOverrideMaybe.getValue());
                                }
                            }
                        }
                    }
                }
            }

            this.fieldToColumnNameMap = Collections.unmodifiableMap(fieldToColumnNameMap);
        }

        /**
         * Return the SQL statement snippet for UPDATE values.
         */
        public CharSequence getUpdateValuesSnippet(DirtyFieldsHolder dirtyFieldsHolder) {
            Collection<String> columnNames = new ArrayList<String>(dirtyFieldsHolder.dirtyFields.size());
            for (Field dirtyField : dirtyFieldsHolder.dirtyFields) {
                String columnName = fieldToColumnNameMap.get(dirtyField);
                if (columnName == null) {
                    continue;
                }
                columnNames.add(columnName);
            }

            return BaseHelper.getUpdateValuesSnippet(columnNames);
        }

        /**
         * Set UPDATE parameters.
         * Return the next parameter index.
         */
        public int setUpdateParameters(PreparedStatement statement, int initialParameterIndex, final DirtyFieldsHolder dirtyFieldsHolder) throws SQLException {
            final Collection<Object> values = new ArrayList<Object>(dirtyFieldsHolder.dirtyFields.size());
            for (Field dirtyField : dirtyFieldsHolder.dirtyFields) {
                String columnName = fieldToColumnNameMap.get(dirtyField);
                if (columnName == null) {
                    continue;
                }
                try {
                    AccessibleObjectUtil.withToggledAccessibility(dirtyField, new A1<Field>() {
                        @Override
                        public void apply(Field field) throws Exception {
                            values.add(field.get(dirtyFieldsHolder.object));
                        }});
                }
                catch (Exception ex) {
                    throw new SQLException(ex.toString());
                }
            }

            return BaseHelper.setUpdateParameters(statement, initialParameterIndex, values);
        }
    }
}
