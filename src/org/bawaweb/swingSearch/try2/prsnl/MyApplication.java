package org.bawaweb.swingSearch.try2.prsnl;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker.StateValue;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultHighlighter;

import org.apache.commons.lang.StringUtils;

public class MyApplication extends JFrame {

	private static final long serialVersionUID = 7376683093257718992L;
	private Action 					searchCancelAction;
	private Action 					browseAction;
	private Action 					copyAction;
	private Action 					clearResultsAction;
	private Action 					clearDirsAction;
	private Action 					playListAction;
	

	private JTextField 				wordTextField;
	private JTextField 				directoryPathTextField;
	private JCheckBox 				searchTextCheckBox;


	private JTextArea 				messagesTextArea;
	private JProgressBar 			searchProgressBar;

	private MySearchForWordWorker 	searchWorker;

	private final String[] 			fileExtensions = new String[] { "ALL",".mp4", ".mp3", ".pdf", ".epub", ".txt", ".avi", ".mov", ".ps", ".doc", ".xls", ".xml", ".chm", ".djvu", ".html", ".exe" } ;
	private JList<String>			extensionsList;
	private List<String> 			selectedExtensionsList;
	

	private static final String DELIMITER = ";";
	
	
	private int 				numDirFilePaths = 0;		//	for the 'dirs' directory; one radio button per file
	private String[] 			searchPaths = null;			//	semi-colon-concatenated strings from each file
	private Map<String,String> 	srchMap = new HashMap<String,String>();
	private MouseListener 		mouseListener;
	
	private boolean 			searchFileText = false;

	public MyApplication() {
		identifyDirsInfo();
		initActions();
		initComponents();
	}
	
	private void identifyDirsInfo() {
		try {
			File dirs = new File("C:\\Users\\Navroz\\eclipseSpaces\\workspace\\SwingFileSearch\\dirs\\");//("/SwingFileSearch/dirs");	//
			
			final File[] dirFiles = dirs.listFiles();
			int numFiles = dirFiles.length;
			this.numDirFilePaths = numFiles;
			
			System.out.println("Found " + this.numDirFilePaths + " files" );
			
			for( File f : dirFiles) {
				String fullPath = "";
				String line = "";
				BufferedReader br = new BufferedReader(new FileReader(f));
				while ((line=br.readLine())!=null) {
					fullPath += line + DELIMITER;
				}
				br.close();
				this.srchMap.put(	f.getName().substring(0, (f.getName().length()-".txt".length())), fullPath	);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	private void initActions() {

		browseAction = new AbstractAction("Browse") {

			private static final long serialVersionUID = 4669650683189592364L;


			@Override
			public void actionPerformed(final ActionEvent e) {
				final File dir = new File(directoryPathTextField.getText())
						.getAbsoluteFile();
				final JFileChooser fileChooser = new JFileChooser(
						dir.getParentFile());
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setMultiSelectionEnabled(true);
				final int option = fileChooser
						.showOpenDialog(MyApplication.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					final File[] selectedFiles = fileChooser.getSelectedFiles();
					String filePathsToAppend = directoryPathTextField.getText();				
					if ( ! StringUtils.isEmpty(filePathsToAppend) ) {
						filePathsToAppend += DELIMITER;
					 }
					
					 for (File sourceFile: selectedFiles) {
						 filePathsToAppend +=  sourceFile.getAbsolutePath() + DELIMITER;
					 }
					
					directoryPathTextField.setText(filePathsToAppend);
				}
			}
		};

		searchCancelAction = new AbstractAction("Search") {

			private static final long serialVersionUID = 4669650683189592364L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (searchWorker == null) {
					if( !searchFileText )
						search();
					else
						searchText();
				} else {
					cancel();
				}
			}
		};
		
		
		clearDirsAction = new AbstractAction("Clear Directories") {
			@Override
			public void actionPerformed(final ActionEvent e) {
				directoryPathTextField.setText("");
			}
		};
		
		
		clearResultsAction = new AbstractAction("Clear All Results") {
			@Override
			public void actionPerformed(final ActionEvent e) {
				messagesTextArea.setText("");
			}
		};
		
		copyAction = new AbstractAction("Copy Results") {
			@Override
			public void actionPerformed(final ActionEvent e) {
				StringSelection selection = new StringSelection(messagesTextArea.getText());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents(selection, selection);
			}
		};
		
		
		playListAction = new AbstractAction("PlayList") {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPlayList(messagesTextArea.getText());
				
			}

			private void createPlayList(String text) {
				new PlayListCreator(text, wordTextField.getText()).create();
				
			}			
		};
	}

	private void initComponents() {
		setLayout(new GridBagLayout());

	    GridBagConstraints constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 0;
	    constraints.gridheight = 1;
	    constraints.insets = new Insets(2, 2, 2, 2);
	    add(new JLabel("Word within FileName: "), constraints);

	    wordTextField = new JTextField();
	    constraints = new GridBagConstraints();
	    constraints.gridx = 1;
	    constraints.gridy = 0;
	    constraints.gridwidth = 2;
	    constraints.gridheight = 1;
	    constraints.insets = new Insets(2, 2, 2, 2);
	    constraints.weightx = 1;
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    add(wordTextField, constraints);
	    

	    searchTextCheckBox = new JCheckBox("Search Text");
	    searchTextCheckBox.setEnabled(true);
	    searchTextCheckBox.setSelected(false);

	    
	    searchTextCheckBox.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {				
				if(!searchTextCheckBox.isSelected()) {
					searchFileText = false;
				} else {
					searchFileText = true;
				}
			}	    	
	    });
	    
	    
	    constraints = new GridBagConstraints();
	    constraints.gridx = 3;
	    constraints.gridy = 0;
	    constraints.gridwidth = 2;
	    constraints.gridheight = 1;
	    constraints.insets = new Insets(2, 2, 2, 2);
	    constraints.weightx = 1;
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    add(searchTextCheckBox, constraints);
	    
	    

