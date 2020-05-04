package amidst.language;

import java.io.IOException;
import java.util.Locale;

import amidst.logging.AmidstLogger;

public class I18n {
	private static final String LANG_PATH = "/amidst/lang/";
	private static final String FILE_EXTENSION = ".txt";
	
	private static Language currentLang;
	private static Locale currentLocale;
	
	public static String get(String key) {
		return currentLang.getValue(key);
	}
	
	public static void setLocalization(String languageTag) throws IOException {
		currentLocale = Locale.forLanguageTag(languageTag);
		currentLang = Language.create(LANG_PATH + languageTag + FILE_EXTENSION);
		Locale.setDefault(currentLocale);
	}
	
	public static void setLocalization(Locale locale) throws IOException {
		currentLocale = locale;
		currentLang = Language.create(LANG_PATH + locale.toLanguageTag() + FILE_EXTENSION);
		Locale.setDefault(currentLocale);
	}
	
	public static Language getLanguage() {
		return currentLang;
	}
	
	public static Locale getLocale() {
		return currentLocale;
	}
	
	static {
		Locale defaultLocale = Locale.getDefault();
		
		try {
			setLocalization(defaultLocale);
		} catch (IOException e) {
			AmidstLogger.warn("Unable to set to language " + defaultLocale.toLanguageTag() + ", falling back to en-US.");
			try {
				setLocalization("en-US");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
