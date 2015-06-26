/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip.metadata;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlSerializer;

import com.darizotas.metadatastrip.file.Utils;

import android.location.Location;
import android.util.Base64;
import android.util.Xml;

/**
 * Class that contains the extracted metadata, providing easy access to that metadata.
 * @author darizotas
 *
 */
public class MetaDataContainer {
	/**
	 * Contains all extracted metadata from the file.
	 */
	private GroupContainer mContainer;
	/**
	 * Contains the geo location of the file.
	 */
	private Location mLocation;
	/**
	 * Path to the file.
	 */
	private String mFileName;
	/**
	 * Hash value.
	 */
	private String mHash;

	/**
	 * Creates the metadata container.
	 * @param file File to which the metadata belongs to.
	 * @param data Metadata associated to the given file.
	 * @param location Geo-location.
	 */
	public MetaDataContainer(File file, GroupContainer data, Location location) {
		mContainer = data;
		mLocation = location;
		mFileName = file.getName();
		mHash = Utils.getSignature(file, "SHA1");
	}
	
	/**
	 * Returns the file name containing the metadata.
	 * @return File name.
	 */
	public String getFileName() {
		return mFileName;
	}
	
	/**
	 * Returns the metadata read.
	 * @return Metadata.
	 */
	public GroupContainer getMetadata() {
		return mContainer;
	}
	
	/**
	 * Returns the GPS location.
	 * @return GPS location.
	 */
	public Location getLocation() {
		return mLocation;
	}
	
	/**
	 * Returns the metadata read in XML format so that it is easily sharable.
	 * @return Metadata read in XML format.
	 */
	public String toXmlString() {
		XmlSerializer xml = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		// Pattern to match no printable chars.
		Pattern p = Pattern.compile("\\p{Cntrl}");
		
		try {
			xml.setOutput(writer);
			// Start Document
		    xml.startDocument("UTF-8", true); 
		    xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			
		    xml.startTag("", "metadatastrip");
		    addSignatureToXmlString(xml);
		    // For each group.
		    int groupIndex = 0;
		    for (HashMap<String, String> group : mContainer.getGroups()) {
		    	String groupTag = group.get(GroupContainer.KEY_GROUP_TEXT);
		    	xml.startTag("", groupTag);
		    	
		    	// Loop through all the tags.
		    	for (HashMap<String, String> tag : mContainer.getMetadata(groupIndex++)) {
		    		String tagName = tag.get(GroupContainer.KEY_TAG_NAME);
		    		xml.startTag("", tagName);
		    		
		    		String tagValue = tag.get(GroupContainer.KEY_TAG_VALUE);
		    		// Checks whether there are no printable chars.
		    		Matcher m = p.matcher(tagValue);
		    		if (!m.find())
		    			xml.text(tagValue);
		    		else
		    			xml.text(Base64.encodeToString(tagValue.getBytes(), Base64.DEFAULT));
		    		
		    		xml.endTag("", tagName);
		    	}
		    	
		    	xml.endTag("", groupTag);
		    }
		    
		    xml.endTag("", "metadatastrip");
		    xml.endDocument();
			
			return writer.toString();
		} catch (IllegalArgumentException e) {
			return "";
		} catch (IllegalStateException e) {
			return "";
		} catch (IOException e) {
			return "";
		}
	}
	
	/**
	 * Adds the signature section: file name, hash, date/time.
	 * @param xml Xml serializer.
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws IllegalArgumentException 
	 */
	private void addSignatureToXmlString(XmlSerializer xml) throws IllegalArgumentException, 
		IllegalStateException, IOException {
		
		xml.startTag("", "signature");
		
		xml.startTag("", "filename");
		xml.text(mFileName);
		xml.endTag("", "filename");
		
		xml.startTag("", "hash");
		xml.text(mHash);
		xml.endTag("", "hash");
		
		xml.startTag("", "timestamp");
		xml.text(DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
		xml.endTag("", "timestamp");
		
		xml.endTag("", "signature");
	}
}
