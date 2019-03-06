package org.bawaweb.swingSearch.try2.prsnl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class MySearchForWordWorker extends SwingWorker<String, String> {

	private static void failIfInterrupted() throws InterruptedException {
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException("Interrupted while searching files");
		}
	}

	/** The word that is searched */
	private final String word;
	private final String DELIMITER = ";";
	
	private final List<String> extension;

	/**
	 * The directories under which the search occurs. All text files found under
	 * the given directories are searched recursively -- this is semi-colon delimited
	 */
	private final String directories;

	/** The text area where messages are written. */
	private final JTextArea messagesTextArea;

	private JavaSearchFile fileSearcher = new JavaSearchFile();
	
	private JProgressBar searchProgressBar;
	
	private boolean searchFileText = false;


	/**
	 * Creates an instance of the worker
	 * 
	 * @param wrd
	 *            The word to search
	 * @param dirs
	 *            the directory under which the search will occur. All text
	 *            files found under the given directory are searched
	 * @param searchText
	 * 				search text within file for wrd      
	 * @param mssgTxtArea
	 *            The text area where messages are written
	 */
	
	public MySearchForWordWorker(final String wrd, final String dirs, List<String> ext, final boolean searchText, final JTextArea mssgTxtArea) {
		
		System.out.println("in ctor MySearchForWordWorker22222");
		this.word = wrd;
		this.directories = dirs;
		this.messagesTextArea = mssgTxtArea;
		/*for(String e : ext) {
			System.out.println(e);
		}*/
		this.extension = ext;
		this.searchFileText = searchText;
		System.out.println("MySearchForWordWorker.searchFileText is "+this.searchFileText);
	}
	

	/**
	 * Creates an instance of the worker
	 * 
	 * @param wrd
	 *            The word to search
	 * @param dirs
	 *            the directory under which the search will occur. All text
	 *            files found under the given directory are searched
	 * @param mssgTxtArea
	 *            The text area where messages are written
	 */
	
	public MySearchForWordWorker(final String wrd, final String dirs, List<String> ext, final JTextArea mssgTxtArea) {
		this(wrd,dirs,ext,false,mssgTxtArea);
	}

	@Override
	protected String doInBackground() throws Exception {
		System.out.println("HERE    HERE    HERE 22222  directories is " + directories);
		String[] theDirPaths = directories.split(DELIMITER);
		StringBuilder result = new StringBuilder();
		for (String aDirPath : theDirPaths ) {
			System.out.println("aDirPath is " + aDirPath);			
			File dir = new File(aDirPath);

			if (dir.exists()) {
				String ss = fileSearcher.searchForFile(aDirPath, word, extension, searchFileText);
				System.out.println(ss);
				result.append(ss);
				MySearchForWordWorker.failIfInterrupted();
				setProgress(1);//(100 / size);
				publish(ss);
				return ss;
			}
		}
	
		return result.toString();
	}

	@Override
	protected void process(final List<String> chunks) {
		System.out.println("HERE    HERE    HERE 111111");
		// Updates the messages text area
	    for (final String string : chunks) {
	      messagesTextArea.append(string);
	      messagesTextArea.append("\n");
	    }
	  }
	

}
