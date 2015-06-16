/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

/**
 * Represents a search phrase.
 */
public class SearchPhrase extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:06 SearchPhrase.java NSI";
    
    public static final String DEFAULT_TRACKING_NUMBER = "";
    
    private static final boolean DEFAULT_ELIMINATE_DUPLICATES = false;
    private static final String DEFAULT_SEARCH_PHRASE_DELIMITER_REGEX = "[,\\r\\n;]";
    private static final Pattern DEFAULT_SEARCH_PHRASE_DELIMITER_PATTERN = Pattern.compile(SearchPhrase.DEFAULT_SEARCH_PHRASE_DELIMITER_REGEX);
    private static final CharSequence DEFAULT_SEARCH_PHRASE_SEPARATOR = ",";
    private static final Pattern SEARCH_WORD_DELIMITER_PATTERN = Pattern.compile("\\s");

    private String keywords;
    private String prodInstId;
    private int searchEngineId;
    private String trackingNumber = SearchPhrase.DEFAULT_TRACKING_NUMBER;
    
    /**
     * Constructor.
     */
    public SearchPhrase() {
        super();
        
        return;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return this.prodInstId;
    }

    public void setSearchEngineId(int searchEngineId) {
        this.searchEngineId = searchEngineId;
    }

    public int getSearchEngineId() {
        return this.searchEngineId;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getTrackingNumber() {
        return this.trackingNumber;
    }

    /**
     * Return a collection of normalized search keywords from stored keywords. 
     */
    public static Collection<String> getSearchKeywords(String keywords) {
        return SearchPhrase.getSearchKeywords(keywords, SearchPhrase.DEFAULT_ELIMINATE_DUPLICATES);
    }
    
    /**
     * Return a collection of normalized search keywords from stored keywords. 
     */
    public static Collection<String> getSearchKeywords(String keywords, boolean eliminateDuplicates) {
        return SearchPhrase.getSearchKeywords(keywords, SearchPhrase.DEFAULT_SEARCH_PHRASE_DELIMITER_REGEX, eliminateDuplicates);
    }
    
    /**
     * Return a collection of normalized search keywords from stored keywords. 
     */
    public static Collection<String> getSearchKeywords(String keywords, String searchPhraseDelimiterRegex) {
        return SearchPhrase.getSearchKeywords(keywords, searchPhraseDelimiterRegex, SearchPhrase.DEFAULT_ELIMINATE_DUPLICATES);
    }
    
    /**
     * Return a collection of normalized search keywords from stored keywords. 
     */
    public static Collection<String> getSearchKeywords(String keywords, String searchPhraseDelimiterRegex, boolean eliminateDuplicates) {
        if (keywords == null) {
            return Collections.emptyList();
        }
        
        Pattern searchPhraseDelimiterPattern =
            SearchPhrase.DEFAULT_SEARCH_PHRASE_DELIMITER_REGEX.equals(searchPhraseDelimiterRegex) ?
                    SearchPhrase.DEFAULT_SEARCH_PHRASE_DELIMITER_PATTERN : Pattern.compile(searchPhraseDelimiterRegex);

        // Use a LinkedHashSet to guarantee that there are no duplicates
        // and iteration order is equal to insertion order.
        Collection<String> searchKeywords = new LinkedHashSet<String>();
        for (String searchKeyword : searchPhraseDelimiterPattern.split(keywords)) {
            // Discard empty search phrases.
            searchKeyword = SearchPhrase.normalizeSearchKeyword(searchKeyword, eliminateDuplicates);
            if (searchKeyword == null) {
                continue;
            }
            
            searchKeywords.add(searchKeyword);
        }
        
        return Collections.unmodifiableCollection(searchKeywords);
    }
    
    /**
     * Return a stored keywords string from a collection of search keywords.
     */
    public static String getSearchKeywords(Collection<String> keywords) {
        return SearchPhrase.getSearchKeywords(keywords, SearchPhrase.DEFAULT_ELIMINATE_DUPLICATES);
    }
    
    /**
     * Return a stored keywords string from a collection of search keywords.
     */
    public static String getSearchKeywords(Collection<String> keywords, boolean eliminateDuplicates) {
        return SearchPhrase.getSearchKeywords(keywords, SearchPhrase.DEFAULT_SEARCH_PHRASE_SEPARATOR, eliminateDuplicates);
    }
    
    /**
     * Return a stored keywords string from a collection of search keywords.
     */
    public static String getSearchKeywords(Collection<String> keywords, CharSequence searchPhraseSeparator) {
        return SearchPhrase.getSearchKeywords(keywords, searchPhraseSeparator, SearchPhrase.DEFAULT_ELIMINATE_DUPLICATES);
    }
    
    /**
     * Return a stored keywords string from a collection of search keywords.
     */
    public static String getSearchKeywords(Collection<String> keywords, CharSequence searchPhraseSeparator, boolean eliminateDuplicates) {
        if ((keywords == null) || keywords.isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (String keyword : keywords) {
            // Discard empty search phrases.
            keyword = SearchPhrase.normalizeSearchKeyword(keyword, eliminateDuplicates);
            if (keyword == null) {
                continue;
            }
            
            sb.append(keyword).append(searchPhraseSeparator);
        }
        int length = sb.length();
        if (length == 0) {
            return null;
        }
        // Remove trailing comma.
        sb.setLength(length - 1);
        
        return sb.toString();
    }

    /**
     * Return a normalized keyword.
     * A normalized keyword is lower-case and has extra whitespace removed. 
     */
    public static String normalizeSearchKeyword(String keyword, boolean eliminateDuplicates) {
        if (keyword == null) {
            return null;
        }
        keyword = keyword.trim();
        if (keyword.length() == 0) {
            return null;
        }
        
        // Split the search phrase into a set of search words.
        Collection<String> searchWords = eliminateDuplicates ? new LinkedHashSet<String>() : new ArrayList<String>();
        // Split at whitespace.
        for (String searchWord : SearchPhrase.SEARCH_WORD_DELIMITER_PATTERN.split(keyword)) {
            if (searchWord.length() == 0) {
                continue;
            }
            
            // Add words in lower case.
            searchWords.add(searchWord.toLowerCase());
        }
        
        // Paste the search words back together to form the normalized search phrase.
        StringBuilder sb = new StringBuilder();
        for (String searchWord : searchWords) {
            sb.append(searchWord).append(' ');
        }
        int length = sb.length();
        if (length == 0) {
            return null;
        }
        // Remove trailing space.
        sb.setLength(length - 1);

        return sb.toString();
    }
    
    public static void main(String[] args) {
        try {
            String keywords = " Hello there ,and        also  GoodBye\n,\r,,     ;hello  THERE";
            Collection<String> searchPhrases = SearchPhrase.getSearchKeywords(keywords);
            System.out.print(BaseData.toString(searchPhrases));
            System.out.println(SearchPhrase.getSearchKeywords(searchPhrases));
            System.out.println(SearchPhrase.getSearchKeywords(Arrays.asList("Alpha", " Omega   Seven", "")));
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
