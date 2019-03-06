package org.bawaweb.swingSearch.try2.prsnl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author Navroz
 * adapted from
 * https://examples.javacodegeeks.com/core-java/util/search-files-in-a-directory-using-futuretask-example/
 */

public class MatchCounter implements Callable<List<File>> {

	private File 			dir;		//	dir the directory in which to start the search keyword the keyword to look for
	private String 			keyword;
	private int 			counter;
	private List<File> 		foundTextFiles 		= new ArrayList<File>();
	private String 			fileList = "";

	public MatchCounter(File directory, String keyword) {
		this.dir = directory;
		this.keyword = keyword;
	}

	@Override
	public List<File> call() {
		counter = 0;
		foundTextFiles 		= new ArrayList<File>();
		try {
			File[] files = dir.listFiles();
			ArrayList<Future<List<File>>> 	resultFiles = new ArrayList<Future<List<File>>>();
			process(files, resultFiles);			
			addResults(resultFiles);
		} catch (InterruptedException e) {}

		return foundTextFiles;
	}
	

	private void addResults(ArrayList<Future<List<File>>> resultFiles) throws InterruptedException {
		for (Future<List<File>> result : resultFiles) {
			try {
				foundTextFiles.addAll(result.get());
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	

	private void process(File[] files, ArrayList<Future<List<File>>> resultFiles) {
		for (File file : files) {

			if (file.isDirectory()) {
				MatchCounter counter = new MatchCounter(file, keyword);
				FutureTask<List<File>> filesTask = new FutureTask<	List<File>>(counter);
				resultFiles.add(filesTask);
				Thread t = new Thread(filesTask);
				t.start();

			} else {
				if (search(file)) {
					counter++;
					foundTextFiles.add(file);
					System.out.println("Added--filesFoundInCall--file is "+file.getAbsolutePath());
					fileList+=fileList+"\n"+file.getAbsolutePath();

				}
			}

		}
	}

	/**
	 * Searches a file for a given keyword.
	 *
	 * file -- the file to search returns true if the keyword is contained in the file
	 */
	public boolean search(File file) {
		try {
			Scanner in = new Scanner(new FileInputStream(file));
			boolean found = false;
			
			while (!found && in.hasNextLine()) {
				String line = in.nextLine();
				if (line.toLowerCase().contains(keyword.toLowerCase())) {
					found = true;
					break;
				}
			}

			in.close();
			return found;

		} catch (IOException e) {
			return false;
		}
	}

	public List<File> getFoundTextFiles() {
		return foundTextFiles;
	}

}
