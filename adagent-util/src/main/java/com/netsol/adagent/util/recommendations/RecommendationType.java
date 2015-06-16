package com.netsol.adagent.util.recommendations;

import java.io.Serializable;
import java.sql.Connection;

/**
 * Base recommendation type class.
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public abstract class RecommendationType implements Serializable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:25 RecommendationType.java NSI";

	/** Category enumeration. */
	public enum Category { ALERT, RECOMMENDATION };
	/** Entity type enumeration. */
	public enum EntityType { PRODUCT, CAMPAIGN, AD_GROUP, AD, KEYWORD };
	
	/** The primary key of the GDB recommendation_type table. */
	private Long recommendationTypeId;
	/** The name of the recommendation type. */
	private String name;
	/** Is this recommendation type enabled? */
	private Boolean isEnabled;
	/** The category. */
	private String category;
	/** The entity type. */
	private String entityType;
	/** The priority; 1 is highest. */
	private Integer priority;
	/** The frequency in days. */
	private Integer frequencyDays;
	/** The type class name. */
	private String typeClassName;
	/** The data class name. */
	private String dataClassName;
	
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isEnabled
	 */
	public Boolean getIsEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the entityType
	 */
	public String getEntityType() {
		return entityType;
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	/**
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @return the frequencyDays
	 */
	public Integer getFrequencyDays() {
		return frequencyDays;
	}

	/**
	 * @param frequencyDays the frequencyDays to set
	 */
	public void setFrequencyDays(Integer frequencyDays) {
		this.frequencyDays = frequencyDays;
	}
	
	/**
	 * @return the typeClassName
	 */
	public String getTypeClassName() {
		return typeClassName;
	}

	/**
	 * @param typeClassName the typeClassName to set
	 */
	public void setTypeClassName(String typeClassName) {
		this.typeClassName = typeClassName;
	}

	/**
	 * @return the dataClassName
	 */
	public String getDataClassName() {
		return dataClassName;
	}

	/**
	 * @param dataClassName the dataClassName to set
	 */
	public void setDataClassName(String dataClassName) {
		this.dataClassName = dataClassName;
	}

	/**
	 * Override Object.toString().
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("recommendationTypeId=" + recommendationTypeId + ", ");
		sb.append("name=" + name + ", ");
		sb.append("isEnabled=" + isEnabled + ", ");
		sb.append("category=" + category + ", ");
		sb.append("entityType=" + entityType + ", ");
		sb.append("priority=" + priority + ", ");
		sb.append("frequencyDays=" + frequencyDays + ", ");
		sb.append("typeClassName=" + typeClassName + ", ");
		sb.append("dataClassName=" + dataClassName);
		return sb.toString();
	}
	
	//
	// Abstract interface
	//

	/**
	 * Generate recommendations of the appropriate type for the product, if needed.
	 * 
	 * @paran pdbConn
	 * @param prodInstId
	 * @param updatedBy
	 * @return true if recommendations were generated, false otherwise
	 */
	protected abstract boolean generateRecommendations(Connection pdbConn, String prodInstId, String updatedBy) throws Exception;
}
