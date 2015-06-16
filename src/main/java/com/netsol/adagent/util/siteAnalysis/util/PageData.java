package com.netsol.adagent.util.siteAnalysis.util;

import java.util.List;

public class PageData {
    private String url;
    private String title;    //content of title tag
    private String description;   //Value of meta description tag 
    private List<String> keywords;  //Value of meta keywords tag, split/trimmed
    private String docType;    //entire doctype tag
    private String bodyContent;  //content of body tag
    private List<String> headerContent; //content of h1..h6 tags    
    private List<String> anchorContent; //content of a[href] tags
    
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    public String getDocType() {
        return docType;
    }
    public void setDocType(String docType) {
        this.docType = docType;
    }
    public String getBodyContent() {
        return bodyContent;
    }
    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }
    public List<String> getHeaderContent() {
        return headerContent;
    }
    public void setHeaderContent(List<String> headerContent) {
        this.headerContent = headerContent;
    }
    public List<String> getAnchorContent() {
        return anchorContent;
    }
    public void setAnchorContent(List<String> anchorContent) {
        this.anchorContent = anchorContent;
    }
    
    
}
