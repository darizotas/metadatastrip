/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 * This file makes of third party libraries:
 *    1. Metadata-extractor
 *        http://drewnoakes.com/code/exif/
 *        http://code.google.com/p/metadata-extractor/ 
 */
package com.darizotas.metadatastrip.metadata.extractor;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.location.Location;

import com.darizotas.metadatastrip.metadata.GroupContainer;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;

/**
 * This class extracts metadata from images.
 * To parse the metadata uses {@link http://drewnoakes.com/code/exif metadata-extractor} library.
 * @author dario
 */
public class ImageMetaDataExtractor extends AbstractMetaDataExtractor {
	@Override
	public void extract(File file) throws IOException, MetadataProcessingException {
		mContainer = new GroupContainer();
		
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(file);
			for (Directory directory : metadata.getDirectories()) {
				// Fills in the metadata structure.
				int group = mContainer.addGroup(directory.getName());
			    for (Tag tag : directory.getTags()) {
			    	mContainer.addMetadataInGroup(group, tag.getTagName(), tag.getDescription());
			    }
			    // Gets the GPS location.
			    if (directory.getClass() == GpsDirectory.class) {
					mLocation = new Location(file.getName());

					GeoLocation location = ((GpsDirectory) directory).getGeoLocation();
				    mLocation.setLatitude(location.getLatitude());
				    mLocation.setLongitude(location.getLongitude());
				    // Let's retrieve the time stamp as well.
				    if (directory.containsTag(GpsDirectory.TAG_GPS_TIME_STAMP)) {
					    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss z", Locale.getDefault());
					    try {
						    Date tStamp = df.parse(
						    	directory.getDescription(GpsDirectory.TAG_GPS_TIME_STAMP));
					    	mLocation.setTime(tStamp.getTime());
					    } catch (ParseException e) {}
				    }
			    }
			}
		} catch (ImageProcessingException ep) {
			throw new MetadataProcessingException(ep.getMessage(), ep.getCause());
		}
	}

}
