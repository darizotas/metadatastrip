/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the metadata container.
 * The structure of this container complies with the structure required by 
 * an {@link android.widget.ExpandableListView}. 
 * @author darizotas
 */
public class GroupContainer {
	/** Group text hash key. */
	public static final String KEY_GROUP_TEXT = "group_text";
	/** Tag name hash key. */
	public static final String KEY_TAG_NAME = "tag_name";
	/** Tag value hash key. */
	public static final String KEY_TAG_VALUE = "tag_value";

	/** List of groups of metadata. */
	private List<HashMap<String, String>> groups;
	/** List of metadata tags within a group. */
	private List<List<HashMap<String, String>>> tags;
	
	/** 
	 * Creates the container. 
	 */
	public GroupContainer() {
		groups = new ArrayList<HashMap<String, String>>();
		tags = new ArrayList<List<HashMap<String, String>>>();
	}
	
	/** 
	 * Adds a new group of metadata with the given description.
	 * This description will be mapped to {@link KEY_GROUP_TEXT}.
	 * @param description Group description.
	 * @return Group index.
	 */
	public int addGroup(String description) {
		HashMap<String, String> group = new HashMap<String, String>();
		group.put(KEY_GROUP_TEXT, description);
		groups.add(group);
		// New group implies to create a new placeholder for the metadata belonging to that group.
		tags.add(new ArrayList<HashMap<String, String>>());
		
		return groups.size()-1;
	}
	
	/** Adds metadata tag within a given group.
	 * @param group Group index to which the metadata will be added.
	 * @param tag Metadata tag name.
	 * @param value Metadata tag value.
	 * @return True whether the metadata was successfully added. 
	 */
	public boolean addMetadataInGroup(int group, String tag, String value) {
		try {
			List<HashMap<String, String>> g = tags.get(group);
			// Creates the metadata.
			HashMap<String, String> metadata = new HashMap<String, String>();
			metadata.put(KEY_TAG_NAME, tag);
			metadata.put(KEY_TAG_VALUE, value);
			
			g.add(metadata);
			
			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	
	/** Returns all groups.
	 * @return List of groups.
	 */
	public List<HashMap<String, String>> getGroups() {
		return groups;
	}

	/** Returns all metadata grouped in each created group.
	 * @return List of groups.
	 */
	public List<List<HashMap<String, String>>> getAllMetadata() {
		return tags;
	}
	
	/** Returns all metadata related to the given group.
	 * @param group Group index.
	 * @return Metadata related to the given group.
	 */
	public List<HashMap<String, String>> getMetadata(int group) {
		try {
			return tags.get(group);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
