/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.quest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.beans.BaseData;
import com.netsol.adagent.util.beans.SearchPhrase;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;
import com.netsol.adagent.util.log.SimpleLoggable;
import com.netsol.adagent.util.lsv.BusinessCategory;
import com.netsol.adagent.util.lsv.BusinessCategoryDBHelper;
import com.netsol.adagent.util.lsv.BusinessCategoryTypeCd;

import com.netsol.adagent.util.lsv.BusinessProfile;

/**
 * Search phrase generator. 
 */
/* package-private */ class LsvSearchPhraseGenerator extends BaseLoggable {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:21 LsvSearchPhraseGenerator.java NSI";
    
    private final static boolean DEFAULT_INCLUDE_LONG_TAIL_GEO_KEYWORDS = true;
    private final static boolean DEFAULT_INCLUDE_NON_LONG_TAIL_GEO_KEYWORDS = false;
    private final static int DEFAULT_MAX_SEARCH_ENGINE_PHRASES = 25;
    
   
    private boolean includeLongTailGeoKeywords = LsvSearchPhraseGenerator.DEFAULT_INCLUDE_LONG_TAIL_GEO_KEYWORDS;
    private boolean includeNonLongTailGeoKeywords = LsvSearchPhraseGenerator.DEFAULT_INCLUDE_NON_LONG_TAIL_GEO_KEYWORDS;
    private int maxSearchEnginePhrases = LsvSearchPhraseGenerator.DEFAULT_MAX_SEARCH_ENGINE_PHRASES;
    
    /**
     * Constructor.
     */
    public LsvSearchPhraseGenerator(BaseLoggable baseLoggable) {
        super(baseLoggable);
        
        
        
        return;
    }
    
    public void setIncludeLongTailGeoKeywords(boolean includeLongTailGeoKeywords) {
        this.includeLongTailGeoKeywords = includeLongTailGeoKeywords;
    }

    public void setIncludeNonLongTailGeoKeywords(boolean includeNonLongTailGeoKeywords) {
        this.includeNonLongTailGeoKeywords = includeNonLongTailGeoKeywords;
    }

    public void setMaxSearchEnginePhrases(int maxSearchEnginePhrases) {
        this.maxSearchEnginePhrases = maxSearchEnginePhrases;
    }

    /**
     * Generate local search phrases.
     */
    public Collection<LocalSearchPhrase> generateLocalSearchPhrases(String logTag, Connection gdbConnection, BusinessProfile businessProfile) throws SQLException {
        Collection<LocalSearchPhrase> phrases = new ArrayList<LocalSearchPhrase>();
        LocalSearchPhrase phrase = null;
        BusinessCategoryDBHelper businessCategoryHelper = new BusinessCategoryDBHelper(this, logTag);
        
        // Business name.
        String businessName = businessProfile.getBusinessName();
        if (BaseData.stringIsNotBlank(businessName)) {
            phrase = new LocalSearchPhrase();
            phrase.setSearchPhrase(businessName);
            phrase.setSearchPhraseSource(SearchEnginePhrase.BUSINESS_NAME_SOURCE);
            phrases.add(phrase);
        }
        
        // Business phone number.
        String phoneNumber = businessProfile.getPhoneRaw();
        if (BaseData.stringIsNotBlank(phoneNumber) && (phoneNumber.length() == 10)) {
            phrase = new LocalSearchPhrase();
            // XXX XXX XXXX
            phrase.setSearchPhrase(phoneNumber.substring(0, 3) + " " + phoneNumber.substring(3, 6) + " " + phoneNumber.substring(6));
            phrase.setSearchPhraseSource(SearchEnginePhrase.BUSINESS_PHONE_NUMBER_SOURCE);
            phrases.add(phrase);
        }
        
        // Business street address.
        String streetAddress = businessProfile.getStreetAddress1();
        if (BaseData.stringIsNotBlank(streetAddress)) {
            phrase = new LocalSearchPhrase();
            phrase.setSearchPhrase(streetAddress);
            phrase.setSearchPhraseSource(SearchEnginePhrase.BUSINESS_STREET_ADDRESS_SOURCE);
            phrases.add(phrase);
        }
        
        // Business ZIP code.
        String zipCode = businessProfile.getZip5Raw();
        if (BaseData.stringIsNotBlank(zipCode)) {
            phrase = new LocalSearchPhrase();
            phrase.setSearchPhrase(zipCode);
            phrase.setSearchPhraseSource(SearchEnginePhrase.BUSINESS_ZIP_CODE_SOURCE);
            phrases.add(phrase);
        }
        
        List<BusinessCategory> categories = null;
        // Category name.
        if (businessProfile.getCategoryId() != 0) {
            categories =
                businessCategoryHelper.queryParentCategories( gdbConnection, BusinessCategoryTypeCd.NETSOL_CATEGORY_TYPE_CD,  (long)businessProfile.getCategoryId());
            
            if(categories != null && categories.size()>0){
                String categoryName = categories.get(categories.size()-1).getName();
                if (categoryName != null) {
                    phrase = new LocalSearchPhrase();
                    phrase.setSearchPhrase(categoryName);
                    phrase.setSearchPhraseSource(SearchEnginePhrase.BUSINESS_CATEGORY_SOURCE);
                    phrases.add(phrase);
                }
            }
            
            
            
        }
        
        // Location is:
        //  <City>, <State>
        String location = new StringBuilder(businessProfile.getCity()).append(", ").append(businessProfile.getState()).toString();
        for (LocalSearchPhrase lsp : phrases) {
            lsp.setLocation(location);
            lsp.setUrl(businessProfile.getSiteUrl());
        }
        
        return phrases;
    }
    
    /**
     * Generate search engine phrases.
     */
    public Collection<SearchEnginePhrase> generateSearchEnginePhrases(String logTag, Connection gdbConnection, BusinessProfile businessProfile) throws SQLException {
        // Use a LinkedHashSet to guarantee that there are no duplicates
        // and iteration order is equal to insertion order.
        Collection<SearchEnginePhrase> phrases = new LinkedHashSet<SearchEnginePhrase>();
        String city = businessProfile.getCity();
        String state = businessProfile.getState();
        
        BusinessCategoryDBHelper businessCategoryHelper = new BusinessCategoryDBHelper(this, logTag);
        // Domain name.
        String hostName = TrackingUtil.extractHostName(businessProfile.getSiteUrl());
        if (hostName != null) {
            SearchEnginePhrase phrase = new SearchEnginePhrase();
            phrase.setSearchPhrase(hostName);
            phrase.setSearchPhraseSource(SearchEnginePhrase.SITE_URL_SOURCE);
            phrases.add(phrase);
        }
        
        // Business name.
        this.addSearchEnginePhrases(
                phrases,
                businessProfile.getBusinessName(),
                city,
                state,
                SearchEnginePhrase.BUSINESS_NAME_SOURCE);
        
        // Business category name.
        if (businessProfile.getCategoryId() != 0) {
            List<BusinessCategory> categories = null;

            categories = businessCategoryHelper.queryParentCategories( gdbConnection, BusinessCategoryTypeCd.NETSOL_CATEGORY_TYPE_CD,  (long)businessProfile.getCategoryId());
            String categoryName = null;
            if(categories != null && categories.size()>0){
                categoryName = categories.get(categories.size()-1).getName();
            }
            this.addSearchEnginePhrases(
                    phrases,
                    categoryName,
                    city,
                    state,
                    SearchEnginePhrase.BUSINESS_CATEGORY_SOURCE);
        }
        
        // Keywords, brands and specialties.
        for (String keywords : new String[] {businessProfile.getKeywords(), businessProfile.getBrands(), businessProfile.getSpecialities()}) {
            for (String keyword : SearchPhrase.getSearchKeywords(keywords, true)) {
                this.addSearchEnginePhrases(
                        phrases,
                        keyword,
                        city,
                        state,
                        SearchEnginePhrase.DESCRIPTIVE_KEYWORD_SOURCE);
            }
        }
        
        for (SearchEnginePhrase phrase : phrases) {
            phrase.setUrl(businessProfile.getSiteUrl());
        }
        
        return phrases;
    }
    
    /**
     * Add search engine phrases.
     */
    private void addSearchEnginePhrases(Collection<SearchEnginePhrase> phrases, String baseKeyword, String city, String state, String source) {
        String[] keywords = new String[] {
                this.includeNonLongTailGeoKeywords ? baseKeyword : null,
                this.includeLongTailGeoKeywords && BaseData.stringIsNotBlank(baseKeyword) ?
                        this.getLongTailGeoKeyword(baseKeyword, city, state): null
        };
        for (String keyword : keywords) {
            if (phrases.size() >= this.maxSearchEnginePhrases) {
                break;
            }
            if (BaseData.stringIsNotBlank(keyword)) {
                SearchEnginePhrase phrase = new SearchEnginePhrase();
                phrase.setSearchPhrase(keyword);
                phrase.setSearchPhraseSource(source);
                phrases.add(phrase);
            }
        }
        
        return;
    }
    
    private String getLongTailGeoKeyword(String baseKeyword, String city, String state) {
        if (BaseData.stringIsBlank(baseKeyword)) {
            return null;
        }
        
        boolean cityIsBlank = BaseData.stringIsBlank(city);
        boolean stateIsBlank = BaseData.stringIsBlank(state);

        // Look for the city name in the base keyword.
        boolean containsCity = false;
        if (!cityIsBlank) {
            for (String s : baseKeyword.split("\\s")) {
                if (BaseData.stringsEqualIgnoreCase(s, city)) {
                    containsCity = true;
                    break;
                }
            }
        }
        
        StringBuilder sb = new StringBuilder(baseKeyword);
        if (containsCity) {
            if (!stateIsBlank) {
                sb.append(' ').append(state.trim());
            }
        }
        else {
            if (cityIsBlank) {
                if (!stateIsBlank) {
                    sb.append(' ').append(state.trim());
                }
            }
            else {
                sb.append(' ').append(city.trim());
                if (!stateIsBlank) {
                    sb.append(", ").append(state.trim());
                }
            }
        }
        
        return sb.toString();
    }
    
    // Test harness.
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            Connection edbConnection = null;
            try {
                edbConnection = DriverManager.getConnection("jdbc:oracle:thin:async_user/async_user@db2.dev.netsol.com:1521:edb");
                
                String logTag = null; 
                LsvSearchPhraseGenerator lsvSearchPhraseGenerator = new LsvSearchPhraseGenerator(new SimpleLoggable(""));

                BusinessProfile businessProfile = new BusinessProfile();
                System.out.println(BaseData.toString(lsvSearchPhraseGenerator.generateSearchEnginePhrases(logTag, edbConnection, businessProfile)));
                
                businessProfile.setBrands("Pepsi");
                businessProfile.setBusinessName("Ashkan Pizza");
                businessProfile.setCity("Herndon");
                businessProfile.setKeywords("pizza, cheese, herndon pizza");
                businessProfile.setState("VA");
                System.out.println(BaseData.toString(lsvSearchPhraseGenerator.generateSearchEnginePhrases(logTag, edbConnection, businessProfile)));
                
                businessProfile.setPhone("703 456 1234");
                businessProfile.setStreetAddress1("123 Main Street");
                businessProfile.setZip("20194");
                System.out.println(BaseData.toString(lsvSearchPhraseGenerator.generateLocalSearchPhrases(logTag, edbConnection, businessProfile)));
            }
            finally {
                BaseHelper.close(edbConnection);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        
        return;
    }
}
