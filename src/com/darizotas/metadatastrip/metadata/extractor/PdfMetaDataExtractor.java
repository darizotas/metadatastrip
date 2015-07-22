package com.darizotas.metadatastrip.metadata.extractor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.darizotas.metadatastrip.metadata.GroupContainer;
import com.itextpdf.text.pdf.PdfReader;

public class PdfMetaDataExtractor extends AbstractMetaDataExtractor {
	
	private static final HashMap<String, String> XMP_SCHEMA = new HashMap<String, String>();
	static {
		XMP_SCHEMA.put("dc", "Dublin Core");
		XMP_SCHEMA.put("xmp", "XMP Basic");
		XMP_SCHEMA.put("xmpRights", "XMP Rights Management");
		XMP_SCHEMA.put("xmpMM", "XMP Media Management");
		XMP_SCHEMA.put("xmpBJ", "XMP Basic Job Ticket");
		XMP_SCHEMA.put("xmpTPg", "XMP Paged-Text");
		XMP_SCHEMA.put("xmpDM", "XMP Dynamic Media");
		XMP_SCHEMA.put("pdf", "Adobe PDF");
		XMP_SCHEMA.put("photoshop", "Adobe Photoshop");
		XMP_SCHEMA.put("crs", "Camera Raw");
		XMP_SCHEMA.put("tiff", "EXIF TIFF Properties");
		XMP_SCHEMA.put("exif", "EXIF Properties");
		XMP_SCHEMA.put("aux", "EXIF Additional Properties");
	};
	
	private HashMap<String, Integer> mGroup;
	
	public PdfMetaDataExtractor() {
		mGroup = new HashMap<String, Integer>();
	}

	@Override
	public void extract(File file) throws IOException,
			MetadataProcessingException {
		mContainer = new GroupContainer();
		mGroup.clear();
		
		PdfReader reader = new PdfReader(new BufferedInputStream(new FileInputStream(file)));
		byte[] metadata = reader.getMetadata();

		XmlPullParser parser = Xml.newPullParser();
        try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
	        parser.setInput(new ByteArrayInputStream(metadata), null);
	        //http://partners.adobe.com/public/developer/en/xmp/sdk/XMPspecification.pdf
	        while (parser.nextTag() == XmlPullParser.START_TAG) {
//	        while (parser.next() != XmlPullParser.END_DOCUMENT) {
	        	//Skip anything it is not an starting tag.
//	        	if (parser.getEventType() != XmlPullParser.START_TAG)
//	        		continue;
	        	
	        	// TODO Check that is top-level descriptions.
	        	String name = parser.getName();
	        	if (name.equalsIgnoreCase("Description")) {
	        		//parser.require(XmlPullParser.START_TAG, "rdf", "Description");

	        		readDescriptionAttributes(parser);
	        		readDescriptionContents(parser);

	        		//parser.require(XmlPullParser.END_TAG, "rdf", "Description");
	        	}
	        }
		} catch (XmlPullParserException e) {
			throw new MetadataProcessingException(e.getMessage(), e.getCause());
		}

	}

	/**
	 * Parses the attributes of a rdf:Description node and generates a metadata group per each namespace
	 * defined and includes the metadata defined in the rest of the attributes that belong to the
	 * defined namespaces.
	 * @param parser XMP XML parser
	 * @throws XmlPullParserException
	 */
	private void readDescriptionAttributes(XmlPullParser parser) throws XmlPullParserException {
		// Registers all the defined schemas for the description node as metadata groups.
		int start = parser.getNamespaceCount(parser.getDepth()-1);
	    int end = parser.getNamespaceCount(parser.getDepth());
	    for (int i = start; i < end; i++) {
	    	addGroup(parser.getNamespacePrefix(i));
	      }
		
		// Adds metadata for those registered groups.
		int count = parser.getAttributeCount();
		for (int i = 0; i < count; i++) {
			addMetadataInGroup(parser.getAttributePrefix(i), parser.getAttributeName(i), 
				parser.getAttributeValue(i));
		}
	}
	
	/**
	 * Adds a new group to the metadata from the given schema/namespace.
	 * @param schema Namespace that represents a metadata group.
	 */
	private void addGroup(String schema) {
		if (!mGroup.containsKey(schema)) {
			int index = mContainer.addGroup(schema); 
			mGroup.put(schema, index);
		}	
	}
	
	// It only adds the metadata to an existing group.
	/**
	 * Adds new metadata (tag, value) within the given 'schema/namespace' metadata group.
	 * If the given schema/namespace (=group) does not exist, then the metadata (tag, value) will not
	 * be added.
	 * @param schema Metadata group.
	 * @param tag Metadata tag.
	 * @param value Metadata value.
	 * @return True whether the metadata given was added to the given group. False in the contrary case.
	 */
	private boolean addMetadataInGroup(String schema, String tag, String value) {
		if (mGroup.containsKey(schema)) {
			int group = mGroup.get(schema);
			mContainer.addMetadataInGroup(group, tag, value);
			
			return true;
		} else
			return false;
	}
	
	// TODO Return some value if empty????????????????????????????????????????????????????
	private void readDescriptionContents(XmlPullParser parser) throws XmlPullParserException, IOException {
//		while (parser.next() != XmlPullParser.END_TAG) {
//			// There is nothing to read: empty description tag.
//			if (parser.getEventType() != XmlPullParser.START_TAG)
//				continue;
		while (parser.nextTag() == XmlPullParser.START_TAG) {

			//Gets the tag and group names from, node name and namespace respectively.
			String tag = parser.getName();
			String schema = parser.getPrefix();
			readProperty(parser, schema, tag);
//			if (i > 0) {
//				schema = tag.substring(0, i);
//				tag = tag.substring(i+1);
//				if (mGroup.containsKey(schema)) {
//					readProperty(parser, schema, tag);
//				}
//			}
		}
		
	}

	// TODO Return some value if empty????????????????????????????????????????????????????
	private void readProperty(XmlPullParser parser, String schema, String tag) throws XmlPullParserException, 
		IOException {
		
		String text = "";
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() == XmlPullParser.TEXT) {
				text += parser.getText().trim();
			} else if (parser.getEventType() == XmlPullParser.START_TAG) {
				String name = parser.getName();
				// Array
//				switch (parser.getName()) {
//				case "Bag":
//				case "Seq":
//				case "Alt":
//					// TODO readd array
//					break;
//			
//				}
				// TODO need to flatten the structure to make it more readable.
				// Needs to be recursive.
			}
		}
	}
}
