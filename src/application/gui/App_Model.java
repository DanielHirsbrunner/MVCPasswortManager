package application.gui;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.abstractClasses.Model;
import application.commonClasses.Password;
import application.commonClasses.XmlHelper;
import application.commonClasses.Configuration.Option;
import application.ServiceLocator;

/**
 * Model für die Datenhaltung der App_View
 * 
 * Cached alle Passwörter zur Laufzeit damit diese wenn das Programm beendet wird wieder gespeichert werden können. 
 * 
 * @author Daniel Hirsbrunner
 */
public class App_Model extends Model {
    private ServiceLocator serviceLocator;
	private Password currentPw;
	private boolean isInEdit = false;

	private ObservableList<Password> passwords = FXCollections.observableArrayList();
    
    public App_Model() {        
        serviceLocator = ServiceLocator.getServiceLocator();
        serviceLocator.getLogger().info("Load Data");
        this.initData();
        serviceLocator.getLogger().info("Application model initialized");
    }
    /**
     * Gibt alle aktuellen Passwörder als Observable View zurück
     * Diese wird in der Tabele der View angezeigt
     * @return Observable liste alle Passwörter
     */
	public ObservableList<Password> getPasswords() {
		return this.passwords;
	}
	/**
	 * Gibt alle aktuellen Passwörter als Array zurück
	 * Benötigt für export oder speicherung.
	 * @return Array aller Passwörter
	 */
	public Password[] getPasswordArrayForExport() {
		if (this.currentPw != null) {
			this.currentPw.setPasswortInEdit(false);
		}
		Password[] pws = new Password[this.passwords.size()];
		int i = 0;
		for (Password pw : this.passwords) {
			pws[i] = pw;
			i++;
		}
		return pws;
	}
    /**
     * Aktives Passwort
     * @return aktuell ausgewähltes Passwort
     */
	public Password getCurrentPw() {
		return this.currentPw;
	}
	/**
	 * setzt das Aktive Passwort, benötigt wenn in anzeige ein neuer Eintrag ausgewählt wird.
	 * @param newPassword Neu ausgewähltes Passwort
	 */
	public void setCurrentPw(Password newPassword) {
		this.currentPw = newPassword;
	}
	/**
	 * Ist der User am editieren
	 * @return boolean true oder false
	 */
	public boolean GetIsInEdit() {
		return this.isInEdit;
	}
	/**
	 * aktiviert oder deaktiviert den Edit Modus
	 * @param inEdit Ist der Editmodus aktiv
	 */
	public void setIsInEdit(boolean inEdit) {
		this.isInEdit = inEdit;
	}
	
	/**
	 * Fügt der Auflistung ein neues Passwort hinzu
	 * @param pw Neues Passwort
	 * @return boolean ob erfolgreich
	 */
	public boolean addPassword(Password pw) {
		return this.passwords.add(pw);
	}
	/**
	 * Speichert die Passwörter in die DB
	 */
	public void savePasswords() {
		String xmlFile = this.serviceLocator.getConfiguration().getOption(Option.DBFileName);
		if (this.currentPw != null) {
			this.currentPw.setPasswortInEdit(false);
		}
		Password[] pws = this.getPasswordArrayForExport();
		try {
			XmlHelper.writeEncrypted(pws, xmlFile);
		} catch (Exception e) {
			this.serviceLocator.getLogger().warning(
					e.toString() + " - " + e.getStackTrace().toString());
			// DialogHelper.ShowWarningDialog(stage, "Fehler",
			// "Die Datei konnte nicht unter dem Pfad:" + xmlFile +
			// "\n gespeichert werden.");
		}
	}
	
	/**
	 * Liest die Passwörter aus der DB
	 */
	private void initData() {
		String xmlFile = this.serviceLocator.getConfiguration().getOption(Option.DBFileName);
		File f = new File(xmlFile);
		if (f.exists()) {
			try {
				this.passwords.addAll(XmlHelper.readEncrypted(xmlFile));
			} catch (Exception e) {
				this.serviceLocator.getLogger().warning(
						e.toString() + " - " + e.getStackTrace().toString());
				// DialogHelper.ShowWarningDialog(stage, "Fehler",
				// "Unter dem folgenden Pfad konnten keine gueltigen Daten gefunden werden.\nEs wird eine neue Passwort Datenbank angelegt.\nPfad:"
				// + xmlFile);
			}
		} else {
			this.passwords.add(new Password("www.test.ch", "Benutzer1", "passwort",
					"Dies ist ein Testeintrag"));
		}
	}

}
