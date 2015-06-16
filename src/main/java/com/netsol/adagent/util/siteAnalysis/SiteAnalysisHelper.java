package com.netsol.adagent.util.siteAnalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.quest.QuestQueue;
import com.netsol.adagent.util.quest.SearchEnginePhrase;
import com.netsol.adagent.util.siteAnalysis.beans.Keyword;
import com.netsol.adagent.util.siteAnalysis.beans.KeywordPpcData;
import com.netsol.adagent.util.siteAnalysis.beans.Site;
import com.netsol.adagent.vendor.client.factory.AdAgentVendorClientFactory;
import com.netsol.adagent.vendor.client.impl.KeywordSuggestionRequestImpl;
import com.netsol.adagent.vendor.client.impl.KeywordsEstimateImpl;
import com.netsol.adagent.vendor.client.impl.KeywordsEstimateRequestImpl;
import com.netsol.adagent.vendor.client.intf.AdAgentVendorClientProcessor;
import com.netsol.adagent.vendor.client.intf.Credential;
import com.netsol.adagent.vendor.client.intf.KeywordSuggestion;
import com.netsol.adagent.vendor.client.intf.KeywordSuggestions;
import com.netsol.adagent.vendor.client.intf.KeywordsEstimate;

/**
 * Handles keyword suggestions and search volume stats for the Site Analysis tool.
 * Calls Google's TargetingIdea Service, and TrafficEstimator Service
 * @author pmitchel
 */
public class SiteAnalysisHelper {
    private Log log = LogFactory.getLog(this.getClass());
    private String adVendorUrl;
    private String adVendorUserName;
    private String adVendorPassword;
    private int adVendorTimeout;
    private static String MATCH_TYPE = "PHRASE";
    private static final double BID = 5.0;
    
    public SiteAnalysisHelper(String adVendorUrl, String adVendorUserName, String adVendorPassword, int adVendorTimeout){
        this.adVendorUrl      = adVendorUrl;      
        this.adVendorUserName = adVendorUserName; 
        this.adVendorPassword = adVendorPassword; 
        this.adVendorTimeout  = adVendorTimeout;  
    }
    
 
    
    /**
     * Use google TargetingIdeaService API to get keyword suggestions.
     * @param site - A Site object with the city and state populated
     * @param seedKeywords - List of Strings to use as the seed keywords
     * @param numSuggestions - Number of results to return
     * @param updatedByUser
     * @return
     */
    public List<Keyword> getKeywordSuggestions(Site site, String[] seedKeywords, int numSuggestions, String updatedByUser) throws Exception{
        
        KeywordSuggestionRequestImpl kwSuggestionRequest = new KeywordSuggestionRequestImpl();
        kwSuggestionRequest.setUpdatedByUser(updatedByUser);
        kwSuggestionRequest.setSeedKeywords(seedKeywords);
        kwSuggestionRequest.setMatchType(MATCH_TYPE);

        kwSuggestionRequest.setNumberOfResults(numSuggestions);
        kwSuggestionRequest.setRequestType("IDEAS");
        
        log.info("Calling Advendor, getKeywordSuggestions for keyword suggestions");
        KeywordSuggestions kwSuggestions = createAdVendorServiceProcessor().getKeywordSuggestions(createAdVendorCredential(), kwSuggestionRequest);
        log.info("Done");
        
        List<Keyword> keywordSuggestions = new ArrayList<Keyword>();
        for(KeywordSuggestion kwSuggestion: kwSuggestions.getKeywordSuggestions()){
            Keyword kw = new Keyword();
            kw.setBaseKeyword(kwSuggestion.getText().toLowerCase());
            kw.setType(Keyword.Type.SUGGESTION.toString());
            kw.setSiteId(site.getSiteId());
            keywordSuggestions.add(kw);
        }
        
        Keyword.fixupKeywordLongtails(keywordSuggestions, site.getCity(), site.getState());
        
        return keywordSuggestions;
    }
    
