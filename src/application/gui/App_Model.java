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
    
	public ObservableList<Password> GetPasswords() {
		return this.passwords;
	}
	
	public Password[] GetPasswordArrayForExport() {
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
    
	public Password GetCurrentPw() {
		return this.currentPw;
	}

	public void SetCurrentPw(Password newPassword) {
		this.currentPw = newPassword;
	}
	
	public boolean GetIsInEdit() {
		return this.isInEdit;
	}

	public void SetIsInEdit(boolean inEdit) {
		this.isInEdit = inEdit;
	}
	
	public boolean AddPassword(Password pw) {
		return this.passwords.add(pw);
	}

	public void SavePasswords() {
		String xmlFile = this.serviceLocator.getConfiguration().getOption(Option.DBFileName);
		if (this.currentPw != null) {
			this.currentPw.setPasswortInEdit(false);
		}
		Password[] pws = new Password[this.passwords.size()];
		int i = 0;
		for (Password pw : this.passwords) {
			pws[i] = pw;
			i++;
		}
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
				// "Unter dem folgenden Pfad konnten keine gültigen Daten gefunden werden.\nEs wird eine neue Passwort Datenbank angelegt.\nPfad:"
				// + xmlFile);
			}
		} else {
			this.passwords.add(new Password("www.test.ch", "Benutzer1", "passwort",
					"Dies ist ein Testeintrag"));
		}
	}

}
