/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.darizotas.metadatastrip.R;

/**
 * This class represents the contents of a directory.
 * The structure of this container complies with the structure required by 
 * an {@link android.widget.SimpleAdapter}. 
 * @author darizotas
 */
public class FileManager {
	/** Icon hash key. */
	public static final String KEY_ICON = "icon";
	/** Filename hash key. */
	public static final String KEY_FILENAME = "filename";
	/** Path hash key. */
	public static final String KEY_PATH = "path";
	
	/**
	 * Returns the contents of the given folder according the format required by 
	 * an {@link android.widget.SimpleAdapter}.
	 * @param startPath Root folder to loop.
	 * @return The contents of the given folder.
	 */
	public static List<HashMap<String, String>> getDirContents(String startPath) {
    	File path = new File(startPath);
    	File[] pathContents = path.listFiles();
    	
        //http://stackoverflow.com/questions/18628831/listview-with-arraylist-simple-adapter-in-android
		//http://www.vogella.com/tutorials/AndroidListView/article.html
		//http://developer.android.com/reference/android/widget/SimpleAdapter.html
		//http://prakashgavade.blogspot.nl/2013/05/android-listview-with-imageview-using.html
    	List<HashMap<String, String>> files = new ArrayList<HashMap<String, String>>();
    	for (int i = 0; i < pathContents.length; i++) {
    		File file = pathContents[i];

    		if (file.canRead()) {
    			HashMap<String, String> data = new HashMap<String, String>();
    			
    			data.put(FileManager.KEY_FILENAME, file.getName());
    			data.put(FileManager.KEY_PATH, file.getPath());
    			if (file.isDirectory()) {
        			data.put(FileManager.KEY_ICON, Integer.toString(R.drawable.ic_action_collection));
    			} else {
        			data.put(FileManager.KEY_ICON, Integer.toString(R.drawable.ic_action_search));
    			}
    			files.add(data);
    		}	
    	}
    	
    	return files;
	}
}
