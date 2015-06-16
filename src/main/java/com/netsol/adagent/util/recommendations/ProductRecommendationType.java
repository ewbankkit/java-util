package com.netsol.adagent.util.recommendations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.CalendarUtil;
import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Product recommendation type data from the product_recommendation_type_xref table.
 * 
 * @author Adam S. Vernon
 */
public class ProductRecommendationType {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:23 ProductRecommendationType.java NSI";
	private static final Log log = LogFactory.getLog(ProductRecommendationType.class);
	
	/** The product instance ID. */
	private String prodInstId;
	/** The recommendation type ID. */
	private Long recommendationTypeId;
	/** The last time the recommendations engine ran for this product and recommendation type. */
	private Calendar lastRunDate;
	
	/**
	 * Constructor.
	 * 
	 * @param prodInstId
	 * @param recommendationTypeId
	 * @param Calendar lastRunDate
	 * @throws Exception
	 */
	public ProductRecommendationType(String prodInstId, Long recommendationTypeId, Calendar lastRunDate) throws Exception {
		this.prodInstId = prodInstId;
		this.recommendationTypeId = recommendationTypeId;
		this.lastRunDate = lastRunDate;
	}

	/**
	 * Constructor.
	 * 
	 * @param prodInstId
	 * @param recommendationTypeId
	 * @throws Exception
	 */
	public ProductRecommendationType(String prodInstId, Long recommendationTypeId) throws Exception {
		this(prodInstId, recommendationTypeId, null);
	}

	/**
	 * @return the prodInstId
	 */
	public String getProdInstId() {
		return prodInstId;
	}

	/**
	 * @param prodInstId the prodInstId to set
	 */
	public void setProdInstId(String prodInstId) {
		this.prodInstId = prodInstId;
	}
	
	/**
	 * @return the recommendationTypeId
	 */
	public Long getRecommendationTypeId() {
		return recommendationTypeId;
	}

	/**
	 * @param recommendationTypeId the recommendationTypeId to set
	 */
	public void setRecommendationTypeId(Long recommendationTypeId) {
		this.recommendationTypeId = recommendationTypeId;
	}
	
	/**
	 * @return the lastRunDate
	 */
	public Calendar getLastRunDate() {
		return lastRunDate;
	}

	/**
	 * @param lastRunDate the lastRunDate to set
	 */
	public void setLastRunDate(Calendar lastRunDate) {
		this.lastRunDate = lastRunDate;
	}
	
	//
	// Database
	//
	
	/** SQL to query product recommendation type data for a product. */
	private static final String GET_PRODUCT_RECOMMENDATION_TYPES_SQL = 
		"select recommendation_type_id, last_run_date from product_recommendation_type_xref where prod_inst_id=?";
	
	/**
	 * Get the ProductRecommendationType for this product. 
	 * 
	 * @param pdbConn
	 * @param prodInstId
	 * @return the ProductRecommendationType for this product or null if it does not exist
	 * @throws Exception
	 */
	public static Map<Long, ProductRecommendationType> getProductRecommendationTypeMap(Connection pdbConn, String prodInstId) throws Exception {
		PreparedStatement pstmt = null;
		Map<Long, ProductRecommendationType> map = new HashMap<Long, ProductRecommendationType>();
		try {				
			pstmt = pdbConn.prepareStatement(GET_PRODUCT_RECOMMENDATION_TYPES_SQL);
			pstmt.setString(1, prodInstId);
			log.info(pstmt);
			ResultSet results = pstmt.executeQuery();
			
			while (results != null && results.next()) {
				int i = 1;
				Long recommendationTypeId = results.getLong(i++);
				Date lastRunDate = results.getDate(i++);
				Calendar c = null;
				if (lastRunDate != null) {
					c = CalendarUtil.dateToCalendar(lastRunDate);
				}
				ProductRecommendationType productRecommendationType = new ProductRecommendationType(prodInstId, recommendationTypeId, c);
				map.put(recommendationTypeId, productRecommendationType);
			}
		} 

		finally {
			BaseHelper.close(pstmt);
		}
		return map;
	}
	
	
	/**
	 * SQL to insert the record.
	 */
	private static final String INSERT_PRODUCT_RECOMMENDATION_TYPE_SQL = 
		"insert into product_recommendation_type_xref (prod_inst_id, recommendation_type_id, last_run_date, created_date, updated_by_user) "
		+ "values (?, ?, ?, now(), ?)";
	
	/**
	 * Insert the record.
	 * 
	 * @param pdbConn
	 * @param updatedBy
	 * @throws Exception
	 */
	public void insert(Connection pdbConn, String updatedBy) throws Exception {
		PreparedStatement pstmt = null;
		try {				
			pstmt = pdbConn.prepareStatement(INSERT_PRODUCT_RECOMMENDATION_TYPE_SQL);
			pstmt.setString(1, prodInstId);
			pstmt.setLong(2, recommendationTypeId);
			pstmt.setTimestamp(3, (lastRunDate == null ? null : new Timestamp(lastRunDate.getTimeInMillis())));
			pstmt.setString(4, updatedBy);
			log.info(pstmt);
			pstmt.executeUpdate();
		} 
		finally {
			BaseHelper.close(pstmt);
		}
	}
	
	/**
	 * SQL to update the record.
	 */
	private static final String UPDATE_PRODUCT_RECOMMENDATION_TYPE_SQL = 
		"update product_recommendation_type_xref set last_run_date = ?, updated_by_user=? "
		+ "where prod_inst_id=? and recommendation_type_id=?;";
	
	/**
	 * Update the record.
	 * 
	 * @param pdbConn
	 * @param updatedBy
	 * @throws Exception
	 */
	public void update(Connection pdbConn, String updatedBy) throws Exception {
		PreparedStatement pstmt = null;
		try {				
			pstmt = pdbConn.prepareStatement(UPDATE_PRODUCT_RECOMMENDATION_TYPE_SQL);
			pstmt.setTimestamp(1, (lastRunDate == null ? null : new Timestamp(lastRunDate.getTimeInMillis())));
			pstmt.setString(2, updatedBy);
			pstmt.setString(3, prodInstId);
			pstmt.setLong(4, recommendationTypeId);
			log.info(pstmt);
			pstmt.executeUpdate();
		} 
		finally {
			BaseHelper.close(pstmt);
		}
	}
	
	//
	// Unit test
	//
	
	public static void main(String[] args) {
		Connection pdbConn = null;
		try {
			// Get dev pdb2 connection.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		    pdbConn = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4301/adagent?user=adagent&password=adagent");

		    ProductRecommendationType prt = new ProductRecommendationType("WN-DIY-TEST4", 1L);
		    prt.insert(pdbConn, "RecommendationEngine");
		    prt.setLastRunDate(Calendar.getInstance());
		    prt.update(pdbConn, "RecommendationEngine");   
		}
		catch(Throwable e) {
			e.printStackTrace(System.out);
		}
		finally {
	    	BaseHelper.close(pdbConn);
		}
	}
}