	    constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 1;
	    constraints.gridheight = 1;
	    constraints.insets = new Insets(0,0,0,0);

	    add(new JLabel("Directory Path: "), constraints);

	    directoryPathTextField = new JTextField();

	    constraints = new GridBagConstraints();
	    constraints.gridx = 1;
	    constraints.gridy = 1;
	    constraints.gridwidth = 2;
	    constraints.gridheight = 1;
	    constraints.insets = new Insets(0,0,0,0);
	    constraints.weightx = 1;
	    constraints.weighty = 2;
	    constraints.fill = GridBagConstraints.HORIZONTAL;//.BOTH;
	    add(directoryPathTextField, constraints);
	    
	    
	    
	    DefaultListModel model = new DefaultListModel();
	    model.ensureCapacity(1000);
	    for (int i = 0; i < fileExtensions.length; i++) {
	        model.addElement(fileExtensions[i]);
	    }
	    
	    extensionsList = new JList<String>(fileExtensions);
	    extensionsList.setVisibleRowCount(3);
	    extensionsList.setSelectedIndex(0);
	    

	    extensionsList.setFixedCellHeight(20);
	    extensionsList.setFixedCellWidth(150);
	    
	    extensionsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    
	    extensionsList.addListSelectionListener(new ListSelectionListener() {
	    	public void valueChanged(ListSelectionEvent e) {
	    		selectedExtensionsList = extensionsList.getSelectedValuesList(); 
	    	}
	    });
	    
	    constraints = new GridBagConstraints();
	    constraints.gridx = 3;
	    constraints.gridy = 1;
	    constraints.gridwidth = 2;
	    constraints.gridheight = 1;
	    constraints.insets = new Insets(0,0,0,0);
	    constraints.weightx = 1;
	    constraints.weighty = 1;
	    
	    JScrollPane scrollPane1 = new JScrollPane(extensionsList);
	    
	    add(scrollPane1, constraints);
	    
	    

	    constraints = new GridBagConstraints();
	    constraints.gridx = 5;
	    constraints.gridy = 1;
	    constraints.gridwidth = 1;
	    constraints.gridheight = 1;
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.insets = new Insets(0,0,0,0);
	    add(new JButton(browseAction), constraints);
	    
	    
	    JPanel radioPanel = new JPanel(new GridLayout(1, 0));
	    ButtonGroup butGroup = new ButtonGroup();	//	for dirs selection

