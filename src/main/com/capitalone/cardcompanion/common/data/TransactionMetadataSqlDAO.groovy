package com.capitalone.cardcompanion.common.data

import java.sql.Connection
import java.sql.Timestamp

import com.google.common.base.Preconditions

class TransactionMetadataSqlDAO extends AbstractSqlDAO implements TransactionMetadataDAO {
	
	private static final String UNIQUE_TAGS = 'SELECT DISTINCT(TAG) FROM TRANSACTION_METADATA WHERE ACCOUNT_ID = ? AND SOR_ID = ? ORDER BY TAG'
	
	private static final String INSERT_METADATA = 'INSERT INTO TRANSACTION_METADATA (TRANSACTION_ID, ACCOUNT_ID, SOR_ID, CREATED_DATE, TAG) VALUES (?, ?, ?, ?, ?)'
	
	private static final String UPDATE_METADATA = 'UPDATE TRANSACTION_METADATA SET TAG = ? WHERE TRANSACTION_ID = ? AND ACCOUNT_ID = ? AND SOR_ID = ? AND TAG = ?'
	
	private static final String DELETE_METADATA = 'DELETE FROM TRANSACTION_METADATA WHERE TRANSACTION_ID = ? AND ACCOUNT_ID = ? AND SOR_ID = ? AND TAG = ?'
	
	private static final String SELECT_BY_TAG = 'SELECT TRANSACTION_ID FROM TRANSACTION_METADATA WHERE ACCOUNT_ID = ? AND SOR_ID = ? AND TAG = ? ORDER BY TRANSACTION_ID'
	
	private static final String ALL_TAGS_FOR_TRANSACTION = 'SELECT TAG, CREATED_DATE FROM TRANSACTION_METADATA WHERE TRANSACTION_ID = ? AND ACCOUNT_ID = ? AND SOR_ID = ? ORDER BY TAG'
	
	private static final String ALL_TAGS_FOR_ACCOUNT = 'SELECT TRANSACTION_ID, ACCOUNT_ID, SOR_ID, CREATED_DATE, TAG FROM TRANSACTION_METADATA WHERE ACCOUNT_ID = ? AND SOR_ID = ? ORDER BY TRANSACTION_ID, TAG'
	

	public TransactionMetadataSqlDAO(Connection connection) {
		super('transactionmetadata', connection);
	}

	@Override
	public List<String> getDistinctTags(String accountId, Integer sorId) {
		Preconditions.checkNotNull accountId
		Preconditions.checkNotNull sorId

        find(UNIQUE_TAGS, [accountId, sorId]) {
            it.TAG
        }
	}

	@Override
	public void addTags(String transactionId, String accountId, Integer sorId, List<String> tags) {
		Preconditions.checkNotNull transactionId
		Preconditions.checkNotNull accountId
		Preconditions.checkNotNull sorId
		Preconditions.checkNotNull tags
		
		tags.each {
			executeUpdate(INSERT_METADATA,
	            [transactionId,
	             accountId,
	             sorId,
				 new Timestamp(System.currentTimeMillis()),
	             it]
	        )
		}
	}

	@Override
	public void updateTag(String transactionId, String accountId, Integer sorId, String tag, String updatedTag) {
		Preconditions.checkNotNull transactionId
		Preconditions.checkNotNull accountId
		Preconditions.checkNotNull sorId
		Preconditions.checkNotNull tag
		Preconditions.checkNotNull updatedTag
		
		executeUpdate(UPDATE_METADATA,
			[updatedTag,
			 transactionId,
			 accountId,
			 sorId,
			 tag]
		)
		
	}

	@Override
	public void removeTags(String transactionId, String accountId, Integer sorId, List<String> tags) {
		Preconditions.checkNotNull transactionId
		Preconditions.checkNotNull accountId
		Preconditions.checkNotNull sorId
		Preconditions.checkNotNull tags
		
		tags.each {
			executeUpdate(DELETE_METADATA,
	            [transactionId,
	             accountId,
	             sorId,
	             it]
	        )
		}
		
	}

	@Override
	public List<String> getTaggedTransactions(String accountId, Integer sorId, String tag) {
		Preconditions.checkNotNull accountId
		Preconditions.checkNotNull sorId
		Preconditions.checkNotNull tag
		
		find(SELECT_BY_TAG, [accountId, sorId, tag]) {
			it.TRANSACTION_ID as String
		}
	}

	@Override
	public List<Tag> getAllTags(String transactionId, String accountId,	Integer sorId) {
		Preconditions.checkNotNull transactionId
		Preconditions.checkNotNull accountId
		Preconditions.checkNotNull sorId
		
		find(ALL_TAGS_FOR_TRANSACTION, [transactionId, accountId, sorId]) {
			tag it
		}
	}

	@Override
	public List<TransactionMetadata> getAllTags(String accountId, Integer sorId) {
		Preconditions.checkNotNull accountId
		Preconditions.checkNotNull sorId
		
		List<TransactionMetadata> flattenedList = find(ALL_TAGS_FOR_ACCOUNT, [accountId, sorId]) {
			transactionMetadata it
		}
		
		Map<String, TransactionMetadata> transactionMetadataMap = new TreeMap<String, TransactionMetadata>();
		
		flattenedList.each {
			consolidate(it, transactionMetadataMap)
		}
		 
		new ArrayList<TransactionMetadata>(transactionMetadataMap.values())
	}
	
	/**
	 * Creates a Tag from a result set.
	 */
	private static Tag tag(def result) {
		new Tag(
			  text          : result.TAG,
			  createdDate   : result.CREATED_DATE as Date
		)
	}
	
	/**
	 * Creates a TransactionMetadata from a result set.
	 */
	private static TransactionMetadata transactionMetadata(def result) {
		new TransactionMetadata (
				sorId         : result.SOR_ID as Integer,
				accountId     : result.ACCOUNT_ID,
				transactionId : result.TRANSACTION_ID,
				tags: [tag(result)]
		)
	}
	
	private void consolidate(TransactionMetadata transactionMetadata, Map<String, TransactionMetadata> transactionMetadataMap) {
		String transactionId = transactionMetadata.transactionId 
		
		TransactionMetadata existingTransactionMetadata = transactionMetadataMap[transactionId]
		
		if (!existingTransactionMetadata) {
			
			existingTransactionMetadata = new TransactionMetadata(
										         sorId         : transactionMetadata.sorId,
										         accountId     : transactionMetadata.accountId,
										         transactionId : transactionMetadata.transactionId
								          )
			
			transactionMetadataMap[transactionId] = existingTransactionMetadata
			
		}
		
		existingTransactionMetadata.addTags(transactionMetadata.tags)
	}
}
