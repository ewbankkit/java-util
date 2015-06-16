/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify column name overrides for a class.
 * The overrides are specified as an array of strings of the form fieldName/columnName.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ColumnNameOverride {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:52 ColumnNameOverride.java NSI";

    String[] value();
}
