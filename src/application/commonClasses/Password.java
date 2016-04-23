package application.commonClasses;

import javafx.beans.property.SimpleStringProperty;


public class Password  {

	public static final String DEFAULT_FAKE_PASSWORD = "******";
	public SimpleStringProperty adresse = new SimpleStringProperty(), benutzername = new SimpleStringProperty(), passwort = new SimpleStringProperty(), bemerkung = new SimpleStringProperty(), passwortFake = new SimpleStringProperty();
	private boolean passwortInEdit = false;
	
	public Password(String adresse, String benutzername, String passwort, String bemerkung) {
		this.passwortFake.set(DEFAULT_FAKE_PASSWORD);
		this.adresse.set(adresse);
		this.benutzername.set(benutzername);
		this.passwort.set(passwort);
		this.bemerkung.set(bemerkung);
	}
	public Password () {
		this.passwortFake.set(DEFAULT_FAKE_PASSWORD);
	}

	public void setPasswort(String passwort) {
		this.passwort.set(passwort);
	}

	public String getPasswort() {
		return passwort.get();
	}
	
	public String getPasswortFake() {
		if (this.passwortInEdit) {
			return passwort.get();
		} else {
			return this.passwortFake.get();
		}
	}

	public void setPasswortFake(String passwortFake) {
		this.passwortFake.set(passwortFake);
	}
	
    public SimpleStringProperty getPasswortFakeProperty(){
        return new SimpleStringProperty(this.getPasswortFake());
    }
    
	public void setPasswortInEdit(Boolean passwortInEdit) {
		this.passwortInEdit = passwortInEdit;
	}
	public String getBenutzername() {
		return benutzername.get();
	}

	public void setBenutzername(String benutzername) {
		this.benutzername.set(benutzername);
	}

	public String getAdresse() {
		return adresse.get();
	}

	public void setAdresse(String adresse) {
		this.adresse.set(adresse);
	}

	public String getBemerkung() {
		return bemerkung.get();
	}

	public void setBemerkung(String bemerkung) {
		this.bemerkung.set(bemerkung);
	}

}
