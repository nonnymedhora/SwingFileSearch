package org.bawaweb.swingSearch.try2.prsnl;


import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang.StringUtils;


/**
 * @author Navroz
 * adapted from
 * http://java-demos.blogspot.in/2012/12/search-file-in-java-recursively.html
 */

public class JavaSearchFile {

	private Vector<File> v;
	
	
	public String searchForFile(String dir2Search, String fileName, List<String> extension, boolean srchText) {
		
		v = new Vector<File>();		
		
		// Create a file pointing a folder
		File dir2SearchDir = new File(dir2Search);		

		System.out.println("Search for the file " + fileName + " within the directory " + dir2Search + "\n-------------------------\n");
		
		if(srchText) {
			searchFileText(dir2SearchDir, fileName, extension);
			
		} else {

			// Go search args[1] (2nd arg) in dir f
			search(dir2SearchDir, fileName, extension);//args[1]);
		}
				
		// Print the found files
		return print();
	}
	
	private String searchFileText(File dir2SearchDir, String keyword, List<String> extension) {
		v = new Vector<File>();
		System.out.println("InsearchFileText------JavaSearchFile");

		MatchCounter countFiles = new MatchCounter(dir2SearchDir, keyword);

		FutureTask<List<File>> tsk = new FutureTask<List<File>>(countFiles);

		Thread thread = new Thread(tsk);
		thread.start();
		try {

			System.out.println("*****\n"+tsk.get() + "\n matching files.");
			for(File f : countFiles.getFoundTextFiles() ) {
				v.addElement(f);
			}
			
			
			
			
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		}

		
		// Print the found files
		return print();
	}
	

	public  void search(File file, String name, List<String> ext) {
		// Print where the search is going on..
		System.out.println("Searching in " + file.getAbsolutePath());

		// Check if file is directory/folder
		if (file.isDirectory()) {
			if (file.getName().contains(name)) {
				// Add the dir containig the name to found files vector
				v.addElement(file);
				//YUP---that-what-it-did
			}

			// Get all files in the folder
			File[] files = file.listFiles();//( nameFilter(file.getParentFile(), name) );

			for (int i = 0; i < files.length; i++) {
				try {
					if (files[i].isDirectory()) {
						// Go search for files if dir
						search(files[i], name, ext);
					} else {
						if (files[i].getName().toLowerCase()
								.contains(name.toLowerCase())  && fileNameEndsWith(files[i].getName(),ext)) {
							// Add the found file to vector
							v.addElement(files[i]);
						}
					}
				} catch (Exception e) { }

			}

		} else {
			System.out.println(file.getAbsolutePath() + " is not a directory");
		}

	}

	
	private boolean fileNameEndsWith(String name, List<String> ext) {		
		boolean srchAll = ext.contains("ALL");
		if(srchAll) {
			return true;
		}
		for (String e : ext) {
			if ( StringUtils.endsWithIgnoreCase(name, e) ) 
				return true;
		}
		
		return false;
	}

	public  String  print() {
		// Create a file array of v size
		File[] f = new File[v.size()];
		StringBuffer sb = new StringBuffer();
		// Copy vector data into f
		v.copyInto(f);
		
		// Loop till end of size
		for (File k : f) {
			// Print the file path
			sb.append(k.getAbsolutePath() + System.lineSeparator());
		}
		
		return sb.toString();
	}

}