package com.capitalone.cardcompanion.common.data

import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation

class Tag {
	String text
	Date createdDate
	
	@Override
	String toString() {
		ReflectiveRepresentation.toString(this)
	}
}
