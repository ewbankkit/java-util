package com.netsol.adagent.util.campaignanalysis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import registrar.base.log.Log;

import com.netsol.adagent.util.beans.BaseData;
import com.netsol.adagent.util.campaignanalysis.db.beans.Ad;
import com.netsol.adagent.util.campaignanalysis.db.beans.AdGroup;
import com.netsol.adagent.util.campaignanalysis.db.beans.Campaign;
import com.netsol.adagent.util.campaignanalysis.db.beans.CampaignAnalysis;
import com.netsol.adagent.util.campaignanalysis.db.beans.CampaignGroup;
import com.netsol.adagent.util.campaignanalysis.db.beans.Keyword;
import com.netsol.adagent.util.campaignanalysis.db.beans.LocationTarget;
import com.netsol.adagent.util.campaignanalysis.db.beans.ProximityTarget;
import com.netsol.adagent.util.campaignanalysis.db.beans.SeedKeyword;
import com.netsol.adagent.util.campaignanalysis.type.CampaignType;






/*
 *  -QUERY
 *  	Get all data, including status and campaign structure
 *  
 *  -INITIATE
 *  	- insert 'INITIAL' status
 *  	- insert/update campaign structure, 'IN_PROCESS'
 *  	- insert/update campaign structure, 'COMPLETE'
 * 
 * 
 */
public class CampaignAnalysisDB {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:18 CampaignAnalysisDB.java NSI";
//	private static Log log = LogFactory.getLog(CampaignAnalysisDB.class);
	private static final String logCmp = "adagent";

	
	/** 
	 * Returns db record for the Campaign Analysis, and all other campaign/adgroup data
	 * @param pdb
	 * @param prodInstId
	 */
	public static CampaignAnalysis queryProduct(Connection pdb, String prodInstId)
	throws SQLException
	{
		CampaignAnalysis analysis = null;

		analysis = queryCampaignAnalysis(pdb, prodInstId);
		
		if(analysis != null){
			long analysisId = analysis.getAnalysisId();

			List<LocationTarget> locationTargets = queryLocationTargets(pdb, prodInstId);
			List<ProximityTarget> proximityTargets = queryProximityTargets(pdb, prodInstId);
			analysis.setLocationTargets(locationTargets);
			analysis.setProximityTargets(proximityTargets);
			
			List<SeedKeyword> seedKeywords = querySeedKeywords(pdb, prodInstId);
			analysis.setSeedKeywords(seedKeywords);
			
			List<CampaignGroup> groups = queryCampaignGroups(pdb, analysisId, prodInstId);
			analysis.setCampaignGroups(groups);

			List<Campaign> campaigns = queryCampaigns(pdb, prodInstId);
			List<AdGroup> adgroups = queryAdgroups(pdb, prodInstId);
			List<Ad> ads = queryAds(pdb, prodInstId);
			List<Keyword> keywords = queryKeywords(pdb, prodInstId);
			 

			for(CampaignGroup group: groups){
				for(Campaign camp: campaigns){
					if(camp.getCampaignGroupId().longValue() == group.getCampaignGroupId().longValue()){
						group.getCampaigns().add(camp);

						for(AdGroup adgroup: adgroups){
							if(camp.getCampaignId().longValue() == adgroup.getCampaignId().longValue()){
								camp.getAdGroups().add(adgroup);

								for(Keyword kw: keywords){
									if(kw.getAdGroupId().longValue() == adgroup.getAdGroupId() ){
										adgroup.getKeywords().add(kw);
									}
								}

								for(Ad ad: ads){
									if(ad.getAdGroupId().longValue() == adgroup.getAdGroupId() ){
										adgroup.getAds().add(ad);
									}
								}

							}
						}
					}
				}
			}				
		}


		return analysis;
	}
	
	
	public static void updateLocations(Connection pdb, 
			String prodInstId, 
			List<LocationTarget> locationTargets, 
			List<ProximityTarget> proximityTargets)
	throws SQLException
	{
		final String[] deleteLocations = {
				"delete from ca_proximity_targets where prod_inst_id = ?",
				"delete from ca_location_targets where prod_inst_id = ?"
		};
		
		PreparedStatement stat = null;
		for(String deleteSql: deleteLocations){
			try{
				stat = pdb.prepareStatement(deleteSql);
				log(prodInstId, "Delete locations from table:\n "+deleteSql);
				setString(stat, 1, prodInstId);
				stat.execute();
			}
			finally{
				close(stat);
			}
		}
		
		insertLocationTargets(pdb, locationTargets, prodInstId);
		insertProximityTargets(pdb, proximityTargets, prodInstId);
		
	}
	
