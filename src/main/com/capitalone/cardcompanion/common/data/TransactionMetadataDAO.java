package com.capitalone.cardcompanion.common.data;

import java.util.List;


public interface TransactionMetadataDAO {
	
	public List<String> getDistinctTags(String accountId, Integer sorId);

	public void addTags(String transactionId, String accountId, Integer sorId, List<String> tags);
	
	public void updateTag(String transactionId, String accountId, Integer sorId, String tag, String updatedTag);

	public void removeTags(String transactionId, String accountId, Integer sorId, List<String> tags);

	public List<String> getTaggedTransactions(String accountId, Integer sorId, String tag);
	
	public List<Tag> getAllTags(String transactionId, String accountId, Integer sorId);
	
	public List<TransactionMetadata> getAllTags(String accountId, Integer sorId);

}
