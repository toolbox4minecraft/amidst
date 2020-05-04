package amidst.language;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import amidst.settings.biomeprofile.BiomeProfile;

public class Language {
	/**
	 * <a href="https://regex101.com/r/aLdw5C/1" target="_top">Regex explanation</a>
	 */
	private static final Pattern FILE_READ_REGEX = Pattern.compile("(.*?)\\:\\s*\\\"((?:.|\\n|\\r)*?)(?:\\\"\\,\\s*$|\\\"\\s*\\Z)", Pattern.MULTILINE);
	
	private final String tag;
	private final Map<String, String> translationMap;
	
	private Language(String tag, Map<String, String> translationMap) {
		this.translationMap = translationMap;
		this.tag = tag;
	}
	
	public static Language create(String path) throws IOException {
		String tag = trimPathToFileName(path);
		Map<String, String> translationMap = fileToTranslationMap(path);
		Language language = new Language(tag, translationMap);
		return language;
	}
	
	private static String trimPathToFileName(String fileName) {
		return fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
	}
	
	private static Map<String, String> fileToTranslationMap(String path) throws IOException {
		Map<String, String> newMap = new HashMap<String, String>();
		try (InputStream stream = BiomeProfile.class.getResourceAsStream(path)) { // For some reason this is the only way we can read the file from inside and outside the jar
			try (Scanner scanner = new Scanner(stream)) {
				StringBuffer buffer = new StringBuffer();
				while(scanner.hasNext()){
					buffer.append(scanner.nextLine() + "\n");
				}
				
				final Matcher matcher = FILE_READ_REGEX.matcher(buffer.toString());
				
				while (matcher.find()) {
					newMap.put(matcher.group(1), matcher.group(2));
				}
			}
		} catch (NullPointerException e) {
			throw new IOException(e);
		}
		return newMap;
	}

	public String getTag() {
		return tag;
	}
	
	public String getValue(String key) {
		String s = translationMap.get(key);
		if (s == null) {
			s = "";
		}
		return s;
	}
	
	public String getName() {
		return translationMap.get("name");
	}
	
}