	public static CampaignAnalysis queryCampaignAnalysis(Connection pdb, String prodInstId)
	throws SQLException
	{

		final String queryAnalysis =
			"select " +
			"  ca_analysis_id, " +
			"  analysis_status, " +
			"  is_local, " +
			"  sells_online, " +
			"  avg_spend,  " +
			"  completion_date " +
			"from ca_analysis " +
			"where prod_inst_id = ? ";

		CampaignAnalysis analysis = null;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			stat = pdb.prepareStatement(queryAnalysis);
			setString(stat, 1, prodInstId);
			rs = stat.executeQuery();
			if(rs.next()){
				analysis = new CampaignAnalysis();
				analysis.setProdInstId(prodInstId);
				analysis.setAnalysisId(rs.getLong("ca_analysis_id"));
				analysis.setLocal(rs.getBoolean("is_local"));
				analysis.setSellsOnline(rs.getBoolean("sells_online"));
				analysis.setAvgTicket(rs.getDouble("avg_spend"));
				String status = rs.getString("analysis_status");
				analysis.setStatus(CampaignAnalysis.Status.valueOf(status));
				analysis.setCompletionDate(rs.getTimestamp("completion_date"));
			}
			
		}
		finally{
			close(rs);
			close(stat);
		}
		return analysis;
	}

	public static List<CampaignGroup> queryCampaignGroups(Connection pdb, long analysisId, String prodInstId)
	throws SQLException
	{	
		final String queryCampaignGroup = 
			"select " +
			"  ca_campaign_group_id, " +
			"  name " +
			"from ca_campaign_group " +
			"where ca_analysis_id = ?";
		
		List<CampaignGroup> groups = new ArrayList<CampaignGroup>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			stat = pdb.prepareStatement(queryCampaignGroup);
			setLong(stat, 1, analysisId);
			rs = stat.executeQuery();
			while(rs.next()){
				CampaignGroup g = new CampaignGroup();
				
				g.setAnalysisId(analysisId);
				g.setCampaignGroupId(rs.getLong("ca_campaign_group_id"));
				g.setName(rs.getString("name"));
				g.setCampaigns(new ArrayList<Campaign>());
				groups.add(g);
				
			}
			
		}
		finally{
			close(rs);
			close(stat);
		}
		return groups;
	}
	
	
	public static List<Campaign> queryCampaigns(Connection pdb, String prodInstId)
	throws SQLException
	{		
		
		final String queryCampaign =
			"select " + 
			"  ca_campaign_id, " +
			"  ca_campaign_group_id, " +
			"  name, " +
			"  type " +
			"from ca_campaign " +
			"where prod_inst_id = ?";
		
		List<Campaign> campaigns = new ArrayList<Campaign>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			stat = pdb.prepareStatement(queryCampaign);
			setString(stat, 1, prodInstId);
			rs = stat.executeQuery();
			while(rs.next()){
				Campaign c = new Campaign();
				
				c.setCampaignId(rs.getLong("ca_campaign_id"));
				c.setCampaignGroupId(rs.getLong("ca_campaign_group_id"));
				c.setName(rs.getString("name"));
				
			
				String type = rs.getString("type");
				c.setType(CampaignType.valueOf(type));
				c.setAdGroups(new ArrayList<AdGroup>());
				
				campaigns.add(c);
				
			}
			
		}
		finally{
			close(rs);
			close(stat);
		}
		return campaigns;
	}
	
	
	public static List<AdGroup> queryAdgroups(Connection pdb, String prodInstId)
	throws SQLException
	{		
		
		final String queryAdgroup = 
			"select " +
			"  ca_adgroup_id, " +
			"  ca_campaign_id, " +
			"  name " +
			"from ca_adgroup " +
			"where prod_inst_id = ?";
		
		List<AdGroup> adgroups = new ArrayList<AdGroup>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			stat = pdb.prepareStatement(queryAdgroup);
			setString(stat, 1, prodInstId);
			rs = stat.executeQuery();
			while(rs.next()){
				AdGroup ag = new AdGroup();
				
				ag.setAdGroupId(rs.getLong("ca_adgroup_id"));
				ag.setCampaignId(rs.getLong("ca_campaign_id"));
				ag.setName(rs.getString("name"));
				ag.setKeywords(new ArrayList<Keyword>());
				ag.setAds(new ArrayList<Ad>());
				
				adgroups.add(ag);
			}
			
		}
		finally{
			close(rs);
			close(stat);
		}
		return adgroups;
	}
	


	public static List<Ad> queryAds(Connection pdb, String prodInstId)
	throws SQLException
	{	
		final String queryAd =
			"select " +
			"  ca_ad_id, " +
			"  ca_adgroup_id, " +
			"  headline, " +
			"  description, " +
			"  display_url, " +
			"  destination_url " +
			"from ca_ad  " +
			"where  prod_inst_id = ?";
		
		List<Ad> ads = new ArrayList<Ad>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			stat = pdb.prepareStatement(queryAd);
			setString(stat, 1, prodInstId);
			rs = stat.executeQuery();
			while(rs.next()){
				Ad ad = new Ad();
				
				ad.setAdId(rs.getLong("ca_ad_id"));
				ad.setAdGroupId(rs.getLong("ca_adgroup_id"));
				ad.setHeadline(rs.getString("headline"));
				ad.setDescription(rs.getString("description"));
				ad.setDestinationUrl(rs.getString("destination_url"));
				ad.setDisplayUrl(rs.getString("display_url"));
				
				ads.add(ad);
			}
			
		}
		finally{
			close(rs);
			close(stat);
		}
		return ads;
	}
	
	

	public static List<Keyword> queryKeywords(Connection pdb, String prodInstId)
	throws SQLException
	{		
		final String queryKeyword =
			"select " +
			"  ca_keyword_id, " +
			"  ca_adgroup_id, " +
			"  text, " +
			"  cpc_estimate, " +
			"  daily_clicks_estimate, " +
			"  search_volume_estimate, " +
			"  competition_scale, " +
			"  search_position_estimate, " +
			"  bid " +
			"from ca_keyword " +
			"where prod_inst_id = ?";

		
		List<Keyword> keywords = new ArrayList<Keyword>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			stat = pdb.prepareStatement(queryKeyword);
			setString(stat, 1, prodInstId);
			rs = stat.executeQuery();
			while(rs.next()){
				Keyword kw = new Keyword();
				kw.setKeywordId(rs.getLong("ca_keyword_id"));
				kw.setAdGroupId(rs.getLong("ca_adgroup_id"));
				kw.setText(rs.getString("text"));
				kw.setCpcEstimate(rs.getDouble("cpc_estimate"));
				kw.setDailyClicksEstimate(rs.getDouble("daily_clicks_estimate"));
				kw.setSearchVolumeEstimate(rs.getDouble("search_volume_estimate"));
				kw.setCompetitionScale(rs.getLong("competition_scale"));
				kw.setSearchPositionEstimate(rs.getDouble("search_position_estimate"));
				kw.setBid(rs.getDouble("bid"));				
				keywords.add(kw);	
			}
			
		}
		finally{
			close(rs);
			close(stat);
		}
		return keywords;
	}
	
	public static List<LocationTarget> queryLocationTargets(Connection pdb, String prodInstId)
	throws SQLException
	{		
		final String queryLocationTargets =
			"select " +
			"  ca_analysis_id, "+
			"  city, " +
			"  state, " +
			"  is_primary " +
			"from ca_location_targets " +
			"where prod_inst_id = ?";

		
		List<LocationTarget> locations = new ArrayList<LocationTarget>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			stat = pdb.prepareStatement(queryLocationTargets);
			setString(stat, 1, prodInstId);
			rs = stat.executeQuery();
			while(rs.next()){
				LocationTarget loc = new LocationTarget();
				loc.setAnalysisId(rs.getLong("ca_analysis_id"));
				loc.setCity(rs.getString("city"));
				loc.setState(rs.getString("state"));
				loc.setPrimaryLocation(rs.getBoolean("is_primary"));
				locations.add(loc);	
			}
			
		}
		finally{
			close(rs);
			close(stat);
		}
		return locations;
	}
	
	public static List<SeedKeyword> querySeedKeywords(Connection pdb, String prodInstId)
	throws SQLException
	{		
		final String querySeedKeywords =
			"select " +
			"  ca_analysis_id, "+
			"  seed_keyword " +		
			"from ca_seed_keyword " +
			"where prod_inst_id = ?";

		
		List<SeedKeyword> keywords = new ArrayList<SeedKeyword>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			stat = pdb.prepareStatement(querySeedKeywords);
			setString(stat, 1, prodInstId);
			rs = stat.executeQuery();
			while(rs.next()){
				SeedKeyword word = new SeedKeyword();
				word.setAnalysisId(rs.getLong("ca_analysis_id"));
				word.setSeedKeyword(rs.getString("seed_keyword"));				
				keywords.add(word);	
			}
			
		}
		finally{
			close(rs);
			close(stat);
		}
		return keywords;
	}
	
	
	public static List<ProximityTarget> queryProximityTargets(Connection pdb, String prodInstId)
	throws SQLException
	{		
		final String queryProximityTargets =
			"select " +
			"  ca_analysis_id, "+
			"  zip_code, " +
			"  radius " +
			"from ca_proximity_targets " +
			"where prod_inst_id = ?";

		
		List<ProximityTarget> locations = new ArrayList<ProximityTarget>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			stat = pdb.prepareStatement(queryProximityTargets);
			setString(stat, 1, prodInstId);
			rs = stat.executeQuery();
			while(rs.next()){
				ProximityTarget loc = new ProximityTarget();
				loc.setAnalysisId(rs.getLong("ca_analysis_id"));
				loc.setZipCode(rs.getString("zip_code"));
				loc.setRadius(rs.getLong("radius"));
				
				locations.add(loc);	
			}
			
		}
		finally{
			close(rs);
			close(stat);
		}
		return locations;
	}
	
	
	/**
	 * Inserts/Updates all records for a product's campaign analysis.
	 * This includes keywords, ads, adgroups, campaigns, campaign groups, and the campaign analysis
	 * @param pdb
	 * @param campaignAnalysis
	 */
	public static void updateProduct(Connection pdb, CampaignAnalysis campaignAnalysis)
	throws SQLException
	{
		
		String prodInstId = campaignAnalysis.getProdInstId();
		log(prodInstId, "About to insert campaign analysis for product:  "+prodInstId);
		
		
		if(campaignAnalysis.getAnalysisId() == null){
			insertCampaignAnalysis(pdb,campaignAnalysis);
		}
		else{
			updateCampaignAnalysis(pdb, campaignAnalysis);
		}
		long analysisId = campaignAnalysis.getAnalysisId();
		deleteCampaigns(pdb, prodInstId);		
		
		if(campaignAnalysis.getLocationTargets() != null){
			for(LocationTarget locationTarget: campaignAnalysis.getLocationTargets()){
				locationTarget.setAnalysisId(analysisId);
			}
			insertLocationTargets(pdb, campaignAnalysis.getLocationTargets(), prodInstId);
		}	
		
		if(campaignAnalysis.getProximityTargets() != null){
			for(ProximityTarget proximityTarget: campaignAnalysis.getProximityTargets()){
				proximityTarget.setAnalysisId(analysisId);
			}
			insertProximityTargets(pdb, campaignAnalysis.getProximityTargets(), prodInstId);
		}
		
		 if(campaignAnalysis.getSeedKeywords() != null){
			for(SeedKeyword seedKeyword: campaignAnalysis.getSeedKeywords()){
				seedKeyword.setAnalysisId(analysisId);
			}
			insertSeedKeywords(pdb, campaignAnalysis.getSeedKeywords(), prodInstId);
		}
		
		if(campaignAnalysis.getCampaignGroups() != null){

			for(CampaignGroup group: campaignAnalysis.getCampaignGroups()){
				group.setAnalysisId(analysisId);
			}
			insertCampaignGroups(pdb, campaignAnalysis.getCampaignGroups(), prodInstId);

			List<Campaign> allCampaigns = new ArrayList<Campaign>();
			for(CampaignGroup group: campaignAnalysis.getCampaignGroups()){
				long groupId = group.getCampaignGroupId();
				
				if(group.getCampaigns() != null){
					for(Campaign camp: group.getCampaigns()){

						camp.setCampaignGroupId(groupId);
						allCampaigns.add(camp);
					}
				}
			}
			insertCampaigns(pdb, allCampaigns, prodInstId);

			List<AdGroup> allAdgroups = new ArrayList<AdGroup>();
			for(Campaign camp: allCampaigns){
				long campaignId = camp.getCampaignId();
				
				if(camp.getAdGroups() != null){
					for(AdGroup ag: camp.getAdGroups()){
						ag.setCampaignId(campaignId);

						allAdgroups.add(ag);
					}
				}
			}

			insertAdGroups(pdb, allAdgroups, prodInstId);

			List<Keyword> allKeywords = new ArrayList<Keyword>();
			List<Ad> allAds = new ArrayList<Ad>();
			for(AdGroup adGroup: allAdgroups){
				long adGroupId = adGroup.getAdGroupId();
				if(adGroup.getKeywords() != null){
					for(Keyword kw: adGroup.getKeywords()){
						kw.setAdGroupId(adGroupId);
						allKeywords.add(kw);
					}
				}

				if(adGroup.getAds() != null){
					for(Ad ad: adGroup.getAds()){
						ad.setAdGroupId(adGroupId);
						allAds.add(ad);
					}
				}
			}

			insertAds(pdb, allAds, prodInstId);
			insertKeywords(pdb, allKeywords, prodInstId);
			

		}
			
			
		
		
	}
	
	/**
	 * updates the status of an existing Campaign Analysis
	 */
	public static void updateCampaignAnalysis(Connection pdb, CampaignAnalysis campaignAnalysis)
	throws SQLException
	{
		
		final String updateCampaignAnalysis = 
			"update ca_analysis set "+
			"  analysis_status = ?, " +
			"  is_local = ?, "+
			"  sells_online = ?, "+
			"  avg_spend =?, " +
			"  completion_date = ? " +
			"  where prod_inst_id = ? ";

		PreparedStatement stat = null;
		String prodInstId = campaignAnalysis.getProdInstId();
		try{
			stat = pdb.prepareStatement(updateCampaignAnalysis, new String[]{"ca_analysis_id"});
			log(prodInstId, "About to update campaign analysis for product: "+ campaignAnalysis.getProdInstId()+", status: "+ campaignAnalysis.getStatus());
			int arg = 1;
			
			setString(stat, arg++, campaignAnalysis.getStatus().name());
			stat.setBoolean(arg++, campaignAnalysis.isLocal());
			stat.setBoolean(arg++, campaignAnalysis.isSellsOnline());
			setDouble(stat, arg++, campaignAnalysis.getAvgTicket());
			setDate(stat, arg++, campaignAnalysis.getCompletionDate());
			setString(stat, arg++, campaignAnalysis.getProdInstId());
			stat.execute();

			if(campaignAnalysis.getAnalysisId() == null){
				Long key = getGeneratedKey(stat, "ca_analysis_id");
				campaignAnalysis.setAnalysisId(key);
			}
			
			log(prodInstId, "Analysis updated");
		}
		
		finally{
			close(stat);
		}


	}
	
	
	/**
	 * Inserts a new row to ca_campaign_analysis.
	 * @param pdb
	 * @param campaignAnalysis
	 * @throws SQLException
	 */
	public static void insertCampaignAnalysis(Connection pdb, CampaignAnalysis campaignAnalysis)
	throws SQLException
	{
		final String insertCampaignAnalysis = 
			"insert into ca_analysis ( "+
			"  prod_inst_id, "+
			"  analysis_status, "+
			"  is_local, "+
			"  sells_online, "+
			"  avg_spend, " +
			"  completion_date," +
			"  created_date "+
			") values (?, ?, ?, ?,?, ?, now() ) ";

		PreparedStatement stat = null;
		String prodInstId = campaignAnalysis.getProdInstId();
		try{
			stat = pdb.prepareStatement(insertCampaignAnalysis, new String[]{"ca_analysis_id"});
			log(prodInstId, "About to insert campaign analysis for product: "+ campaignAnalysis.getProdInstId());
			int arg = 1;
			setString(stat, arg++, prodInstId);
			setString(stat, arg++, campaignAnalysis.getStatus().name());
			stat.setBoolean(arg++, campaignAnalysis.isLocal());
			stat.setBoolean(arg++, campaignAnalysis.isSellsOnline());
			setDouble(stat,  arg++, campaignAnalysis.getAvgTicket());
			setDate(stat, arg++, campaignAnalysis.getCompletionDate());			
			stat.execute();

			//if(campaignAnalysis.getAnalysisId() == null){
				Long key = getGeneratedKey(stat, "ca_analysis_id");
				campaignAnalysis.setAnalysisId(key);
			//}
			
			log(prodInstId, "Analysis inserted");
		}
		
		finally{
			close(stat);
		}


	}

	public static void insertCampaignGroups(Connection pdb, List<CampaignGroup> campaignGroups, String prodInstId)
	throws SQLException
	{
		final String insertCampaignGroup = 
			"insert into ca_campaign_group( " +
			"  prod_inst_id, " +
			"  ca_analysis_id, " +
			"  name, " +
			"  created_date ) " +
			"values ( ?, ?, ?, now() )";
			


		PreparedStatement stat = null;

		try{
			stat = pdb.prepareStatement(insertCampaignGroup, new String[]{"ca_campaign_group_id"});
			log(prodInstId, "About to insert "+campaignGroups.size()+" campaign groups");
			for(CampaignGroup campaignGroup: campaignGroups){
				int arg = 1;
				stat.clearParameters();
				setString(stat, arg++, prodInstId);
				setLong(stat, arg++, campaignGroup.getAnalysisId());
				setString(stat, arg++, campaignGroup.getName());
				stat.execute();
	
				//if(campaignGroup.getCampaignGroupId() == null){
				Long key = getGeneratedKey(stat, "ca_campaign_group_id");
				campaignGroup.setCampaignGroupId(key);
				//}
			}
			log(prodInstId, "Finished inserting campaign groups");
		}
		finally{
			close(stat);
		}

	}

	public static void insertCampaigns(Connection pdb, List<Campaign> campaigns, String prodInstId)
	throws SQLException
	{
		String insertCampaign = 
			"insert into ca_campaign( " +
			"  prod_inst_id, " +
			"  ca_campaign_group_id, " +
			"  type, " +
			"  name, " +
			"  created_date " +
			" ) values (?, ?, ?, ?, now())";


		PreparedStatement stat = null;

		try{
			stat = pdb.prepareStatement(insertCampaign, new String[]{"ca_campaign_id"});
			log(prodInstId, "About to insert "+campaigns.size()+" campaigns");
			for(Campaign campaign: campaigns){
				int arg = 1;
				stat.clearParameters();
				
				setString(stat, arg++, prodInstId);
				setLong(stat, arg++, campaign.getCampaignGroupId());
				setString(stat, arg++, campaign.getType().name());
				setString(stat, arg++, campaign.getName());
				stat.execute();
	
				//if(campaign.getCampaignId() == null){
					Long key = getGeneratedKey(stat, "ca_campaign_id");
					campaign.setCampaignId(key);
				//}
			}

			log(prodInstId, "Finished inserting campaigns");
		}
		finally{
			close(stat);
		}

	}

	public static void insertAdGroups(Connection pdb, List<AdGroup> adGroups, String prodInstId)
	throws SQLException
	{
		String insertAdGroup = 
			"insert into ca_adgroup( " +
			"  prod_inst_id, " +
			"  ca_campaign_id, " +
			"  name, " +
			"  created_date " +
			") values( ?, ?, ?, now()) ";


		PreparedStatement stat = null;

		try{
			stat = pdb.prepareStatement(insertAdGroup, new String[]{"ca_adgroup_id"});
			log(prodInstId, "About to insert "+adGroups.size()+" adgroups");
			for(AdGroup adGroup: adGroups){
				int arg = 1;
				stat.clearParameters();				
				setString(stat, arg++, prodInstId);			
				setLong(stat, arg++, adGroup.getCampaignId());
				setString(stat, arg++, adGroup.getName());
				stat.execute();

				
				//if(adGroup.getAdGroupId() == null){
					Long key = getGeneratedKey(stat, "ca_adgroup_id");
					adGroup.setAdGroupId(key);
				//}
			}
			log(prodInstId, "Finished inserting adgroups");
		}
		finally{
			close(stat);
		}

	}


	public static void insertAds(Connection pdb, List<Ad> ads, String prodInstId)
	throws SQLException
	{
		String insertAd = 
			"insert into ca_ad( " +
			"  prod_inst_id, " +			
			"  ca_adgroup_id, " +
			"  headline, " +
			"  description, " +
			"  display_url, " +
			"  destination_url, " +
			"  created_date " +
			") values( ?, ?, ?, ?, ?, ?, now()) ";
			


		PreparedStatement stat = null;
		Ad ad = null;
		try{
			stat = pdb.prepareStatement(insertAd, new String[]{"ca_ad_id"});
			log(prodInstId, "About to insert "+ads.size()+" ads");
			for(Ad thisAd: ads){
				ad = thisAd;
				int arg = 1;
				stat.clearParameters();
				
				setString(stat, arg++, prodInstId);
				setLong(stat, arg++, ad.getAdGroupId());
				setString(stat, arg++, ad.getHeadline());
				setString(stat, arg++, ad.getDescription());
				setString(stat, arg++, ad.getDisplayUrl());
				setString(stat, arg++, ad.getDestinationUrl());
				stat.execute();

				//if(ad.getAdId() == null){
					Long key = getGeneratedKey(stat, "ca_ad_id");
					ad.setAdId(key);
				//}
			}
			log(prodInstId, "finished inserting ads");
		}
		catch(SQLException e){
			log(prodInstId, "Error Inserting Ad: "+ e +",  "+ BaseData.toString(ad));
			throw e;
		}
		finally{
			
			close(stat);
		}
	}

	public static void insertKeywords(Connection pdb, List<Keyword> keywords, String prodInstId)
	throws SQLException 
	{
		String insertKeyword = 
			"insert into ca_keyword( " +
			"  prod_inst_id, " +
			"  ca_adgroup_id, " +
			"  text, " +
			"  cpc_estimate, " +
			"  daily_clicks_estimate, " +
			"  search_volume_estimate, " +
			"  competition_scale, " +
			"  search_position_estimate, " +
			"  bid, " +
			"  created_date " +
			") values( ?, ?, ?, ?, ?, ?, ?, ?, ?, now() ) ";
			

		PreparedStatement stat = null;
		Keyword keyword = null;
		try{
			stat = pdb.prepareStatement(insertKeyword, new String[]{"ca_keyword_id"});
			log(prodInstId, "About to insert "+keywords.size()+" keywords");
			long t = System.currentTimeMillis();
			
			for(Keyword kw: keywords){
				keyword = kw;
				int arg = 1;
				stat.clearParameters();
				setString(stat, arg++, prodInstId);
				setLong(stat, arg++, keyword.getAdGroupId());
				setString(stat, arg++, keyword.getText());
				setDouble(stat, arg++, keyword.getCpcEstimate());
				setDouble(stat, arg++, keyword.getDailyClicksEstimate());
				setDouble(stat, arg++, keyword.getSearchVolumeEstimate());
				setLong(stat, arg++, keyword.getCompetitionScale());
				setDouble(stat, arg++, keyword.getSearchPositionEstimate());
				setDouble(stat, arg++, keyword.getBid());
				stat.execute();
				
				//if(keyword.getKeywordId() == null){
					Long key = getGeneratedKey(stat, "ca_keyword_id");			
					keyword.setKeywordId(key);
				//}
				
				
			}
			t = System.currentTimeMillis() - t;
			log(prodInstId, "finished inserting keywords, time: "+ t +"ms");
			//log(prodInstId, "Avg time/kw: "+ (t/keywords.size()) +"ms");
		}
		catch(SQLException e){
			log(prodInstId, "Error Inserting Keyword: "+ e +",  "+BaseData.toString(keyword));
			throw e;
		}
		finally{
			
			close(stat);
		}

	}
	
	
	
	public static  void insertLocationTargets(Connection pdb, List<LocationTarget> locations, String prodInstId)
	throws SQLException
	{		
		final String insertLocation =
			"insert into ca_location_targets( " +
			"  prod_inst_id, " +
			"  ca_analysis_id, " +
			"  city, " +
			"  state, " +
			"  is_primary, " +
			"  created_date " +
			") values( ?, ?, ?, ?, ?, now() ) ";		
		
		PreparedStatement stat = null;

		try{
			stat = pdb.prepareStatement(insertLocation);
			log(prodInstId, "About to insert "+locations.size()+" locations");
			for(LocationTarget loc: locations){
				int arg = 1;
				stat.clearParameters();
				
				setString(stat, arg++, prodInstId);
				setLong(stat, arg++, loc.getAnalysisId());
				setString(stat, arg++, loc.getCity());
				setString(stat, arg++, loc.getState());
				stat.setBoolean(arg++, loc.isPrimaryLocation());
				stat.execute();
			}
			log(prodInstId, "finished inserting locations");
		}
		finally{
			close(stat);
		}
	}
	
	public static  void insertSeedKeywords(Connection pdb, List<SeedKeyword> seedKeywords, String prodInstId)
	throws SQLException
	{		
		final String insertLocation =
			"insert into ca_seed_keyword( " +
			"  prod_inst_id, " +
			"  ca_analysis_id, " +
			"  seed_keyword,	" + 
			"  created_date " +
			") values( ?, ?, ?, now() ) ";		
		
		PreparedStatement stat = null;

		try{
			stat = pdb.prepareStatement(insertLocation);
			log(prodInstId, "About to insert "+seedKeywords.size()+" seed keywords");
			for(SeedKeyword word: seedKeywords){
				int arg = 1;
				stat.clearParameters();
				
				setString(stat, arg++, prodInstId);
				setLong(stat, arg++, word.getAnalysisId());
				setString(stat, arg++, word.getSeedKeyword());				
				stat.execute();
			}
			log(prodInstId, "finished inserting seed keywords");
		}
		finally{
			close(stat);
		}
	}
	
	public static void insertProximityTargets(Connection pdb, List<ProximityTarget> locations, String prodInstId)
	throws SQLException
	{		
		final String insertProximityTargets =
			"insert into ca_proximity_targets( " +
			"  prod_inst_id, " +
			"  ca_analysis_id, " +
			"  zip_code, " +
			"  radius, " +
			"  created_date " +
			") values( ?, ?, ?, ?, now() ) ";
		
		PreparedStatement stat = null;

		try{
			stat = pdb.prepareStatement(insertProximityTargets);
			log(prodInstId, "About to insert "+locations.size()+" proximity locations");
			for(ProximityTarget loc: locations){
				int arg = 1;
				stat.clearParameters();
				
				setString(stat, arg++, prodInstId);
				setLong(stat, arg++, loc.getAnalysisId());
				setString(stat, arg++, loc.getZipCode());
				setLong(stat, arg++, loc.getRadius());
				stat.execute();
			}
			log(prodInstId, "finished inserting proximity locations");
		}
		finally{
			close(stat);
		}
	}
	
	
	private static void deleteCampaigns(Connection pdb, String prodInstId)
	throws SQLException
	{
		final String[] deleteTables = {
				"delete from ca_ad where prod_inst_id = ?",
				"delete from ca_keyword where prod_inst_id = ?",
				"delete from ca_adgroup where prod_inst_id = ?",
				"delete from ca_campaign where prod_inst_id = ?",
				"delete from ca_campaign_group where prod_inst_id = ?",
				"delete from ca_proximity_targets where prod_inst_id = ?",
				"delete from ca_location_targets where prod_inst_id = ?",
				"delete from ca_seed_keyword where prod_inst_id = ?"
		};
		
		PreparedStatement stat = null;
		for(String deleteSql: deleteTables){
			try{
				stat = pdb.prepareStatement(deleteSql);
				log(prodInstId, "Delete from table:\n "+deleteSql);
				
				setString(stat, 1, prodInstId);
				stat.execute();
				
			}
			finally{
				close(stat);
			}
		}
	}


	/**
	 * Returns the new primary key after the statement has executed.
	 */
	private static Long getGeneratedKey(Statement stat, String keyName)
	throws SQLException
	{
		ResultSet rs = null;
		Long key = null;
		try{
			rs = stat.getGeneratedKeys();
			if(rs.next()){
				long k = rs.getLong(1);
				if(k > 0){
					key = k;
				}
				else{
					throw new SQLException("Null key returned: " + keyName);
				}
			}
		}		
		finally{
			close( rs);
		}
		return key;

	}

	private  static void setLong(PreparedStatement stat, int arg, Long value) throws SQLException{		
		if(value == null){ stat.setNull(arg, java.sql.Types.INTEGER);
		}
		else{ stat.setLong(arg, value);
		}
	}

	private  static void setDate(PreparedStatement stat, int arg, Date value) throws SQLException{		
		if(value == null){ stat.setNull(arg, java.sql.Types.TIMESTAMP);
		}
		else{ stat.setTimestamp(arg, new java.sql.Timestamp(value.getTime()));
		}
	}

	private  static void setString(PreparedStatement stat, int arg, String value) throws SQLException {
		if(value == null){  stat.setNull(arg, java.sql.Types.VARCHAR);
		}
		else{ stat.setString(arg, value);
		}
	}

	private  static void setDouble(PreparedStatement stat, int arg, Double value) throws SQLException{		
		if(value == null){ stat.setNull(arg, java.sql.Types.DOUBLE);
		}
		else{ stat.setDouble(arg, value);
		}

	}
	
	public static void close(ResultSet rs){
		if(rs != null) try{
			rs.close();
		}catch(Exception e){
			log("", "Unable to close sql resultSet: "+e);
		}
	}

	public static void close(Statement stat){			
		if(stat != null) try{
			stat.close();
		}catch(Exception e){
			log("", "Unable to close sql statement: "+e);
		}		
	}
	
	
	public static void log(String tag, String msg){
		Log.log(null, logCmp, Log.INFO, tag, msg);
	}
	

}
