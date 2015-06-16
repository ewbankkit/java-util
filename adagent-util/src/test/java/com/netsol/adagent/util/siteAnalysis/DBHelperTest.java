package com.netsol.adagent.util.siteAnalysis;



import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.netsol.adagent.util.LocalHost;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.siteAnalysis.SiteAnalysisDbHelper;
import com.netsol.adagent.util.siteAnalysis.beans.Competitor;
import com.netsol.adagent.util.siteAnalysis.beans.ContentSection;
import com.netsol.adagent.util.siteAnalysis.beans.Keyword;
import com.netsol.adagent.util.siteAnalysis.beans.KeywordPpcData;
import com.netsol.adagent.util.siteAnalysis.beans.Site;

public class DBHelperTest {
    Connection gdbConn = null;
    @Before
    public void makeGdbConn() throws Exception{
        gdbConn = BaseHelper.createDevGdbConnection();
        gdbConn.setAutoCommit(false);
    }
    @After    
    public void closeGdbConn(){
        BaseHelper.close(gdbConn);        
    }
    
    @Test
    public void testLead() throws Exception{  
        try{
            System.out.println("Creating new site object");
        Site site = new Site();
        site.setBusinessName("Pizza Hut - "+System.currentTimeMillis());
        site.setCity("Herndon");
        site.setState("VA");
        site.setUrl("http://www.pizzahut.com");
        
        
        site.setCompetitors(new ArrayList<Competitor>());
        for(int i=0; i<3; i++){
            Competitor c = new Competitor();
            c.setUrl("http://www.pizza-1"+i+".com");
            c.setScore(10);
            site.getCompetitors().add(c);
        }
        
        
        
        site.setContentSections(new ArrayList<ContentSection>());
        for(int i=0; i<ContentSection.Type.values().length; i++){
            ContentSection cs = new ContentSection();
            cs.setType(ContentSection.Type.values()[i].toString());
            cs.setContent("Content: "+cs.getType());
            site.getContentSections().add(cs);
        }
        
        List<Keyword> keywords = new ArrayList<Keyword>();
        for(int i=0; i<10; i++){
            Keyword kw = new Keyword();
            kw.setBaseKeyword("KW "+i);
            keywords.add(kw);
        }
        site.setKeywords(keywords);
        site.fixupKeywordLongtails();
        System.out.println("Getting dupe kws from db");
        SiteAnalysisDbHelper.getKeywordsByText(gdbConn, keywords);
        
        for(Keyword kw: keywords){
            kw.setType("META");
            kw.setSearchVolume(50000L);
            kw.setWafJobId(11L);
            kw.setRank(2);
            kw.setKeywordPpcData(new ArrayList<KeywordPpcData>());
            int ranks[] = {2,4,10};
            for(int j=0; j<ranks.length; j++){
                KeywordPpcData kwData = new KeywordPpcData();
                kwData.setBid(j*3+1);
                kwData.setCpc(100*(3-j));
                kwData.setTargetPosition(ranks[j]);
                kwData.setActualPosition(ranks[j]+1);
                kw.getKeywordPpcData().add(kwData);
            }
            
           
        }
        System.out.println("Saving to db");
        SiteAnalysisDbHelper.persistSiteAnalysis(gdbConn, site, "DBHelperTest - "+LocalHost.NAME);
        gdbConn.commit();
        Assert.assertTrue("Site not added to db",site.getSiteId() > 0);
        
        System.out.println("Query same object back from db");
        Site dbSite = SiteAnalysisDbHelper.querySiteAnalysis(gdbConn, site.getUrl(), site.getCity(), site.getState(), site.getBusinessName());
        Assert.assertTrue("Site not found in db", dbSite != null && dbSite.getSiteId() > 0);
        System.out.println("Check Equality");
        Assert.assertTrue("Equal check failed", site.equals(dbSite));
        
           
        
        
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

}
