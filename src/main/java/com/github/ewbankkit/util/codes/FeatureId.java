/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.codes;

import static com.github.ewbankkit.util.beans.BaseData.arrayIsNotEmpty;
import static com.github.ewbankkit.util.beans.BaseData.toIterable;

import java.util.Collection;

import com.github.ewbankkit.util.Predicate;
import com.github.ewbankkit.util.beans.BaseData;

// Feature IDs from the GDB feature table.
public final class FeatureId {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:25 FeatureId.java NSI";

    public static final int BUSINESS_PROFILE = 1;
    public static final int PAID_LOCAL_SUBMISSION = 2;
    public static final int SEO_REPORT = 3;
    public static final int SEO_BASIC = 4;
    public static final int CALL_TRACKING = 5;
    public static final int CLICK_TRACKING = 6;
    public static final int PPC = 7;
    public static final int SEO = 8;
    public static final int PCT = 9;

    private FeatureId() {}

    /**
     * Return whether or not any of the specified features is enabled.
     */
    public static boolean anyFeatureIsEnabled(final int[] featureIds, int... features) {
        if (BaseData.arrayIsNotEmpty(features)) {
            for (int i = 0; i < features.length; i++) {
                if (BaseData.arrayContains(featureIds, features[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return whether or not any of the specified features is enabled.
     */
    public static boolean anyFeatureIsEnabled(final Collection<Integer> featureIds, int... features) {
        if (BaseData.collectionIsEmpty(featureIds)) {
            return false;
        }
        return BaseData.any(BaseData.toIterable(features), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer feature) {
                return featureIds.contains(feature);
            }
        });
    }

    /**
     * Return whether or not the specified feature is enabled.
     */
    public static boolean featureIsEnabled(int[] featureIds, int feature) {
        return anyFeatureIsEnabled(featureIds, feature);
    }

    /**
     * Return whether or not the specified feature is enabled.
     */
    public static boolean featureIsEnabled(Collection<Integer> featureIds, int feature) {
        return anyFeatureIsEnabled(featureIds, feature);
    }

    /**
     * Return whether or not the specified feature is the sole feature.
     */
    public static boolean featureIsSoleFeature(int[] featureIds, int feature) {
        return (featureIds != null) && (featureIds.length == 1) && (featureIds[0] == feature);
    }

    /**
     * Return whether or not the specified feature is the sole feature.
     */
    public static boolean featureIsSoleFeature(Collection<Integer> featureIds, int feature) {
        return (featureIds != null) && (featureIds.size() == 1) && featureIds.contains(Integer.valueOf(feature));
    }
}
