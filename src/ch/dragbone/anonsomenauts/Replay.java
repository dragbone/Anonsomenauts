package ch.dragbone.anonsomenauts;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public final class Replay{
	private static final String charsetString = "UTF-8";
	public static final Charset defaultCharset = Charset.forName(charsetString), dataCharset = Charset
			.forName("ISO-8859-1");

	private final String replayPath, replacementName;

	Replay(String replayPath, String replacementName){
		this.replayPath = replayPath;
		this.replacementName = replacementName;
	}

	/**
	 * Creates a copy of the given replay and replaces all personal data in that copy
	 * @throws IOException 
	 */
	void anonify() throws IOException{
		File originalReplayFolder = new File(replayPath);

		// Check if it is an existing folder
		if(!originalReplayFolder.exists()){
			throw new IOException("Selected folder does not exist");
		}
		if(!originalReplayFolder.isDirectory() || !originalReplayFolder.exists()){
			throw new IOException("Selected folder is not a folder");
		}
		if(!new File(originalReplayFolder, "Chatlog.info").exists()
				|| !new File(originalReplayFolder, "Replays.info").exists()){
			throw new IOException("Selected folder does not contain a replay");
		}

		/*
		 * Copy Replay
		 */
		String[] pathParts = replayPath.split("[/\\\\]");
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < pathParts.length - 1; ++i){
			sb.append(pathParts[i]);
			sb.append("/");
		}
		sb.append("Anon - " + pathParts[pathParts.length - 1]);
		String anonReplayPath = sb.toString();

		File anonReplayFolder = new File(anonReplayPath);
		if(anonReplayFolder.exists()){
			// TODO Ask if overwrite replay
			FileUtils.deleteDirectory(anonReplayFolder);
		}
		anonReplayFolder.mkdir();
		FileUtils.copyDirectory(originalReplayFolder, anonReplayFolder);

		File chatlogFile = new File(anonReplayFolder, "Chatlog.info");
		File replaysFile = new File(anonReplayFolder, "Replays.info");

		Set<String> names = getNames(replaysFile);

		anonifyXML(chatlogFile, names, defaultCharset);
		anonifyXML(replaysFile, names, dataCharset);

		File[] dataFiles = anonReplayFolder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name){
				return name.endsWith(".blockData") || name.endsWith(".continuousData");
			}
		});
		for(File dataFile : dataFiles){
			anonifyReplay(dataFile, names);
		}

		System.out.println("Done");
	}

	/**
	 * Removes names in replay data files (*.blockData and *.continuousData)
	 * @param replayFile
	 * @param names
	 * @throws IOException
	 */
	private void anonifyReplay(File replayFile, Set<String> names) throws IOException{
		// Replace (ascii) text
		String fileContents = new String(FileUtils.readFileToByteArray(replayFile), dataCharset);
		for(String name : names){
			fileContents = fileContents.replace(name, replacementName);
		}

		// Replace bit compressed strings
		byte[] data = fileContents.getBytes(dataCharset);
		BitData bd = new BitData(data);
		int pos = -1;
		for(String name : names){
			do{
				pos = bd.find(name.getBytes(dataCharset), pos + 1);
				if(pos != -1){
					bd.replace(pos, name.length() * 8, BitData.byteToBoolArray(replacementName.getBytes()));
				}
			}while(pos != -1);
		}
		data = bd.toByteArray();
		FileUtils.writeByteArrayToFile(replayFile, data, false);
	}

	/**
	 * Removes names in XML based files (Chatlog.info and Replays.info files)
	 * @param file
	 * @param names
	 * @param charset
	 * @throws IOException
	 */
	private void anonifyXML(File file, Set<String> names, Charset charset) throws IOException{
		List<String> lines = FileUtils.readLines(file, charset);
		List<String> newLines = new ArrayList<>();
		for(String line : lines){
			for(String name : names){
				line = line.replace(name, replacementName);
			}
			line = StringHelper.replaceSubMatch(line, " steamId=\"(.*?#\\d{16,18})\"", replacementName
					+ "#31415926535897932");
			newLines.add(line);
		}
		FileUtils.writeLines(file, charsetString, newLines, false);
	}

	/**
	 * Get all player names (including steam name and versions due to different encoding)
	 * @param replaysFile
	 * @return
	 * @throws IOException
	 */
	private Set<String> getNames(File replaysFile) throws IOException{
		List<String> lines = new ArrayList<>();
		lines.addAll(FileUtils.readLines(replaysFile, defaultCharset));
		lines.addAll(FileUtils.readLines(replaysFile, dataCharset));
		Set<String> names = new HashSet<>();
		for(String line : lines){
			String userName = StringHelper.firstSubMatch(line, " name=\"(.*?)\"");
			if(userName != null && userName.length() > 0){
				names.add(userName);
				// Fix XML escape characters
				String fixUserName = StringHelper.unEscapeXMLString(userName);
				if(!userName.equals(fixUserName)){
					names.add(fixUserName);
				}
			}
			String steamName = StringHelper.firstSubMatch(line, " steamId=\"(.*?)#\\d{16,18}\"");
			if(steamName != null && steamName.length() > 0)
				names.add(steamName);
		}
		return names;
	}

}
