package com.netsol.adagent.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Lead prices for all channels by lead type.
 * 
 * @author Adam S. Vernon
 */
public class LeadPriceMap {
    static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:04 LeadPriceMap.java NSI";
    
	private static final String GET_LEAD_PRICES = "select * from base_tier";
	
	/** A map of channel_id to lead_type price map. */
	private Map<Integer, Map<Integer, Double>> channelMap;
	
	/**
	 * Construct the object.
	 */
	public LeadPriceMap(Connection gdbConn) throws Exception {

		channelMap = new HashMap<Integer, Map<Integer, Double>>();

		PreparedStatement stmt = gdbConn.prepareStatement(GET_LEAD_PRICES);
		ResultSet results = stmt.executeQuery();
		while(results != null && results.next()) {
			Integer channelId = results.getInt("channel_id");
			Integer leadTypeId = results.getInt("lead_type_id");
			Double price = results.getDouble("price");
			addPrice(channelId, leadTypeId, price);
		}
	}
	
	/** Add a price to the map. */
	public void addPrice(Integer channelId, Integer leadTypeId, Double price) {
		Map<Integer, Double> leapTypeMap = channelMap.get(channelId);
		if (leapTypeMap == null) {
			leapTypeMap = new HashMap<Integer, Double>();
			channelMap.put(channelId, leapTypeMap);
		}
		leapTypeMap.put(leadTypeId, price);
	}
	
	/** Get a price from the map. */
	public Double getPrice(Integer channelId, Integer leadTypeId) {
		Map<Integer, Double> leadTypeMap = channelMap.get(channelId);
		if (leadTypeMap == null) {
			return null;
		}
		return leadTypeMap.get(leadTypeId);
	}
	
	/** Override toString. Returns all the data in the map. */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Set<Integer> channelIds = channelMap.keySet();
		for (Integer channelId : channelIds) {
			Map<Integer, Double> leapTypeMap = channelMap.get(channelId);
			Set<Integer> leadTypeIds = leapTypeMap.keySet();
			for (Integer leadTypeId : leadTypeIds) {
				Double price = leapTypeMap.get(leadTypeId);
				sb.append("channelId=" + channelId + ", leadTypeId=" + leadTypeId + ", price=" + price + "\n");
			}
		}
		return sb.toString();
	}
}
