package com.netsol.adagent.util.jscheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.config.Config;
import com.netsol.adagent.util.httpclient.HttpClientFactory;

public class CheckJs {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:00 CheckJs.java NSI";
    private static final Log log = LogFactory.getLog(CheckJs.class);
    private static final Pattern scriptTagPattern = Pattern.compile("<\\s*script(.*)</\\s*script\\s*>", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
    private static final String collectorJsUrlPrefix = "stats.netsolads.com/jsconfig?pid=";

    
    /**
     * 
     * @param url - page to check for JS code
     * @param prodInstId - product Instance Id used in the js code
     * @param cfg - properties used to Configure a new HttpClient from the HttpClientFactory
     * @return
     * @throws Exception
     */
    public static boolean checkJS(String url, String prodInstId, Config cfg) throws Exception{
        HttpClient httpClient = HttpClientFactory.newHttpClient(cfg);
        return checkJS(url, prodInstId, httpClient);
    }

    /**
     * @param url - page to check for JS code
     * @param prodInstId - product Instance Id used in the js code
     * @param httpClient
     * @return
     * @throws Exception
     */
    public static boolean checkJS(String url, String prodInstId, HttpClient httpClient) throws Exception{
        boolean check = false;
        String htmlContent = getHtml(url, httpClient);

        Matcher scriptTagMatcher = scriptTagPattern.matcher(htmlContent);
        String collectorJsUrl = collectorJsUrlPrefix + prodInstId; //must be an exact match, not using a regex for this.
        
        while(scriptTagMatcher.find()){
            String jsCode = scriptTagMatcher.group(1);
            if(jsCode.indexOf(collectorJsUrl) >= 0){
                
                check = true;
                break;
            }
        }
        if(check){
            log.debug("Collector Javascript code was found");
        }
        else{
            log.debug("Collector Javascript code was Not found");
            if(htmlContent.indexOf(collectorJsUrl) >= 0){
                log.error("JS Stats link was found outside of script tag!!!!!:  "+url);
            }
        }
        return check;
    }



    private static String getHtml(String url, HttpClient httpClient) throws Exception{
        GetMethod get = null;
        String pageContent = null;
        try{
            get = new GetMethod(url);
            get.setRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            log.debug("Getting Html content: "+url );
            httpClient.executeMethod(get);
            log.debug("Get Complete ");
            pageContent = get.getResponseBodyAsString();      
        }
        finally{
            if(get != null) try{
                get.releaseConnection();   
            }catch(Exception e){
                log.error("Could not release http connection: "+e, e);
            }
        }
        return pageContent;
    }
}
