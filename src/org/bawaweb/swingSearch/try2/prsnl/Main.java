package org.bawaweb.swingSearch.try2.prsnl;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// https://java-creed-examples.googlecode.com/svn/swing/Swing%20Worker%20Example/src/main/java/com/javacreed/examples/swing/worker/part3/Main.java
public class Main {

	public static void main(final String[] args) {
	    SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
//	        final Application frame = new Application();
	        final MyApplication frame = new MyApplication();
	        frame.setTitle("File Name SearcherDemo");
	        frame.setSize(700, 600);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setResizable(false);
	        frame.setVisible(true);
	      }
	    });
	  }

}
