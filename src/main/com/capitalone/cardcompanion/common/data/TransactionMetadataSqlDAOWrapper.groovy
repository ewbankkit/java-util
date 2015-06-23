package com.capitalone.cardcompanion.common.data

import java.sql.Connection

class TransactionMetadataSqlDAOWrapper implements TransactionMetadataDAO {

	@Override
	public List<String> getDistinctTags(String accountId, Integer sorId) {
		DatabaseConnectionPool.instance.transactionMetadataDataSource.withConnection {
			new TransactionMetadataSqlDAO(it as Connection).getDistinctTags(accountId, sorId)
		}
	}

	@Override
	public void addTags(String transactionId, String accountId, Integer sorId,	List<String> tags) {
		DatabaseConnectionPool.instance.transactionMetadataDataSource.withTransaction {
			new TransactionMetadataSqlDAO(it as Connection).addTags(transactionId, accountId, sorId, tags)
		}
		
	}

	@Override
	public void updateTag(String transactionId, String accountId, Integer sorId, String tag, String updatedTag) {
		DatabaseConnectionPool.instance.transactionMetadataDataSource.withTransaction {
			new TransactionMetadataSqlDAO(it as Connection).updateTag(transactionId, accountId, sorId, tag, updatedTag)
		}
	}

	@Override
	public void removeTags(String transactionId, String accountId, Integer sorId, List<String> tags) {
		DatabaseConnectionPool.instance.transactionMetadataDataSource.withTransaction {
			new TransactionMetadataSqlDAO(it as Connection).removeTags(transactionId, accountId, sorId, tags)
		}
	}

	@Override
	public List<String> getTaggedTransactions(String accountId, Integer sorId, String tag) {
		DatabaseConnectionPool.instance.transactionMetadataDataSource.withConnection {
			new TransactionMetadataSqlDAO(it as Connection).getTaggedTransactions(accountId, sorId, tag)
		}
	}

	@Override
	public List<Tag> getAllTags(String transactionId, String accountId,	Integer sorId) {
		DatabaseConnectionPool.instance.transactionMetadataDataSource.withConnection {
			new TransactionMetadataSqlDAO(it as Connection).getAllTags(transactionId, accountId, sorId)
		}
	}

	@Override
	public List<TransactionMetadata> getAllTags(String accountId, Integer sorId) {
		DatabaseConnectionPool.instance.transactionMetadataDataSource.withConnection {
			new TransactionMetadataSqlDAO(it as Connection).getAllTags(accountId, sorId)
		};
	}

}
