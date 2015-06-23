/*
 * //
 * //  ApplicationLocale
 * //  Created by Ali, Sarmad on 4/24/15 1:21 PM
 * //  Copyright (c) 2015 Capital One. All rights reserved.
 * //  Modified on 4/24/15 1:21 PM
 *
 */

package com.capitalone.cardcompanion.common;

import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by hfg971 on 4/24/15.
 */
@Slf4j
public final class ApplicationLocale {

    private static final ThreadLocal<ApplicationLocale> ATLS                                 = new ThreadLocal<>();
    private static final String                         ACCEPTABLE_LANGUAGES_KEY            = "acceptableLanguages";
    private static final String                         APPLICATION_ID_KEY                  = "applicationId";
    private static final Logger LOGGER               =  LoggerFactory.getLogger(ApplicationLocale.class);
    private static final List<String>                   CANADA_APP_IDS                      = Config.getInstance().getList("digitalWallet.APPIds.ca").get();
    private static final List<String>                   US_APP_IDS                          = Config.getInstance().getList("digitalWallet.APPIds.us").get();

    private  final List<Locale>         acceptableLanguages;



    private  final String               applicationId;

    private final HystrixRequestContext hystrixRequestContext;

    ApplicationLocale(List<Locale> acceptableLanguages,String applicationId)
    {
        this.acceptableLanguages = acceptableLanguages;
        this.applicationId = applicationId;
        hystrixRequestContext = HystrixRequestContext.initializeContext();
    }

    @Nullable
    public static ApplicationLocale getApplicationLocaleForCurrentThread() {
        LOGGER.debug("Current thread name when getting the applicationLocale::" + Thread.currentThread().getName());
        return ATLS.get();
    }

    public static ApplicationLocale initializeApplicationLocale() {
        return initializeApplicationLocale(null, null);
    }

    public static ApplicationLocale initializeApplicationLocale(
            @Nullable
            List<Locale> acceptableLanguages,
            @Nullable
            String     applicationId
    ) {
        ApplicationLocale applicationLocale = new ApplicationLocale(acceptableLanguages,applicationId);
        setApplicationLocaleOnCurrentThread(applicationLocale);
        return applicationLocale;
    }



    public static void removeApplicationLocaleFromCurrentThread() {
        MDC.clear();

        HystrixRequestContext.setContextOnCurrentThread(null);

        ATLS.remove();
    }

    public static void setApplicationLocaleOnCurrentThread(@Nullable ApplicationLocale applicationLocale) {
        if (applicationLocale != null) {

            LOGGER.debug("Current thread name when setting the applicationLocale::" + Thread.currentThread().getName());

            ATLS.set(applicationLocale);

            HystrixRequestContext.setContextOnCurrentThread(applicationLocale.hystrixRequestContext);

            MDC.put(APPLICATION_ID_KEY, applicationLocale.applicationId);

        }
    }

    public void shutdown() {
        hystrixRequestContext.shutdown();
        removeApplicationLocaleFromCurrentThread();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public List<Locale> getAcceptableLanguages() {
        return acceptableLanguages;
    }

    /***
     * This method returns the locale based on some rules
     * using acceptableLanguages and applicationId
     * @return Locale
     */
    public Locale getLocaleForCurrentRequest()
    {
        if(this.applicationId != null)
            /*** Canadian Rules ***/ {
            if (CANADA_APP_IDS.contains(this.applicationId)) {
                /*** Mobile Application is Canadian ***/
                if (this.acceptableLanguages != null && this.acceptableLanguages.size() > 0) {
                    /*** Go over all the preferred languages and look if en or fr ***/
                    for (Locale preferredLanguage : this.acceptableLanguages) {
                        if (preferredLanguage.getLanguage().equals(new Locale("en").getLanguage())) {
                            return Locale.CANADA;
                        }

                        if (preferredLanguage.getLanguage().equals(new Locale("fr").getLanguage())) {
                            return Locale.CANADA_FRENCH;
                        }

                    }

                    // We default to english canada if no preferredLanguage matches above rules
                    return Locale.CANADA;
                } else {   // We default to english canada if no acceptableLanguages are provided
                    return Locale.CANADA;
                }
            }
            /*** US Rules ***/
            if(US_APP_IDS.contains(this.applicationId))
            {
                /*** Mobile Application is US ***/
                return Locale.US;
            }
        }

        /*** Use the first preferredLanguage ***/
        if (this.acceptableLanguages != null && this.acceptableLanguages.size() > 0) {

            return this.acceptableLanguages.get(0);
        }

        // We will use the system default locale if no locale is found from above rules
        return Locale.getDefault();
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
        ApplicationLocale that = (ApplicationLocale)obj;
        return Objects.equals(applicationId, that.applicationId) &&
                Objects.equals(acceptableLanguages, that.acceptableLanguages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId,acceptableLanguages);
    }

    @Override
    public String toString() {
        return ReflectiveRepresentation.toString(this);
    }

    /**
     * This function tells weather the request is made from Canadian Application
     * @return
     */
    public boolean isCanadianApplication() {
         return (this.applicationId != null && CANADA_APP_IDS.contains(this.applicationId));
    }

    /**
     * This function tells weather the request is made from US Application
     * @return
     */
    public boolean isUSApplication() {
        return (this.applicationId != null && US_APP_IDS.contains(this.applicationId));
    }



}
