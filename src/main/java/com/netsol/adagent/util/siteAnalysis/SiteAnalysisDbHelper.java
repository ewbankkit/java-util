package com.netsol.adagent.util.siteAnalysis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.siteAnalysis.beans.Competitor;
import com.netsol.adagent.util.siteAnalysis.beans.ContentSection;
import com.netsol.adagent.util.siteAnalysis.beans.Keyword;
import com.netsol.adagent.util.siteAnalysis.beans.KeywordPpcData;
import com.netsol.adagent.util.siteAnalysis.beans.Site;

public class SiteAnalysisDbHelper {
    
    private static final Log log = LogFactory.getLog(SiteAnalysisDbHelper.class);
    /**
     * Insert or Update a site analysis object to the db.
     */
    public static void persistSiteAnalysis(Connection gdbConn, Site site, String updatedBy){
        insertUpdateSite(gdbConn, site, updatedBy);
        
        if(site.getCompetitors() != null){
            for(Competitor competitor:site.getCompetitors()){
                competitor.setSiteId(site.getSiteId());
            }
            insertUpdateCompetitors(gdbConn, site.getCompetitors(), updatedBy);
        }
        
        if(site.getContentSections() != null){
            for(ContentSection content:site.getContentSections()){
                content.setSiteId(site.getSiteId());
            }
            insertUpdateContentSections(gdbConn, site.getContentSections(), updatedBy);
        }
        
        if(site.getKeywords() != null){
            for(Keyword siteKeyword:site.getKeywords()){
                siteKeyword.setSiteId(site.getSiteId());
            }
            persistKeywords(gdbConn, site.getKeywords(), updatedBy);
        }
    }
    
    public static void persistKeywords(Connection gdbConn, List<Keyword> kws, String updatedBy){
        insertUpdateKeywords(gdbConn, kws, updatedBy);
        for(Keyword kw: kws){
            if(kw.getKeywordPpcData()!=null){
                updateKeywordPPCData(gdbConn, kw, updatedBy);
            }
        }
    }

    /**
     * Queries the full site analysis object from the db, including keywords, competitors, and content sections.
     * @return
     */
    public static Site querySiteAnalysis(Connection gdbConn, String url, String city, String state, String businessName){
        Site site = getSite(gdbConn, url, city, state, businessName);
        populateSiteAnalysis(gdbConn, site);
        return site;
    }
    /**
     * Queries the full site analysis object from the db, including keywords, competitors, and content sections.
     * @return
     */
    public static Site querySiteAnalysis(Connection gdbConn, long siteId){
        Site site = getSite(gdbConn, siteId);
        populateSiteAnalysis(gdbConn, site);
        return site;
    }
    
    private static void populateSiteAnalysis(Connection gdbConn, Site site){
        if(site!= null){
            site.setKeywords(getKeywords(gdbConn, site.getSiteId()));
            getKeywordPPCData(gdbConn, site.getKeywords());
            site.setContentSections(getSiteContentSections(gdbConn, site.getSiteId()));        
            site.setCompetitors(getCompetitors(gdbConn, site.getSiteId()));
        }
    }
    
    
    
    
    
    
    
  
   
    private static final String selectAllFromSite = "SELECT " +
            "  site_id, " +
            "  url,  " +
            "  business_name, " +
            "  city, " +
            "  state " +
            "FROM site_analysis.site ";
    
    public static Site getSite(Connection gdbConn, long siteId){
        SimpleJdbcTemplate tpl = new SimpleJdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        return tpl.queryForObject(
                selectAllFromSite + " WHERE site_id = ?;",
                ParameterizedBeanPropertyRowMapper.newInstance(Site.class), siteId);
    }

    public static Site getSite(Connection gdbConn, String url, String city, String state, String businessName){
        SimpleJdbcTemplate tpl = new SimpleJdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        Site site = null;
        try {
            site = tpl.queryForObject(
                    selectAllFromSite + " WHERE url = ? and city = ? and state = ? and business_name = ?;",
                    ParameterizedBeanPropertyRowMapper.newInstance(Site.class), 
                    url, city, state, businessName);
        } catch (EmptyResultDataAccessException e) { //ignore Exception            
        }
        return site;
    }
  
   
    
