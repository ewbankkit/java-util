package com.netsol.adagent.util.recommendations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Calendar;
import java.util.Map;

import com.netsol.adagent.util.CalendarUtil;
import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Simple facade for managing recommendations for products.
 * 
 * @author Adam S. Vernon
 */
public class RecommendationEngine {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:24 RecommendationEngine.java NSI";
	
	/** String constant for updated_by_user fields in the database. */
	private static final String UPDATED_BY = "RecommendationEngine";
	
	/** The singleton instance. */
	private static final RecommendationEngine instance = new RecommendationEngine();
	
	/** Get the singleton instance. */
	public static RecommendationEngine getInstance() { return instance; }

	/** Private constructor enforces the singleton pattern. */
	private RecommendationEngine() {}
	
	/**
	 * Get all non-deleted recommendations for the product.
	 * 
	 * @param pdbConn
	 * @param prodInstId
	 * @return the array of Recommendations
	 * @throws Exception
	 */
	public Recommendation[] getRecommendations(Connection pdbConn, String prodInstId) throws Exception {
		return RecommendationFactory.getInstance().getRecommendations(pdbConn, prodInstId);
	}
	
	/**
	 * Generate recommendations for a product. This method is intended to be called from a nightly batch process.
	 * 
	 * @param pdbConn
	 * @param prodInstId
	 * @throws Exception
	 */
	public void generateRecommendationsForProduct(Connection pdbConn, String prodInstId) throws Exception {
		// Initialize type data.
		RecommendationTypeFactory rtf = RecommendationTypeFactory.getInstance();
		RecommendationType[] types = rtf.getRecommendationTypes(pdbConn);
		Map<Long, ProductRecommendationType> productRecommendationTypeMap = 
			ProductRecommendationType.getProductRecommendationTypeMap(pdbConn, prodInstId);
		// Loop over the recommendation types.
		for (RecommendationType type : types) {
			Long recommendationTypeId = type.getRecommendationTypeId();
			ProductRecommendationType productRecommendationType = productRecommendationTypeMap.get(recommendationTypeId);
			boolean generate = false;
			if (productRecommendationType == null) {
				productRecommendationType = new ProductRecommendationType(prodInstId, recommendationTypeId);
				productRecommendationType.insert(pdbConn, UPDATED_BY);
				generate = true;
			}
			else {
				// Check to see if it is time to generate more recommendations of this type for the product.
				Calendar lastRunDate = productRecommendationType.getLastRunDate() == null ? null 
						: CalendarUtil.zeroOutTimePart(productRecommendationType.getLastRunDate());
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DAY_OF_YEAR, -(type.getFrequencyDays()));
				generate = (lastRunDate == null || lastRunDate.before(c));
			}
			
			if (generate) {
				// It's time to generate this type of recommendation for the product.
				if (type.generateRecommendations(pdbConn, prodInstId, UPDATED_BY)) {
					// Update lastRunDate to today for this type.
					productRecommendationType.setLastRunDate(Calendar.getInstance());
					productRecommendationType.update(pdbConn, UPDATED_BY);
				}
			}
		}
	}
	
	/**
	 * A lead tracking recommendation is generated when the product is set up. This method provides a simple interface to 
	 * support this feature. 
	 * 
	 * @param pdbConn
	 * @param prodInstId
	 */
	public void createLeadTrackingRecommendation(Connection pdbConn, String prodInstId) throws Exception {
		createRecommendation(pdbConn, prodInstId, RECOMMENDATION_TYPE_ID_LEAD_TRACKING);
	}

	
	/**
	 * The setup guide recommendation is a special case that is generated only once when the product is set up. This method
	 * provides a simple interface to support this feature. 
	 * 
	 * @param pdbConn
	 * @param prodInstId
	 */
	public void createSetupGuideRecommendation(Connection pdbConn, String prodInstId) throws Exception {
		createRecommendation(pdbConn, prodInstId, RECOMMENDATION_TYPE_ID_SETUP_GUIDE);
	}
	
	//
	// Private helper methods
	//

	/** Constant for lead tracking recommendation_type_id. */
	private static final Long RECOMMENDATION_TYPE_ID_LEAD_TRACKING = 6L;

	/** Constant for set guide recommendation_type_id. */
	private static final Long RECOMMENDATION_TYPE_ID_SETUP_GUIDE = 7L;

	/**
	 * Reusable method to create a recommendation for specific one-time events.
	 * 
	 * @param pdbConn
	 * @param prodInstId	 
	 */
	private void createRecommendation(Connection pdbConn, String prodInstId, Long recommendationTypeId) throws Exception {
		// Insert or update product recommendation type.
		Map<Long, ProductRecommendationType> productRecommendationTypeMap = 
			ProductRecommendationType.getProductRecommendationTypeMap(pdbConn, prodInstId);
		ProductRecommendationType productRecommendationType = productRecommendationTypeMap.get(recommendationTypeId);
		if (productRecommendationType == null) {
			productRecommendationType = new ProductRecommendationType(prodInstId, recommendationTypeId, Calendar.getInstance());
			productRecommendationType.insert(pdbConn, UPDATED_BY);
		}
		else {
			productRecommendationType.setLastRunDate(Calendar.getInstance());
			productRecommendationType.update(pdbConn, UPDATED_BY);	
		}

		// Insert or update recommendation.
		RecommendationFactory rf = RecommendationFactory.getInstance();
		Map<Object, Recommendation> activeRecommendationMap = rf.getActiveRecommendationsOfType(pdbConn, prodInstId, 
				RecommendationTypeFactory.getInstance().getRecommendationType(pdbConn, recommendationTypeId));
		Recommendation activeRecommendation = activeRecommendationMap.get(prodInstId);
		if (activeRecommendation == null) {
			RecommendationTypeFactory rtf = RecommendationTypeFactory.getInstance();
			RecommendationType type = rtf.getRecommendationType(pdbConn, recommendationTypeId);
			Recommendation recommendation = new Recommendation(prodInstId, type);
			recommendation.insert(pdbConn, UPDATED_BY);			
		}
		else {
			activeRecommendation.update(pdbConn, UPDATED_BY);
		}
	}
		
	//
	// Unit test
	//
	
	/**
	 * Unit test.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Test last run date logic.
		Calendar past = Calendar.getInstance();
		past.add(Calendar.DAY_OF_YEAR, -1);
		Calendar lastRunDate = CalendarUtil.stringToCalendar("2011-03-01");
		System.out.println("lastRunDate=" + CalendarUtil.calendarToString(lastRunDate));
		System.out.println("past=" + CalendarUtil.calendarToString(past));
		if (lastRunDate.before(past)) {
			System.out.println("Ok");
		}
		else {
			System.out.println("Not Ok");
		}
		
		Connection pdbConn = null;
		try {
			// Get dev pdb2 connection.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		    pdbConn = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4301/adagent?user=adagent&password=adagent");

			// Create setup alerts.
		    String prodInstId="WN-DIY-TEST9";
			RecommendationEngine.getInstance().createSetupGuideRecommendation(pdbConn, prodInstId);
			RecommendationEngine.getInstance().createLeadTrackingRecommendation(pdbConn, prodInstId);

			// Generate recommendations for a product.
			RecommendationEngine.getInstance().generateRecommendationsForProduct(pdbConn, prodInstId);
			
			// Log
			Recommendation[] recommendations = RecommendationEngine.getInstance().getRecommendations(pdbConn, prodInstId);
			for (Recommendation r : recommendations) {
				System.out.println(r);
			}
		}
		catch(Throwable e) {
			e.printStackTrace(System.out);
		}
		finally {
	    	BaseHelper.close(pdbConn);
		}
	}
}
