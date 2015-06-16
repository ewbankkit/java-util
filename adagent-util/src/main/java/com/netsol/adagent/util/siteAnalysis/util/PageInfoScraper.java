package com.netsol.adagent.util.siteAnalysis.util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class PageInfoScraper {
    private Document doc;
    
    public PageInfoScraper(String url) throws Exception{
        HttpConnection conn = null;
        try{
            conn = (HttpConnection)Jsoup.connect(url);
            conn.request().ignoreHttpErrors(true);
            doc = conn.get();
        }catch(UnknownHostException e){
            throw new Exception("The URL "+url+" could not be resolved.  Unknown Host "+e.getMessage());
        }
        catch(Exception e){
            String msg = "Error:  "+e.getMessage();            
            throw new Exception("The URL "+url+" could not be resolved.  "+msg);
        }
        if(conn.response().statusCode() != 200){
            throw new Exception("The URL "+url+" could not be resolved.  Http Response: "+conn.response().statusCode());
        }
        
    } 
    
    public PageData getPageData(){
        PageData pd = new PageData();
        pd.setUrl(doc.baseUri());
        
        
        pd.setTitle(doc.select("title").html());
        Elements meta = doc.select("meta[name=descriptionxxx]");        
        pd.setDescription(StringUtils.trimToNull(meta.attr("content")));
        String keywords = doc.select("meta[name=keywords]").attr("content");
        
        List<String> kwList = new ArrayList<String>();        
        for(String kw: keywords.split(",")){
            kw = StringUtils.trimToNull(kw);
            if(kw != null){
                kwList.add(kw);
            }
        }
        pd.setKeywords(kwList);
        
        Elements headers = doc.select("h1,h2,h3,h4,h5,h6");
        List<String> headerContent = new ArrayList<String>();        
        for (Element element : headers){ 
            String txt = this.getText(element.childNodes()).trim();
            if(txt.length()>0){
                headerContent.add(this.getText(element.childNodes()));
            }
        }
        pd.setHeaderContent(headerContent);
        
        
        Elements anchors = doc.select("a[href]");
        List<String> anchorContent = new ArrayList<String>();        
        for (Element element : anchors){ 
            String txt = this.getText(element.childNodes()).trim();
            if(txt.length()>0){
                anchorContent.add(this.getText(element.childNodes()));
            }
        }
        pd.setAnchorContent(anchorContent);
        
        Elements body = doc.select("body");
        pd.setBodyContent(getText(body));
        
        
        //Scan top-level nodes for the doctype node
        for (Node node : doc.childNodes()) {
           if (node instanceof DocumentType) {
               DocumentType documentType = (DocumentType)node;
                 pd.setDocType(documentType.toString());
                 break;
           }
        }
        
        return pd;
    }
    
    /*
     * Gets text content of a list of nodes, with all html tags removed
     */
    private String getText(List<? extends Node> nodes){        
        StringBuffer text = new StringBuffer();
        this.getText(nodes, text);
        return text.toString().trim();
    } 
    private void getText(List<? extends Node> nodes, StringBuffer text){
        for(Node n: nodes){
            if(n instanceof TextNode){                
                text.append(" ").append(((TextNode) n).text());
            }
            else{
                getText(n.childNodes(), text);
            }
        }
    }
   
    public final static void main(String[] args) throws Exception {
        try {
            String url = "http://www.networksolutions.com";
            PageData pd = new PageInfoScraper(url).getPageData();
            
            
            System.out.println("Title:  "+pd.getTitle());
            System.out.println("description:  "+pd.getDescription());
            System.out.println("Keywords:  "+Arrays.toString(pd.getKeywords().toArray()));
            System.out.println("Header Content:  "+Arrays.toString(pd.getHeaderContent().toArray()));
            System.out.println("Anchor Content:  "+Arrays.toString(pd.getAnchorContent().toArray()));            
            System.out.println("All Body Content:  "+pd.getBodyContent());

        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Document getDoc() {
        return doc;
    }
    
    
}