    public static void insertUpdateSite(Connection gdbConn, final Site site, final String updatedBy){
        JdbcTemplate tpl = new JdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        tpl.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(
                            "insert into site_analysis.site(" +
                            "  site_id, " +                            
                            "  url, " +
                            "  business_name, " +
                            "  city, " +
                            "  state, " +    
                            "  created_date,  " +
                            "  updated_by_user,  " +   
                            "  updated_date  " +
                            ") values (?,?,?,?,?,now(),?,now()) " +
                            "ON DUPLICATE KEY UPDATE " +
                            " updated_by_user= values(updated_by_user), " +   
                            " updated_date = values(updated_date)" ,
                        Statement.RETURN_GENERATED_KEYS);
                    int i=1;
                    ps.setLong(i++, site.getSiteId());
                    ps.setString(i++, site.getUrl());
                    ps.setString(i++, site.getBusinessName());
                    ps.setString(i++, site.getCity());
                    ps.setString(i++, site.getState());
                    ps.setString(i++, updatedBy);
                    return ps;
                }
            },
            keyHolder);
            if(site.getSiteId() == 0){
                site.setSiteId(keyHolder.getKey().intValue());
            }
    }
    
    
    
    public static List<Competitor> getCompetitors(Connection gdbConn, long siteId){
        SimpleJdbcTemplate tpl = new SimpleJdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        return tpl.query(
                " SELECT " +
                "  competitor_id, " +
                "  site_id, " +                            
                "  url, " +
                "  score " +
                " FROM site_analysis.competitor" +
                " WHERE site_id = ?",
                ParameterizedBeanPropertyRowMapper.newInstance(Competitor.class), siteId);
      
    }
    
    public static void insertUpdateCompetitors(Connection gdbConn, final List<Competitor>competitors, final String updatedBy){
        JdbcTemplate tpl = new JdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        for(final Competitor competitor: competitors){
            tpl.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(
                                "insert into site_analysis.competitor(" +
                                "  competitor_id, " +
                                "  site_id, " +                            
                                "  url, " +
                                "  score, " +
                                "  created_date,  " +
                                "  updated_by_user,  " +   
                                "  updated_date  " +
                                ") values (?,?,?,?,now(),?,now()) " +
                                "ON DUPLICATE KEY UPDATE " +
                                " score= values(score), " +
                                " url= values(url), " +
                                " updated_by_user= values(updated_by_user), " +   
                                " updated_date = values(updated_date)" ,
                            Statement.RETURN_GENERATED_KEYS);
                        int i=1;
                        ps.setLong(i++, competitor.getCompetitorId());
                        ps.setLong(i++, competitor.getSiteId());
                        ps.setString(i++, competitor.getUrl());
                        ps.setLong(i++, competitor.getScore());
                        ps.setString(i++, updatedBy);
                        return ps;
                    }
                },
                keyHolder);
                if(competitor.getCompetitorId() == 0){
                    competitor.setCompetitorId(keyHolder.getKey().intValue());
                }
        }
    }
    
    
    
    public static List<Keyword> getKeywords(Connection gdbConn, long siteId){
        SimpleJdbcTemplate tpl = new SimpleJdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        return tpl.query(
                " SELECT " +
                "   kw.keyword_id,  " +
                "   skx.site_id,  " +
                "   kw.keyword,  " +
                "   kw.base_keyword,  " +
                "   kw.search_volume, " +
                "   skx.competitor_id, " +
                "   skx.type,  " +
                "   skx.waf_job_id,  " +
                "   skx.rank  " +
                " FROM site_analysis.keyword kw" +
                "  left join site_analysis.site_keyword_xref skx on skx.keyword_id = kw.keyword_id" +
                " WHERE skx.site_id = ?",
                ParameterizedBeanPropertyRowMapper.newInstance(Keyword.class), siteId);
    }
    
    /**
     * Get list of keyword to process in stats batch job.
     * @param gdbConn
     * @return keywords with null search volume
     */
    public static List<Keyword> getKeywordsWithNullStats(Connection gdbConn){
        SimpleJdbcTemplate tpl = new SimpleJdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        return tpl.query(
                " SELECT " +
                "   kw.keyword_id,  " +
                "   kw.keyword,  " +
                "   kw.base_keyword,  " +
                "   kw.search_volume " +
                " FROM site_analysis.keyword kw" +
                " WHERE search_volume is null",
                ParameterizedBeanPropertyRowMapper.newInstance(Keyword.class));
    }
    
    /**
     * Query a list of keywords by the keyword text.
     * @param gdbConn
     * @param keywords - List of keyword objects with the keyword field populated.
     * If Keyword exists in the database, it will be replaced with the keyword from the DB.
     */
    public static void  getKeywordsByText(Connection gdbConn, List<Keyword> keywords){
        SimpleJdbcTemplate tpl = new SimpleJdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        for (ListIterator<Keyword> iter = keywords.listIterator(); iter.hasNext();) {
            Keyword kw = iter.next();
            try{
                Keyword existing = tpl.queryForObject(
                        " SELECT " +
                        "   kw.keyword_id,  " +
                        "   kw.keyword,  " +
                        "   kw.base_keyword,  " +
                        "   kw.search_volume " +
                        " FROM site_analysis.keyword kw" +
                        " WHERE kw.keyword = ?",
                        ParameterizedBeanPropertyRowMapper.newInstance(Keyword.class), kw.getKeyword());
                
                //If keyword was found, copy fields from the site_keyword_xref table into the bean, and replace it.
                existing.setWafJobId(kw.getWafJobId());
                existing.setRank(kw.getRank());
                existing.setType(kw.getType());
                existing.setCompetitorId(kw.getCompetitorId());
                iter.set(existing); 
            }catch(EmptyResultDataAccessException e){
                //keyword not found. Ignore Exception
            }
        }
    }
    
    
    
    
   /* public static List<Keyword> getKeywordsWithNullSearchVolume(Connection gdbConn){
        SimpleJdbcTemplate tpl = new SimpleJdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        return tpl.query("SELECT " +
                "   keyword_id,  " +
                "   site_id,  " +
                "   keyword,  " +
                "   base_keyword,  " +
                "   type,  " +
                "   waf_job_id,  " +
                "   rank,  " +
                "   avg_cpc,  " +            
                "   search_volume,  " +
                "   created_date,  " +
                "   updated_by_user,  " +
                "   updated_date  " +
                " FROM site_analysis.site_keyword " + 
                " WHERE search_volume is null order by site_id asc;",
                ParameterizedBeanPropertyRowMapper.newInstance(SiteKeyword.class));
      
    }*/
    
    
    public static void insertUpdateKeywords(Connection gdbConn, final List<Keyword> kws, final String updatedBy){
        JdbcTemplate tpl = new JdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        for(final Keyword kw: kws){
            tpl.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(
                                "insert into site_analysis.keyword(" +
                                "   keyword_id,  " +
                                "   keyword,  " +
                                "   base_keyword,  " +
                                "   search_volume,  " +
                                "   created_date,  " +
                                "   updated_by_user,  " +
                                "   updated_date  " +
                                ") values (?,?,?,?,now(),?,now()) " +
                                "ON DUPLICATE KEY UPDATE " +
                                " search_volume = values(search_volume), " +
                                " updated_by_user = values(updated_by_user), " +   
                                " updated_date = values(updated_date)" ,
                            Statement.RETURN_GENERATED_KEYS);
                        int i=1;
                        ps.setObject(i++, kw.getKeywordId());
                        ps.setString(i++, kw.getKeyword());
                        ps.setString(i++, kw.getBaseKeyword());
                        ps.setObject(i++, kw.getSearchVolume());
                        ps.setString(i++, updatedBy);
                        log.info("Keyword PS: "+ps.toString());
                        return ps;
                    }
                },
                keyHolder);
            if(kw.getKeywordId() == 0){
                kw.setKeywordId(keyHolder.getKey().intValue());
            }
            
            tpl.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(
                            "insert into site_analysis.site_keyword_xref(" +
                            "   site_id,  " +
                            "   keyword_id,  " +
                            "   competitor_id,  " +
                            "   type,  " +                                
                            "   waf_job_id,  " +
                            "   rank,  " +
                            "   created_date,  " +
                            "   updated_by_user,  " +
                            "   updated_date  " +
                            ") values (?,?,?,?,?,?, now(),?,now()) " +
                            "ON DUPLICATE KEY UPDATE " +                                
                            " waf_job_id = values(waf_job_id), " +
                            " rank = values(rank), " +
                            " updated_by_user= values(updated_by_user), " +   
                            " updated_date = values(updated_date)");
                    int i=1;
                    ps.setLong(i++, kw.getSiteId());
                    ps.setLong(i++, kw.getKeywordId());
                    ps.setObject(i++, kw.getCompetitorId());
                    ps.setString(i++, kw.getType());                        
                    ps.setObject(i++, kw.getWafJobId());
                    ps.setObject(i++, kw.getRank());                    
                    ps.setString(i++, updatedBy);
                    return ps;
                }
            });
        }     
    }
    
    
   

    /**
     * Populate keywordPPCData list for each of the given keywords
     * @param gdbConn
     * @param keywords
     * @throws Exception
     */
    public static void getKeywordPPCData(Connection gdbConn, List<Keyword> keywords){ 
        final ParameterizedBeanPropertyRowMapper<KeywordPpcData> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(KeywordPpcData.class);
        PreparedStatement ps = null;
        try{
            ps = gdbConn.prepareStatement(
                    " SELECT " +
                    "   keyword_ppc_data_id,  " +
                    "   keyword_id,  " +
                    "   bid,  " +
                    "   cpc,  " +                                
                    "   target_position,  " +
                    "   actual_position  " +
                    " FROM site_analysis.keyword_ppc_data" +                
                    " WHERE keyword_id = ?");
            
            for(Keyword kw: keywords){
                if(kw.getKeywordId() > 0){ //skip keywords that havent been created yet
                    ResultSet rs =null;
                    try{
                        ps.clearParameters();
                        ps.setLong(1, kw.getKeywordId());                    
                        rs = ps.executeQuery();
                        int r = 0;
                        List<KeywordPpcData> ppcData = new ArrayList<KeywordPpcData>();
                        while(rs.next()){
                            ppcData.add(rowMapper.mapRow(rs, ++r));    
                        }
                        kw.setKeywordPpcData(ppcData);
                        
                    }finally{
                        BaseHelper.close(rs);
                    }
                }
            }
        }
        catch(Exception e){
            throw new RuntimeException(e); //throw runtime exception to keep things consistent with the spring JDBC Api
        }
        finally{
            BaseHelper.close(ps);
        }
    }
    
    
    public static void updateKeywordPPCData(Connection gdbConn, final Keyword kw, final String updatedBy){
        JdbcTemplate tpl = new JdbcTemplate(new SingleConnectionDataSource(gdbConn, true));    
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        tpl.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "delete from site_analysis.keyword_ppc_data" +
                        " where keyword_id = ?;");
                ps.setLong(1, kw.getKeywordId());
                return ps;
            }
        });
        
        List<KeywordPpcData> kwPPCDataList = kw.getKeywordPpcData();
        for(final KeywordPpcData kwPPCData: kwPPCDataList){
            kwPPCData.setKeywordId(kw.getKeywordId());
            tpl.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(
                            "insert into site_analysis.keyword_ppc_data(" +
                            "   keyword_id,  " +
                            "   bid,  " +
                            "   cpc,  " +                                
                            "   target_position,  " +
                            "   actual_position,  " +
                            "   created_date,  " +
                            "   updated_by_user,  " +
                            "   updated_date  " +
                            ") values (?,?,?,?,?,now(),?,now()); ",
                        Statement.RETURN_GENERATED_KEYS);
                    int i=1;
                    ps.setLong(i++, kw.getKeywordId());
                    ps.setDouble(i++, kwPPCData.getBid());
                    ps.setDouble(i++, kwPPCData.getCpc());
                    ps.setDouble(i++, kwPPCData.getTargetPosition());
                    ps.setDouble(i++, kwPPCData.getActualPosition());
                    ps.setString(i++, updatedBy);
                    return ps;
                }
            },keyHolder);
            kwPPCData.setKeywordPpcDataId(keyHolder.getKey().intValue());
        }
    }
    
    private static final String selectAllFromContentSection ="SELECT " +
            "  content_section_id, " +
            "  site_id,   " +
            "  content,  " +
            "  type,  " +
            "  created_date,  " +
            "  updated_by_user,  " +
            "  updated_date  " +
            "FROM site_analysis.content_section ";

    public static List<ContentSection> getSiteContentSections(Connection gdbConn, long siteId){
        SimpleJdbcTemplate tpl = new SimpleJdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        return tpl.query(selectAllFromContentSection + " WHERE site_id = ?;",
                ParameterizedBeanPropertyRowMapper.newInstance(ContentSection.class), siteId);
      
    }
    
    public static void insertUpdateContentSections(Connection gdbConn, final List<ContentSection> sections, final String updatedBy){
        JdbcTemplate tpl = new JdbcTemplate(new SingleConnectionDataSource(gdbConn, true));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        for(final ContentSection section: sections){ 
       
            tpl.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement("insert ignore into site_analysis.content_section(" +
                                "  content_section_id, " +
                                "  site_id,   " +
                                "  content,  " +
                                "  type,  " +
                                "  created_date,  " +
                                "  updated_by_user,  " +
                                "  updated_date  " +
                                ") values (?,?,?,?,now(),?,now()) ",
                                Statement.RETURN_GENERATED_KEYS);
                        int i=1;
                        ps.setLong(i++, section.getContentSectionId());
                        ps.setLong(i++, section.getSiteId());
                        ps.setString(i++, section.getContent());
                        ps.setString(i++, section.getType());                       
                        ps.setString(i++, updatedBy);
                        return ps;
                    }
                },keyHolder);
            if(section.getContentSectionId() == 0){
                section.setContentSectionId(keyHolder.getKey().intValue());
            }
                
        }
            
    }

}
