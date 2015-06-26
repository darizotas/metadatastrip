/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import com.darizotas.metadatastrip.metadata.GroupContainer;
import com.darizotas.metadatastrip.metadata.MetaDataContainer;
import com.darizotas.metadatastrip.metadata.extractor.MetaDataExtractorProxy;
import com.darizotas.metadatastrip.metadata.extractor.MetadataProcessingException;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ShareActionProvider;
import android.widget.SimpleExpandableListAdapter;

/**
 * A fragment representing a single File detail screen. This fragment is either
 * contained in a {@link FileListActivity} in two-pane mode (on tablets) or a
 * {@link FileDetailActivity} on handsets.
 */
public class FileDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The metadata associated to the file that this fragment is presenting.
	 */
	private MetaDataContainer mFileMetadata = null;
	/**
	 * File with the extracted metadata.
	 */
	private File mStripFile = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FileDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializes the metadata from the selected file.
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			File file = new File(getArguments().getString(ARG_ITEM_ID));
			
			MetaDataExtractorProxy proxy = MetaDataExtractorProxy.getInstance();
			mFileMetadata = null;
			try {
				mFileMetadata = proxy.extract(file);
				
			// Error opening the file.
			} catch (IOException e) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setIcon(R.drawable.ic_launcher)
					.setTitle("[" + file.getName() + "] " + 
						getResources().getText(R.string.error_open_file))
					.setPositiveButton("OK", null)
					.show();
			// Error processing metadata.
			} catch (MetadataProcessingException e) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setIcon(R.drawable.ic_launcher)
					.setTitle("[" + file.getName() + "] " + 
						getResources().getText(R.string.error_process_file))
					.setMessage(e.getMessage())
					.setPositiveButton("OK", null)
					.show();	
			}

			setHasOptionsMenu(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detail_list,
				container, false);
		
		// Update the file details while creating the view.
		if (mFileMetadata != null) {
			updateFileDetails(rootView);
		}
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	   inflater.inflate(R.menu.detail_menu, menu);

	    // Gets the share action provider and initializes it.
	    // http://developer.android.com/training/sharing/shareaction.html
	    MenuItem item = menu.findItem(R.id.share);
	    ShareActionProvider provider = (ShareActionProvider)item.getActionProvider();
	    if (provider != null && mFileMetadata != null) {
	        provider.setShareIntent(getShareIntent(mFileMetadata));
	    }
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
	    MenuItem item = menu.findItem(R.id.location);
	    Boolean enabled = getUriGeoLocation() != null;
		item.setVisible(enabled);
		item.setEnabled(enabled);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.location:
				Uri geoLocation = getUriGeoLocation();
	
				if (geoLocation != null) {
				    Intent intent = new Intent(Intent.ACTION_VIEW);
				    intent.setData(geoLocation);
				    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
				        startActivity(intent);
				    }
				}
			break;
			
			case R.id.about:
			    AboutDialogFragment aboutDialog = new AboutDialogFragment();
			    aboutDialog.show(getFragmentManager(), "fragment_about_dialog");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		if (mStripFile != null && mStripFile.exists()) {
			mStripFile.delete();
		}
	}
	
	/**
	 * Creates the Share Intent that contains the 
	 * @param container Metadata container.
	 * @return Intent for sharing.
	 */
	private Intent getShareIntent(MetaDataContainer container) {
	    Intent intent = new Intent(Intent.ACTION_SEND);

	    intent.putExtra(Intent.EXTRA_SUBJECT, "[" + getResources().getString(R.string.app_name) + "] " +
	    		getResources().getString(R.string.share_subject) + " " + container.getFileName());
	    intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text) + " " + 
	    	DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
	    
		try {
		    // http://stackoverflow.com/questions/3272534/what-content-type-value-should-i-send-for-my-xml-sitemap
		    // http://www.grauw.nl/blog/entry/489
		    intent.setType("application/xml");
		    //https://developer.android.com/training/sharing/send.html#send-binary-content
		    //https://developer.android.com/reference/android/support/v4/content/FileProvider.html
		    mStripFile = getMetadataFile(container);
		    Uri uri = FileProvider.getUriForFile(getActivity(), "com.darizotas.metadatastrip", mStripFile);
		    intent.putExtra(Intent.EXTRA_STREAM, uri);
			
		} catch (IllegalArgumentException e) {
			// Invalidates the intent.
		    intent.setType(null);
		}
		return intent;
	}

	/**
	 * Returns the file that contains the extracted metadata.
	 * @param container Metadata container.
	 * @return File File with the extracted metadata.
	 */
	private File getMetadataFile(MetaDataContainer container) {
		try {
			//Though External SD Card is preferred, internal files dir must be used.
			//http://stackoverflow.com/questions/20455035/illegalargumentexception-faild-to-find-configuration-root-that-contains-xxx-on
			//https://code.google.com/p/android/issues/detail?id=61170
			File path = new File(getActivity().getFilesDir(), "striped");
			//Let's create the path if it does not exist.
			if (path.mkdirs() || path.isDirectory()) {
				File file = new File(path, container.getFileName() + "_metadatastrip.xml");
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(container.toXmlString());
				out.close();
				
				return file;
			} else
				return null;
			
		} catch (IOException e) {
			return null;
		}
	}
	
	/** 
	 * Updates the view with the metadata contained in the selected file that this fragment
	 * is presenting.
	 * @param view View
	 */
	private void updateFileDetails(View view) {
		GroupContainer container = mFileMetadata.getMetadata();
		
		SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(getActivity(), 
			// List of groups
			container.getGroups(),
			R.layout.fragment_detail_rowgroup,
			new String[] {GroupContainer.KEY_GROUP_TEXT},
			new int[] { R.id.group_name },
			// List of metadata, per group.
			container.getAllMetadata(),
			R.layout.fragment_detail_rowtag,
			new String[] { GroupContainer.KEY_TAG_NAME, GroupContainer.KEY_TAG_VALUE },
			new int[] { R.id.tag_name, R.id.tag_value });
		
		ExpandableListView details = (ExpandableListView)view.findViewById(R.id.detail_list);
		details.setAdapter(adapter);
	}
	
	/**
	 * Returns the URI containing the geo location of the selected file.
	 * @return Uri "geo:0,0?q=lat,long(label)".
	 */
	private Uri getUriGeoLocation() {
		if (mFileMetadata == null)
			return null;
		
		Location location = mFileMetadata.getLocation();
		if (location != null) {
			return Uri.parse("geo:0,0?q=" + location.getLatitude() + "," + location.getLongitude() + 
				" (" + location.getProvider() + ")");
		} else
			return null;
		
	}
}
