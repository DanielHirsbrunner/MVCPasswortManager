package application.commonClasses;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import application.ServiceLocator;

/**
 * Die Translator Klasse kümmert sich um die Sprachtexte und liefert abhänig vom gesetzten Locale die benötigte Lang. Texte
 * Abgesehen von dem Umbau auf die LangTexte Enum entspricht Sie dem im Unterricht behandeltem Beispiel.
 * 
 * @author Brad Richards
 */
public class Translator {

	private ServiceLocator sl = ServiceLocator.getServiceLocator();
	private Logger logger = sl.getLogger();

	protected Locale currentLocale;
	private ResourceBundle resourceBundle;

	public Translator(String localeString) {
		// Can we find the language in our supported locales?
		// If not, use VM default locale
		Locale locale = Locale.getDefault();
		if (localeString != null) {
			Locale[] availableLocales = sl.getLocales();
			for (int i = 0; i < availableLocales.length; i++) {
				String tmpLang = availableLocales[i].getLanguage();
				if (localeString.substring(0, tmpLang.length()).equals(tmpLang)) {
					locale = availableLocales[i];
					break;
				}
			}
		}

		// Load the resource strings
		resourceBundle = ResourceBundle.getBundle(sl.getAPP_CLASS().getName(),
				locale);
		Locale.setDefault(locale); // Change VM default (for dialogs, etc.)
		currentLocale = locale;

		logger.info("Loaded resources for " + locale.getLanguage());
	}

	/**
	 * Return the current locale; this is useful for formatters, etc.
	 * @return aktives Locale
	 */
	public Locale getCurrentLocale() {
		return currentLocale;
	}

	/**
	 * Public method to get string resources, default to "--" *
	 * @param text Enum eintrag für die Sprachkonstante
	 * @return Sprachtext für das aktuelle Locale
	 */
	public String getString(LangText text) {
		try {
			return resourceBundle.getString(text.toString());
		} catch (MissingResourceException e) {
			logger.warning("Missing string: " + text.toString());
			return "--";
		}
	}
}
