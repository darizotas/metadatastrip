/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip.metadata.extractor;

import java.io.File;
import java.io.IOException;

import com.darizotas.metadatastrip.metadata.GroupContainer;

import android.location.Location;

/**
 * Abstract class in charge of extracting the metadata associated to a given file.
 * @author darizotas
 *
 */
public abstract class AbstractMetaDataExtractor {
	protected GroupContainer mContainer = null;
	protected Location mLocation = null;

	/**
	 * Extracts the metadata associated to the given file.
	 * @param file File that contains metadata.
	 * @throws IOException, MetadataProcessingException
	 */
	public abstract void extract(File file) throws IOException, MetadataProcessingException;
	
	/**
	 * Gets the extracted metadata.
	 * @return Metadata extracted or null.
	 */
	public GroupContainer getMetadata() {
		return mContainer;
	}

	/**
	 * Gets the geo-location, if any, extracted from the metadata.
	 * @return Location or null, if not defined.
	 */
	public Location getLocation() {
		return mLocation;
	}
}
