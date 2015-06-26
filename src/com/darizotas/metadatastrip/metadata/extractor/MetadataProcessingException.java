/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip.metadata.extractor;

/**
 * Base class for metadata processing exceptions.
 * @author dario
 *
 */
public class MetadataProcessingException extends Exception {
	private static final long serialVersionUID = -2152047798645766984L;

	public MetadataProcessingException() {
		super();
	}

	public MetadataProcessingException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MetadataProcessingException(String detailMessage) {
		super(detailMessage);
	}

	public MetadataProcessingException(Throwable throwable) {
		super(throwable);
	}
}
