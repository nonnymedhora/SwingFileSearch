/**
 * 
 */
package org.bawaweb.swingSearch.try2.prsnl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Navroz
 *
 */
public class PlayListCreator {
	final String top = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
						"<playlist xmlns=\"http://xspf.org/ns/0/\" xmlns:vlc=\"http://www.videolan.org/vlc/playlist/ns/0/\" version=\"1\">\n"+
						"	<title>Playlist</title>\n"+
						"	<trackList>";
	
	final String bot = "\n	</trackList>";
	
	private String filesListing = "";
	private String srchString = "";

	private final String playListFilePath = "H:\\temp\\playList ("+System.currentTimeMillis()+").xspf";
	
	public PlayListCreator(String data, String srch) {
		this.filesListing = data;
		this.srchString = srch;
	}
	
	public void create() {
		String[] cleanedLines = cleanFilesListing(this.filesListing);
		String tracksInfo = generatePlayListTracks(cleanedLines);
		String extensionsInfo = generateExtensionInfo(cleanedLines.length);
		
		String fileContents = top + tracksInfo + extensionsInfo + bot;
		write2File(fileContents);
	}

	
	private void write2File(String fileContents) {
		final String listFilePath = playListFilePath.replace("playList", this.srchString);;
		try {
			FileWriter fw = new FileWriter( listFilePath  );
			fw.write(fileContents);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * <extension application="http://www.videolan.org/vlc/playlist/0">
			<vlc:item tid="0"/>
			<vlc:item tid="1"/>
			<vlc:item tid="2"/>
			<vlc:item tid="3"/>
	</extension>
	 * @param length
	 * @return
	 */
	private String generateExtensionInfo(int length) {
		StringBuilder builder = new StringBuilder("\n	<extension application=\"http://www.videolan.org/vlc/playlist/0\">\n");
		final String vlcItem = "	<vlc:item tid=\"X\"/>\n";
		for(int iindex = 0;	iindex < length; iindex ++) {
			String vIt = vlcItem;
			
			vIt = vIt.replace("X", String.valueOf(iindex));
			
			builder.append(vIt);
		}
		builder.append("\n	</extension>");
		return builder.toString();
	}

	/**
	 * <track>
			<location>file:///H:/xitiz/HxVidZ/wwwwWwwww/039.mpeg.avi</location>
			<extension application="http://www.videolan.org/vlc/playlist/0">
				<vlc:id>1</vlc:id>
			</extension>
		</track>
	 * @param cleanedLines
	 */
	private String generatePlayListTracks(String[] cleanedLines) {
		StringBuilder builder = new StringBuilder();		
		final String trackInfo = 
								"\n"+
								"		<track>\n"+
								"			<location>_FILETRACK_</location>\n"+
								"			<extension application=\"http://www.videolan.org/vlc/playlist/0\">\n"+
								"				<vlc:id>_FILENUM_</vlc:id>\n"+
								"			</extension>\n"+
								"		</track>\n";
		int i = 0;
		for(String line : cleanedLines) {
//			System.out.println("cleanedLine is "+line);
			line = line.substring(0,line.length()-1);	//removes CR
			String tInf = trackInfo;
			tInf = tInf.replace("_FILETRACK_", line).replace("_FILENUM_", String.valueOf(i));
			builder.append(tInf);
			i++;
		}
		return builder.toString();
	}

	private String[] cleanFilesListing(String theList) {
		String[] theCleanedLines = null;
		List<String> cleanedLinesList = new ArrayList<String>();
		String[] lines = theList.split("\n");
		String replaceStart = "file:///";
		
		for(int lineNum = 0; lineNum < lines.length; lineNum++) {
			String line = lines[lineNum];
			if(!line.isEmpty()) {
				if ( line.contains(".mp4") || line.contains(".mov") || line.contains(".avi") || line.contains(".mp3") ) {
					line = replaceStart + line.replaceAll("\\\\", "/");
					line = line.replaceAll(" ","%20");
					line = line.replaceAll("\\&","&amp;");
					line = line.replaceAll("\\[","%5B");
					line = line.replaceAll("\\]","%5D");
					line = line.replaceAll("\\#","%23");
					line = line.replaceAll("\\~","%7E");
					
					cleanedLinesList.add(line);
				}
			}
		}
		
		theCleanedLines = new String[cleanedLinesList.size()];
		for (int index = 0; index < cleanedLinesList.size(); index++) {
			theCleanedLines[index] = cleanedLinesList.get(index);
		}
		
		return theCleanedLines;
		
		/*for( String line : lines ) {
			line.trim();
//			System.out.println("11Line is " + line);
			//if ( line.contains(".mp4") || line.contains(".mov") || line.contains(".avi") || line.contains(".mp3") ) {
				line = replaceStart + line.replaceAll("\\\\", "/");
				line = line.replaceAll(" ","%20");//.replaceAll("[","%5B").replaceAll("]","%5D");//.replaceAll("&","&amp;");
			//}
		}
		return lines;*/
		
	}

	/*public static void main(String[] args) {
		

	}*/

}
