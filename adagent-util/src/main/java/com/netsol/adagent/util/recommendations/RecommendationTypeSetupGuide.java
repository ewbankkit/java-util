package com.netsol.adagent.util.recommendations;

import java.io.Serializable;
import java.sql.Connection;

/**
 * Recommendation type = setup guide.
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public class RecommendationTypeSetupGuide extends RecommendationType implements Serializable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:26 RecommendationTypeSetupGuide.java NSI";
	
	//
	// RecommendationType abstract interface implementation
	//

	/**
	 * This method does nothing for this recommendation type.
	 * 
	 * @paran pdbConn
	 * @param prodInstId
	 * @param updatedBy
	 * @return
	 */
	protected boolean generateRecommendations(Connection pdbConn, String prodInstId, String updatedBy) throws Exception {
		return false;
	}
}
