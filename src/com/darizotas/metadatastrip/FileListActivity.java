/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip;

import java.io.File;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * An activity representing a list of Files. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link FileDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link FileListFragment} and the item details (if present) is a
 * {@link FileDetailFragment}.
 * <p>
 * This activity also implements the required {@link FileListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class FileListActivity extends FragmentActivity implements
		FileListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);

		if (findViewById(R.id.file_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((FileListFragment) getSupportFragmentManager().findFragmentById(
					R.id.file_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}
	
	/**
	 * Callback method from {@link FileListFragment.Callbacks} indicating that
	 * the item with the given PATH was selected.
	 */
	@Override
	public void onItemSelected(String path) {
		File file = new File(path);
		if (file.canRead()) {
			if (file.isDirectory()) {
				FragmentManager manager = getSupportFragmentManager();
				
				if (mTwoPane) {
					// Removes the previous detail fragment
					Fragment fragment = manager.findFragmentById(R.id.file_detail_container);
					if (fragment != null) {
						manager.beginTransaction().remove(fragment).commit();
						//http://stackoverflow.com/questions/7246479/android-fragmenttransaction-commit-when
						manager.executePendingTransactions();
					}
				}
				// Updates the list.
				((FileListFragment) manager.findFragmentById(
						R.id.file_list)).updateListAdapter(file.getPath());

			} else {
				showDetails(path);
			}
		// The folder cannot be read.
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.ic_launcher)
				.setTitle("[" + file.getName() + "] " + 
					getResources().getText(R.string.error_open_file))
				.setPositiveButton("OK", null)
				.show();	
		}	
	}
	
	/**
	 * Shows the {@link FileDetailFragment file metadata details fragment} of the given file.
	 * @param path Path to file.
	 */
	private void showDetails(String path) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(FileDetailFragment.ARG_ITEM_ID, path);
			FileDetailFragment fragment = new FileDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.file_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, FileDetailActivity.class);
			detailIntent.putExtra(FileDetailFragment.ARG_ITEM_ID, path);
			startActivity(detailIntent);
		}
	}
}
