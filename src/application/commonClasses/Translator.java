package application.commonClasses;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import application.ServiceLocator;

public class Translator {
	
	/*public final String NAME = "program.name";
	public final String TITLE = "program.title";
	public final String MENU_FILE = "program.menu.file";
	public final String MENU_FILE_BACKUP = "program.menu.file.backup";
	public final String MENU_FILE_RESTORE = "program.menu.file.restore";
	public final String MENU_FILE_EXPORT = "program.menu.file.export";
	public final String MENU_FILE_EXIT = "program.menu.file.exit";
	public final String MENU_LANGUAGE = "program.menu.language";
	public final String MENU_HELP = "program.menu.help";
	public final String MENU_HELP_ABOUT = "program.menu.help.about";
	public final String TABLE_COL_ADRESS = "program.table.colAdress";
	public final String TABLE_COL_USERNAME = "program.table.colUserName";
	public final String TABLE_COL_REMARKS = "program.table.colRemark";
	public final String TABLE_COL_PASSWORD = "program.table.colPassword";
	public final String BUTTON_ADD = "program.btnAdd";
	public final String CONTEXTMENU_OPENADR = "program.contextmenu.openadr";
	public final String CONTEXTMENU_COPYUSERNAME = "program.contextmenu.copyuser";
	public final String CONTEXTMENU_COPYPASSWORD = "program.contextmenu.copypassword";
	public final String CONTEXTMENU_DELETEENTRY = "program.contextmenu.delete";*/

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
	 */
	public Locale getCurrentLocale() {
		return currentLocale;
	}

	/**
	 * Public method to get string resources, default to "--" *
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
