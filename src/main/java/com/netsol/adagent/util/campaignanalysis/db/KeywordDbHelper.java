package com.netsol.adagent.util.campaignanalysis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.campaignanalysis.db.beans.Ad;



/**
 * DB Wrapper for querying ads that contain a keyword, from PDB
 *
 * Stores a cache of previously queried keywords for performance
 * 
 */
public class KeywordDbHelper {
	static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:19 KeywordDbHelper.java NSI";
	private static Log log = LogFactory.getLog(KeywordDbHelper.class);
	
	//temporary storage to save query results, 
	//this will only be used for the life of this KeyworDbHelper instance, or a single request
	private Map<String, List<Ad>> cache = new HashMap<String, List<Ad>>();
	
	
	
	final static String sql = 
		"select headline, desc_1, desc_2 from ns_ad " +
		"where ( " +
		"	 headline like ? or desc_1 like ? or desc_2 like ? ) " +
		"and status = 'ACTIVE' " +
		"and ad_type = 'text' " +
		"and vendor_id = 1 " +
		"group by headline, desc_1, desc_2 " +
		"limit ?";
	
	
	//matches on the variable syntax used in ppc ads, such as: {Keyword: XXX}	
	private static Pattern adVariables = Pattern.compile("\\{[\\s\\w]*:\\s*([\\s\\w]*)\\}");
	

	
	
	/**
	 * 
	 * @param searchPhrase
	 * @param maxNumAds - max number of ads to return - this is ignored if the result was cached
	 * @return
	 */
	public List<Ad> queryPDBKeywords(Connection pdb, String searchPhrase, int maxNumAds) throws SQLException{
		List<Ad> result = null;
		if(cache.containsKey(searchPhrase)){
			result = cache.get(searchPhrase);
			log.info("return cached result: "+ searchPhrase +", n ads: "+ result.size());
		}
		else{
			result = executeKeywordQuery(pdb, searchPhrase, maxNumAds);
			cache.put(searchPhrase, result);
			log.info("return queried result: "+ searchPhrase +", n ads: "+ result.size());
		}
		return result;		
	}
	
	private List<Ad> executeKeywordQuery(Connection pdb, String searchPhrase, int maxNumAds) throws SQLException{
		List<Ad> ads = new ArrayList<Ad>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		log.info("Query PDB for ads for keyword: "+ searchPhrase);
		try{
			stat = pdb.prepareStatement(sql);
			
			searchPhrase = searchPhrase.replaceAll("%", "");
			stat.setString(1, "%" + searchPhrase + "%");
			stat.setString(2, "%" + searchPhrase + "%");
			stat.setString(3, "%" + searchPhrase + "%");
			stat.setInt(4, maxNumAds);
			
			
			rs = stat.executeQuery();
			
			while(rs.next()){
				
				
				
				
				String headline = rs.getString("headline");
				headline = adVariables.matcher(
						headline == null ? "" : headline.trim() ).replaceAll("$1");
				
				String desc1 = rs.getString("desc_1");
				desc1 = adVariables.matcher(
						desc1 == null ? "" : desc1.trim() ).replaceAll("$1");
				
				String desc2 = rs.getString("desc_2");
				desc2 = adVariables.matcher(
						desc2 == null ? "" : desc2.trim() ).replaceAll("$1");
				
				
				
				
				Ad ad = new Ad();
				ad.setHeadline(headline);
				ad.setDescription(desc1 + "\n" + desc2);
				
			
				ads.add(ad);
			}
			
			
		}		
		finally{
			close(rs);
			close(stat);
		}
			
		log.info("\tNum Ads: "+ ads.size());
		return ads;
	}
	
	
	
	
	private static void close(Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (Exception e) {
				log.error( "Cannot close stmt: " + e.toString());
			}
		}
	}
	private static void close(ResultSet rs) {
		if (rs != null){
			try {
				rs.close();
			} catch (Exception e) {
				log.error( "Cannot close RS: " + e.toString());
			}
		}
	}
	


}
