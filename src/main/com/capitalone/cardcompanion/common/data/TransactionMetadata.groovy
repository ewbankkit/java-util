package com.capitalone.cardcompanion.common.data

import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation
import com.google.common.base.Strings


class TransactionMetadata {
	Integer sorId
	String accountId
	String transactionId
	List<Tag> tags
	
	public List<Tag> getTags() {
		if (this.tags == null) {
			this.tags = new ArrayList<Tag>();
		}
		return tags;
	}

	public void setTags(List<Tag> tags) {
		if (this.tags == null) {
			this.tags = new ArrayList<Tag>();
		}
		
		this.tags.clear();
		
		if (tags != null) {
			tags.each ({
				if(!this.tags.contains(it) && !Strings.isNullOrEmpty(it.text)) {
					this.tags.add(it)
				}
			});
		}
	}
	
	public void addTag(Tag tag) {
		if (this.tags == null) {
			this.tags = new ArrayList<Tag>()
		}
		
		if(!this.tags.contains(tag) && !Strings.isNullOrEmpty(tag.getText())) {
			this.tags.add(tag)
		}
	}
	
	public void addTags(List<Tag> tags) {
		if (this.tags == null) {
			this.tags = new ArrayList<Tag>()
		}
		if (tags != null) {
			tags.each ({
				if(!this.tags.contains(it) && !Strings.isNullOrEmpty(it.text)) {
					this.tags.add(it)
				}
			});
		}
	}
	
	@Override
	String toString() {
		ReflectiveRepresentation.toString(this)
	}
}
