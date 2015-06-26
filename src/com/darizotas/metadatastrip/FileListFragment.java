/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.darizotas.metadatastrip.file.FileManager;

/**
 * A list fragment representing a list of Files. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link FileDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class FileListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current path.
	 */
	private static final String STATE_CURRENT_PATH = "current_path";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	/**
	 * List of contents of the current directory.
	 */
	private List<HashMap<String, String>> mDirContents = new ArrayList<HashMap<String, String>>();
	/**
	 * Current path.
	 */
	private String mPath;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String path);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String path) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FileListFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		//http://developer.android.com/guide/components/fragments.html
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//http://developer.android.com/reference/android/app/ListFragment.html
		// Let's instantiate my customized view.
		return inflater.inflate(R.layout.fragment_file_list, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		//http://developer.android.com/reference/android/app/Fragment.html
		//http://stackoverflow.com/questions/8041206/android-fragment-oncreateview-vs-onactivitycreated
        super.onActivityCreated(savedInstanceState);

        // Retrieves the current path.
        mPath = (savedInstanceState != null && savedInstanceState.containsKey(STATE_CURRENT_PATH))? 
        		savedInstanceState.getString(STATE_CURRENT_PATH) : "/";
		updateListAdapter(mPath);
	}	
	
	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Retrieves the content of the current folder
		HashMap<String, String> selected = mDirContents.get(position);
		mCallbacks.onItemSelected(selected.get(FileManager.KEY_PATH));			
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
		// Serialize and persist the current path
		outState.putString(STATE_CURRENT_PATH, mPath);
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	   inflater.inflate(R.menu.list_menu, menu);
	}	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.folder_back:
	    		File f = new File(mPath);
	    		if (f.getParent() != null)
	    			mCallbacks.onItemSelected(f.getParent());

	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Updates the current Fragment with the contents of the given path.
	 * @param path Current path.
	 */
	public void updateListAdapter(String path) {
		// Updates the current folder
		mPath = path;
		TextView currentPath = (TextView) getView().findViewById(R.id.current_path);
		currentPath.setText(path);
		
		// Updates the list
		mDirContents = FileManager.getDirContents(path);
		String[] from = { FileManager.KEY_ICON, FileManager.KEY_FILENAME };
		int[] to = { R.id.file_icon, R.id.file_name };
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), mDirContents, 
				R.layout.fragment_file_row, from, to);
		
		setListAdapter(adapter);
	}
	
}
