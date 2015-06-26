/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */package com.darizotas.metadatastrip;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This class represents the dialog with the About information.
 */
public class AboutDialogFragment extends DialogFragment {
	/**
	 * Software licenses.
	 */
	private enum License {
		LIC_METADATASTRIP, LIC_METADATAEXTRACTOR, LIC_HOPSTARTER
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	}
	
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
	    //Set title for this dialog
        getDialog().setTitle(getResources().getString(R.string.app_name) + " v1.0");
        //Set the licenses
	    View v = inflater.inflate(R.layout.fragment_about_dialog, container, false);
        TextView tv = (TextView) v.findViewById(R.id.textAbout);
        //http://stackoverflow.com/questions/1748977/making-textview-scrollable-in-android
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        tv.setText(getLicense(License.LIC_METADATASTRIP) +
        	"\n\n*** Third party license agreements ***\n\n" +
        	getLicense(License.LIC_METADATAEXTRACTOR) +
        	"\n\n*** ***\n\n" +
        	getLicense(License.LIC_HOPSTARTER)
        );

        return v;
    }
   
   private static String getLicense(License license) {
	   switch (license) {
	   case LIC_METADATASTRIP:
		   return "Copyright 2014 Dario B\n" +
		    	"All rights reserved.\n\n" +
		    	"This software is licensed under a new BSD License : http://opensource.org/licenses/BSD-3-Clause\n" +
		    	"Redistribution and use in source and binary forms, with or without modification, are permitted " +
		    	"provided that the following conditions are met:\n" +
		    	"1. Redistributions of source code must retain the above copyright notice, this list of conditions " +
		    	"and the following disclaimer.\n" +
		    	"2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions " +
		    	"and the following disclaimer in the documentation and/or other materials provided with the distribution.\n" +
		    	"3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse " +
		    	"or promote products derived from this software without specific prior written permission.\n\n" +
		    	"THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ANY EXPRESS " +
		    	"OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY " +
		    	"AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR " +
		    	"CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL " +
		    	"DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, " +
		    	"DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER " +
		    	"IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT " +
		    	"OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n\n" +
		    	"More information at:\n" +
		    	"http://darizotas.blogspot.com\n" +
		    	"http://code.google.com/p/darizotas";	

	   case LIC_METADATAEXTRACTOR:
		   return "This software makes use of the metadata-extractor v2.6.4 library, licensed as\n" +
	        	"Copyright 2002-2012 Drew Noakes\n" +
	        	"Licensed under the Apache License, Version 2.0 (the \"License\"); " +
	        	"you may not use this file except in compliance with the License.\n" +
	            "You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0\n\n" +
	            "Unless required by applicable law or agreed to in writing, software " +
	            "distributed under the License is distributed on an \"AS IS\" BASIS, " +
	            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. " +
	            "See the License for the specific language governing permissions and " +
	            "limitations under the License.\n\n" +
	            "More information about this project is available at:\n" +
	            "http://drewnoakes.com/code/exif/\n" +
	            "http://code.google.com/p/metadata-extractor/";

	   case LIC_HOPSTARTER:
		   return "This software makes use of icons provided by Iconarchive, licensed as\n" +
	        	"Creative Commons (Attribution-Noncommercial-No Derivate 3.0)\n" +
	        	"Artist: http://hopstarter.deviantart.com";
	   }
	   return "";
   }
	
 }
