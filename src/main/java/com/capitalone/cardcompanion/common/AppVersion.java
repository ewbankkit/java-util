//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * App version.
 */
public final class AppVersion {
    // Version string is of the format major.minor[.hotfix]
    // Allow non-digit characters after the hotifx version.
    private static final Pattern PATTERN = Pattern.compile("\\A(\\d+)\\.(\\d+)(\\z|(\\.(\\d+)(.*)\\z))");

    public static final AppVersion UNKNOWN = new AppVersion(0, 0, 0);

    private final int hotfixVersionNumber;
    private final int majorVersionNumber;
    private final int minorVersionNumber;

    private AppVersion(int majorVersionNumber, int minorVersionNumber, int hotfixVersionNumber) {
        this.majorVersionNumber = majorVersionNumber;
        this.minorVersionNumber = minorVersionNumber;
        this.hotfixVersionNumber = hotfixVersionNumber;
    }

    public static AppVersion fromVersionString(String versionString) {
        Preconditions.checkNotNull(versionString);

        Matcher matcher = PATTERN.matcher(versionString);
        if (matcher.matches()) {
            return new AppVersion(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                (matcher.group(5) == null) ? 0 : Integer.parseInt(matcher.group(5))
            );
        }
        throw new IllegalArgumentException(versionString);
    }

    public int getHotfixVersionNumber() {
        return hotfixVersionNumber;
    }

    public int getMajorVersionNumber() {
        return majorVersionNumber;
    }

    public int getMinorVersionNumber() {
        return minorVersionNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        AppVersion that = (AppVersion)obj;
        return (majorVersionNumber == that.majorVersionNumber) &&
               (minorVersionNumber == that.minorVersionNumber) &&
               (hotfixVersionNumber == that.hotfixVersionNumber);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
            append(majorVersionNumber).
            append(minorVersionNumber).
            append(hotfixVersionNumber).
            hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(majorVersionNumber).
            append('.').append(minorVersionNumber).
            append('.').append(hotfixVersionNumber);
        return sb.toString();
    }
}