	    Set<String> keys = this.srchMap.keySet();
	    JRadioButton[] radios = new JRadioButton[keys.size()];
	    this.searchPaths = new String[keys.size()];
	    int rbCount = 0;
	    for(String dirC : keys) {System.out.println("dirC is "+dirC);
	    	radios[rbCount] = new JRadioButton(dirC);
	    	radios[rbCount].setText(dirC);
	    	radios[rbCount].setActionCommand(dirC);
	    	
	    	radios[rbCount].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					directoryPathTextField.setText(srchMap.get(e.getActionCommand()));
					
				}	    		
	    	});
	    	
	    	butGroup.add(radios[rbCount]); 	  
	    	radioPanel.add(radios[rbCount]);
	    	
	    	rbCount++;
	    	
	    }
	    
	    constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 2;
	    constraints.gridwidth = 5;
	    constraints.insets = new Insets(0,0,0,0);
	    constraints.weightx = 1;
	    constraints.weighty = 3;
	    constraints.fill = GridBagConstraints.BOTH;

	    add(radioPanel, constraints);
	    
	    

	    messagesTextArea = new JTextArea();
	    messagesTextArea.setEditable(false);
	    messagesTextArea.addMouseListener(mouseListener);
	    constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 3;
	    constraints.gridwidth = 5;
	    constraints.insets = new Insets(0,0,0,0);
	    constraints.weightx = 1;
	    constraints.weighty = 3;
	    constraints.fill = GridBagConstraints.BOTH;
	    add(new JScrollPane(messagesTextArea), constraints);

	    searchProgressBar = new JProgressBar();
	    searchProgressBar.setStringPainted(true);
	    searchProgressBar.setVisible(true);
	    constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 4;
	    constraints.gridwidth = 2;
	    constraints.insets = new Insets(2, 2, 2, 2);
	    constraints.weightx = 1;
	    constraints.fill = GridBagConstraints.BOTH;
	    add(searchProgressBar, constraints);

	    constraints = new GridBagConstraints();
	    constraints.gridx = 3;
	    constraints.gridy = 5;
	    constraints.insets = new Insets(2, 2, 2, 2);
	    constraints.weightx = 0;
	    add(new JButton(searchCancelAction), constraints);
	    
	    constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 5;
	    constraints.insets = new Insets(2, 2, 2, 2);
	    constraints.weightx = 0;
	    add(new JButton(copyAction), constraints);
	    
	    constraints = new GridBagConstraints();
	    constraints.gridx = 2;
	    constraints.gridy = 5;
	    constraints.insets = new Insets(2, 2, 2, 2);
	    constraints.weightx = 0;
	    add(new JButton(clearDirsAction), constraints);
	    
	    constraints = new GridBagConstraints();
	    constraints.gridx = 1;
	    constraints.gridy = 5;
	    constraints.insets = new Insets(2, 2, 2, 2);
	    constraints.weightx = 0;
	    add(new JButton(clearResultsAction), constraints);
	    
	    constraints = new GridBagConstraints();
	    constraints.gridx = 4;
	    constraints.gridy = 5;
	    constraints.insets = new Insets(2, 2, 2, 2);
	    constraints.weightx = 0;
	    add(new JButton(playListAction), constraints);
	    
	    pack();
	    
	}

		
	private void cancel() {
		searchWorker.cancel(true);
	}
	
	private void searchText() {
		if (searchFileText) {
			final String word = wordTextField.getText();
			System.out.println("in searchText() directoryPathTextField.getText() is " + directoryPathTextField.getText());
			String[] theDirs = directoryPathTextField.getText().split(DELIMITER);
			for (int i = 0; i < theDirs.length; i++) {
				searchWorker = new MySearchForWordWorker(word, theDirs[i], selectedExtensionsList, searchFileText, messagesTextArea);
				searchWorker.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(final PropertyChangeEvent event) {
						/*System.out.println("event-is--"+event);
						System.out.println("event.getProopertyName---"+event.getPropertyName());
						System.out.println("event.getNewValue-is---"+event.getNewValue());
						System.out.println("in MyAPPLICATIOn " + event.getPropertyName() + " value is "
								+ (StateValue) event.getNewValue());*/
						switch (event.getPropertyName()) {
						case "progress":
							searchProgressBar.setIndeterminate(false);
							searchProgressBar.setValue((Integer) event.getNewValue());
							break;
						case "state":
							switch ((StateValue) event.getNewValue()) {
							case DONE:
								searchProgressBar.setVisible(false);
								searchCancelAction.putValue(Action.NAME, "Search");
								searchWorker = null;
								break;
							case STARTED:
							case PENDING:
								searchCancelAction.putValue(Action.NAME, "Cancel");
								searchProgressBar.setVisible(true);
								searchProgressBar.setIndeterminate(true);
								break;
							}
							break;
						}
					}
				});

				searchWorker.execute();
			}
			
		}
	}

	private void search() {

		if (!searchFileText) {
			final String word = wordTextField.getText();
			selectedExtensionsList = extensionsList.getSelectedValuesList();
			System.out.println("in search() directoryPathTextField.getText() is " + directoryPathTextField.getText());
			for (String e : selectedExtensionsList) {
				System.out.println("extension is " + e);
			}
			String[] theDirs = directoryPathTextField.getText().split(DELIMITER);
			for (int i = 0; i < theDirs.length; i++) {
				searchWorker = new MySearchForWordWorker(word, theDirs[i], selectedExtensionsList, messagesTextArea);
				searchWorker.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(final PropertyChangeEvent event) {
						/*System.out.println("event-is--"+event);
						System.out.println("event.getProopertyName---"+event.getPropertyName());
						System.out.println("event.getNewValue-is---"+event.getNewValue());
						System.out.println("in MyAPPLICATIOn " + event.getPropertyName() + " value is "
								+ (StateValue) event.getNewValue());*/
						switch (event.getPropertyName()) {
						case "progress":
							searchProgressBar.setIndeterminate(false);
							searchProgressBar.setValue((Integer) event.getNewValue());
							break;
						case "state":
							switch ((StateValue) event.getNewValue()) {
							case DONE:
								searchProgressBar.setVisible(false);
								searchCancelAction.putValue(Action.NAME, "Search");
								searchWorker = null;
								break;
							case STARTED:
							case PENDING:
								searchCancelAction.putValue(Action.NAME, "Cancel");
								searchProgressBar.setVisible(true);
								searchProgressBar.setIndeterminate(true);
								break;
							}
							break;
						}
					}
				});

				searchWorker.execute();
			} 
		}
	}

}
