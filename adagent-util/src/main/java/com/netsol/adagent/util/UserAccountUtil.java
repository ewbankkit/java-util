/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static com.netsol.adagent.util.beans.BaseData.stringIsNotBlank;

import com.netsol.adagent.util.beans.Pair;

/**
 * User account utilities.
 */
public final class UserAccountUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:36 UserAccountUtil.java NSI";
    
    private UserAccountUtil() {}
    
    /**
     * Split a user name into user account name and domain name.
     * The user name can be a plain user name, a UPN or Windows "down-level logon name". 
     */
    public static Pair<String, String> getUserAccountNameAndDomainName(String userName) {
        String userAccountName = null;
        String domainName = null;

        if (stringIsNotBlank(userName)) {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            CharSequence csUserAccountName = null;
            CharSequence csDomainName = null;
            StringBuilder sb = null;

            csUserAccountName = sb1;
            sb = sb1;
            int len = userName.length();
            Loop:
            for (int i = 0; i < len; i++) {
                char c = userName.charAt(i);
                switch (c) {
                case '\\':
                    // e.g. corpit\kewbank
                    if (sb == sb2) {
                        break Loop;
                    }
                    csDomainName = sb1;
                    csUserAccountName = sb2;
                    sb = sb2;
                    break;

                case '@':
                    // e.g. kewbank@corpit
                    if (sb == sb2) {
                        break Loop;
                    }
                    csDomainName = sb2;
                    csUserAccountName = sb1;
                    sb = sb2;
                    break;

                default:
                    sb.append(c);
                    break;
                }
            }

            if (stringIsNotBlank(csUserAccountName)) {
                userAccountName = csUserAccountName.toString();
            }
            if (stringIsNotBlank(csDomainName)) {
                domainName = csDomainName.toString();
            }
        }

        return Pair.from(userAccountName, domainName);
    }
}