    /**
     * Call Google TargetingIdeaService to get Search volume estimates.
     * @param keywords
     * @throws Exception
     */
    public void getKeywordSearchVolume(List<Keyword> keywords, String updatedBy) throws Exception{

        Map<String, Keyword> keywordMap = new HashMap<String, Keyword>();
        for(Keyword siteKw: keywords){
            keywordMap.put(siteKw.getKeyword().toLowerCase(), siteKw);
        }
        Set<String> seedKeywords = keywordMap.keySet();
        
        KeywordSuggestionRequestImpl kwSuggestionRequest = new KeywordSuggestionRequestImpl();
        kwSuggestionRequest.setUpdatedByUser(updatedBy);
        kwSuggestionRequest.setSeedKeywords(seedKeywords.toArray(new String[seedKeywords.size()]));
        kwSuggestionRequest.setMatchType(MATCH_TYPE);
        kwSuggestionRequest.setRequestType("STATS");
        kwSuggestionRequest.setNumberOfResults(keywords.size());
        
        log.info("Calling Advendor, getKeywordSuggestions for search volume stats");
        KeywordSuggestions kwSuggestions = createAdVendorServiceProcessor().getKeywordSuggestions(createAdVendorCredential(), kwSuggestionRequest);
        log.info("Done");
        for(KeywordSuggestion kwSuggestion: kwSuggestions.getKeywordSuggestions()){
            Keyword kw = keywordMap.get(kwSuggestion.getText().toLowerCase());
            if(kw != null){
                kw.setSearchVolume(kwSuggestion.getSearchVolume());
            }
        }
    }

    
    /**
     * Call Google TrafficEstimatorServiceAPI to get CPC Estimates for target rankings.
     * @param keywords
     * @throws Exception
     */
    public void getCPCEstimate(List<Keyword> keywords, String updatedBy) throws Exception {
   
        Map<String, Keyword> keywordMap = new HashMap<String, Keyword>(); 
        List<KeywordsEstimate> kwEstimates = new ArrayList<KeywordsEstimate>();
        for(Keyword siteKw: keywords){            
            keywordMap.put(siteKw.getKeyword().toLowerCase(), siteKw);
            KeywordsEstimate kwEst = new KeywordsEstimateImpl();
            kwEst.setKeyword(siteKw.getKeyword());
            kwEst.setKeywordType(MATCH_TYPE);
            kwEst.setCpc(BID);
            kwEstimates.add(kwEst);
        }
        
    
        KeywordsEstimateRequestImpl kwEstRequest = new KeywordsEstimateRequestImpl();
        kwEstRequest.setUpdatedByUser(updatedBy);        
        kwEstRequest.setKeywordsEstimate(kwEstimates.toArray(new KeywordsEstimate[kwEstimates.size()]));
        
        log.info("Calling Advendor, getKeywordTrafficEstimate");
        KeywordsEstimate[] kwEstResponses = createAdVendorServiceProcessor().getKeywordTrafficEstimate(createAdVendorCredential(), kwEstRequest);
        log.info("Done");
        
        for(KeywordsEstimate kwEstResponse: kwEstResponses){
            Keyword kw = keywordMap.get(kwEstResponse.getKeyword().toLowerCase());
            if(kw != null){
                KeywordPpcData kwData = new KeywordPpcData();
                kwData.setTargetPosition(1.0);
                kwData.setActualPosition(kwEstResponse.getPosition());
                kwData.setBid(BID);
                kwData.setCpc(kwEstResponse.getCpc());                
                kw.setKeywordPpcData(Arrays.asList(kwData));
            } 
        }
    }
    
    
    /**
     * Submit site's keywords to WAF for keyword ranking job.
     * @param site
     * @param questQueueUrl
     * @param updatedBy
     * @throws Exception
     */
    public void submitWafRankingRequest(Site site, String questQueueUrl, String updatedBy) throws Exception{
  
        List<Keyword> keywords = site.getKeywords();
        List<SearchEnginePhrase> searchEnginePhraseList = new ArrayList<SearchEnginePhrase>();
        for (Keyword keyword : keywords) {
            if (keyword.getWafJobId() == null) {
                SearchEnginePhrase phrase = new SearchEnginePhrase();
                phrase.setSearchPhrase(keyword.getKeyword());
                phrase.setUrl(site.getUrl());
                phrase.setSearchPhraseSource(mapKeywordTypeToSearchEnginePhraseSource(keyword.getType()));
                
                searchEnginePhraseList.add(phrase);
            }
        }

        if (!searchEnginePhraseList.isEmpty()) {
            // Submit the keyword ranking request to WAF/Quest.                
            QuestQueue questQueue = new QuestQueue(log, questQueueUrl);
            long jobId = questQueue.submitLQTSearchEnginePhraseJob(updatedBy, searchEnginePhraseList, updatedBy); 
            
            // Update the WAF/Quest job ID.
            for (Keyword siteKeyword : keywords) {
                if (siteKeyword.getWafJobId() == null) {
                    siteKeyword.setWafJobId(jobId);
                }
            }
        }
        
    }
    
    /**
     * Map site keyword type to Quest search engine phrase source type.
     * 
     * @param type
     * @return the source type string
     */
    private String mapKeywordTypeToSearchEnginePhraseSource(String type) {
        String source = SearchEnginePhrase.DESCRIPTIVE_KEYWORD_SOURCE;
        Keyword.Type typeValue = Keyword.Type.valueOf(type);
        if (typeValue.equals(Keyword.Type.BUSINESS_NAME)) {
            source = SearchEnginePhrase.BUSINESS_NAME_SOURCE;
        }
        else if (typeValue.equals(Keyword.Type.META)) {
            source = SearchEnginePhrase.META_TAG_KEYWORD_SOURCE;
        }
        return source;
    }
    
    
    /**
     * Create the ad vendor service credentials.
     */
    public com.netsol.adagent.vendor.client.intf.Credential createAdVendorCredential() throws Exception {
        Credential adVendorCredential = AdAgentVendorClientFactory.getCredentialInstance();
        adVendorCredential.setUserName(adVendorUserName);
        adVendorCredential.setPassword(adVendorPassword);
        return adVendorCredential;
    }

    /**
     * Create the ad vendor service processor.
     */
    public AdAgentVendorClientProcessor createAdVendorServiceProcessor() throws Exception {
        return AdAgentVendorClientFactory.getProcessorInstance(adVendorUrl, adVendorTimeout);
    }
}
