package application.gui;

import java.awt.Desktop;
import java.net.URI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import application.ServiceLocator;
import application.abstractClasses.Controller;
import application.commonClasses.Exporter;
import application.commonClasses.Password;

/**
 * Der App Controller ist für die Steuerung der Hauptview zuständig und koordiniert die einzelnen Befehle
 * Er verwaltet die verschiedenen Eventmethoden und Listener und handelt die Callbacks
 * 
 * @author Daniel Hirsbrunner
 */
public class App_Controller extends Controller<App_Model, App_View> {
	ServiceLocator serviceLocator;

	public App_Controller(App_Model model, App_View view) {
		super(model, view);
		this.serviceLocator = ServiceLocator.getServiceLocator();
		
		// Action Methoden fuer View hinzufuegen
		// Button: neues Passwort hinzufuegen
		view.btnAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				savePassword();
			}
		});
		// Menu Datensicherung erstellen
		view.menuFileBackup.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				createBackup(view.getStage());

			}
		});
		// Menu Datensicherung zuruecklesen
		view.menuFileRestore.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				restoreBackup(view.getStage());
			}
		});
		// Menu Passwoerter exportieren
		view.menuFileExport.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				createCsvFile(view.getStage());
			}
		});
		// Menu Programm beenden
		view.menuFileExit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				view.getStage().close();
			}
		});
		// Stage beim schliessen
		view.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				// beim schliessen die Daten speichern
				model.savePasswords();
				Platform.exit();
			}
		});
		// Context Menu Passwort kopieren
		view.cmiCopyPW.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				copyPasswordToClipboard();
			}
		});
		// Context Menu Benutzername kopieren
		view.cmiCopyUser.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				copyUsernameToClipboard();
			}
		});
		// Context Menu Eintrag loeschen
		view.cmiDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				deletePassword();
			}
		});
		// Context Menu URL oeffnen
		view.cmiOpenAdr.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openCurrentUrl();
			}
		});
		// Context Menu Benutzername kopieren
		view.cmiEdit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				editPassword();
			}
		});

		serviceLocator.getLogger().info("Application controller initialized");
	}

	private void savePassword() {
		Password pw;
		boolean isNew = false;
		if (this.model.GetIsInEdit()) {
			this.model.setIsInEdit(false);
			pw = this.model.getCurrentPw();
		} else {
			isNew = true;
			pw = new Password();
		}
		pw.setAdresse(view.txtAdress.getText());
		pw.setBenutzername(view.txtUserName.getText());
		pw.setPasswort(view.txtPassword.getText());
		pw.setBemerkung(view.txtRemark.getText());
		
		if (isNew) {
			this.model.addPassword(pw);
		} else {
			this.model.getPasswords().set(this.model.getPasswords().indexOf(pw), pw);
		}
		view.clearFields();
		view.setDefaultFocus();		
	}
	
	private void createBackup(Stage stage) {
		Exporter.exportToXmlFile(stage, this.model.getPasswordArrayForExport());
	}

	private void createCsvFile(Stage stage) {
		Exporter.exportToCsvFile(stage, this.model.getPasswordArrayForExport());
	}

	private void restoreBackup(Stage stage) {
		Password[] newData = Exporter.importFromXmlFile(stage);
		for (Password pw : newData) {
			this.model.addPassword(pw);
		}
	}

	private void copyPasswordToClipboard() {
		if (this.model.getCurrentPw() != null) {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(this.model.getCurrentPw().getPasswort());
			clipboard.setContent(content);
		}
	}

	private void copyUsernameToClipboard() {
		if (this.model.getCurrentPw() != null) {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(this.model.getCurrentPw().getBenutzername());
			clipboard.setContent(content);
		}
	}

	private void openCurrentUrl() {
		if (this.model.getCurrentPw() != null) {
			try {
				Desktop.getDesktop().browse(
						new URI(this.model.getCurrentPw().getAdresse()));
			} catch (Exception e) {
				this.serviceLocator.getLogger().warning(
						e.toString() + " - " + e.getStackTrace().toString());
				// DialogHelper.ShowWarningDialog(primaryStage, "Fehler",
				// "Es koennen nur korrekte Internetadressen geoeffnet werden");
			}
		}
	}
	
	private void deletePassword() {
		if (this.model.getCurrentPw() != null) {
			this.model.getPasswords().remove(model.getCurrentPw());
		}
	}
	
	private void editPassword() {
		if (this.model.getCurrentPw() != null) {
			this.model.setIsInEdit(true);
			this.view.txtAdress.setText(model.getCurrentPw().getAdresse());
			this.view.txtUserName.setText(model.getCurrentPw().getBenutzername());
			this.view.txtPassword.setText(model.getCurrentPw().getPasswort());
			this.view.txtRemark.setText(model.getCurrentPw().getBemerkung());
			this.view.setDefaultFocus();
		}
	}

	private void chancelEdit() {
		this.model.setIsInEdit(false);
		this.view.clearFields();
		this.view.setDefaultFocus();
	}

}
