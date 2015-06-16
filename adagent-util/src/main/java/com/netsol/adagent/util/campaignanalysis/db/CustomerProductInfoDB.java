package com.netsol.adagent.util.campaignanalysis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import registrar.base.log.Log;

import com.netsol.adagent.util.campaignanalysis.db.beans.CustomerProductInfo;


public class CustomerProductInfoDB {

	public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:18 CustomerProductInfoDB.java NSI";
	//static Class className = CustomerProductInfoDB.class;
	//private static Log logger = LogFactory.getLog(className);
	private static final String logCmp = "adagent";
	
	
	/** method returns information related to a product and also customer data . If records not found then null is returned.
	 * 
	 * @param prodInstId
	 * @param pdbConn
	 * @return CustomerProductInfo
	 * @throws DataServicesException
	 */
	public static CustomerProductInfo getCustomerProductInfo(Connection globalConnection, String prodInstId )
			throws SQLException {
		
		//logger.info("Entering CustomerProductInfoDB.getCustomerProductInfo");	
		log(prodInstId, "Entering CustomerProductInfoDB.getCustomerProductInfo");
		
		CustomerProductInfo custInfo= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		String qryStry = "select base_target, url,  avg_customer_spend, conv_rate , prod_services, " 
		 + " location, city, state, zip_code, radius "
		 + " from product p, product_pricing pp, ci_customer_info ci " 
		 + " where p.prod_inst_id = pp.prod_inst_id and p.prod_inst_id = ci.prod_inst_id "
		 + " and p.prod_inst_id=? ";
		
		
		try {
			 pstmt = globalConnection.prepareStatement(qryStry);
			 pstmt.setString(1, prodInstId);
			 rs = pstmt.executeQuery();
			 
			 if (rs.next()){
				 custInfo = new CustomerProductInfo();
				 custInfo.setAvgTicket(rs.getFloat("avg_customer_spend"));
				 custInfo.setMonthlyBudget(rs.getDouble("base_target"));
				 custInfo.setConvRate(rs.getDouble("conv_rate"));
				 custInfo.setUrl(rs.getString("url"));				 		 
				 custInfo.setProdInstId(prodInstId);
				 custInfo.setSeedKeywords(rs.getString("prod_services"));						 
				 custInfo.setLocation(rs.getString("location"));
				 custInfo.setZipCode(rs.getInt("zip_code"));
				 custInfo.setCity(rs.getString("city"));
			 }else{
				 //logger.info("No record found for this product " + prodInstId);
				 log(prodInstId, "No record found for this product " + prodInstId);
				 custInfo= null; 
			 }
		} catch (SQLException excp) {
			//logger.error("SQLException executing query:" + excp.getMessage(),			
			//		excp);
			
			log(prodInstId, "SQLException executing query:" + excp.getMessage());
			
		} finally {
			close(rs);
			close(pstmt);			
			
		}

		return custInfo;
	}
	
	private static void close(Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (Exception e) {
				//logger.error( "Cannot close stmt: " + e.toString());
				log("",  "Cannot close stmt: " + e.toString());
			}
		}
	}
	private static void close(ResultSet rs) {
		if (rs != null){
			try {
				rs.close();
			} catch (Exception e) {
				//logger.error( "Cannot close RS: " + e.toString());
				log("",  "Cannot close RS: " + e.toString());
			}
		}
	}
	
	public static void log(String tag, String msg){
		Log.log(null, logCmp, Log.INFO, tag, msg);
	}
}
