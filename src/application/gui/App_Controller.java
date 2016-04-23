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
 * 
 * @author Daniel Hirsbrunner
 */
public class App_Controller extends Controller<App_Model, App_View> {
	ServiceLocator serviceLocator;

	public App_Controller(App_Model model, App_View view) {
		super(model, view);
		this.serviceLocator = ServiceLocator.getServiceLocator();
		
		// Action Methoden für View hinzufügen
		// Button: neues Passwort hinzufügen
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
		// Menu Datensicherung zurücklesen
		view.menuFileRestore.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				restoreBackup(view.getStage());
			}
		});
		// Menu Passwörter exportieren
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
				model.SavePasswords();
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
		// Context Menu Eintrag löschen
		view.cmiDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				deletePassword();
			}
		});
		// Context Menu URL öffnen
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

	public void savePassword() {
		Password pw;
		boolean isNew = false;
		if (this.model.GetIsInEdit()) {
			this.model.SetIsInEdit(false);
			pw = this.model.GetCurrentPw();
		} else {
			isNew = true;
			pw = new Password();
		}
		pw.setAdresse(view.txtAdress.getText());
		pw.setBenutzername(view.txtUserName.getText());
		pw.setPasswort(view.txtPassword.getText());
		pw.setBemerkung(view.txtRemark.getText());
		
		if (isNew) {
			this.model.AddPassword(pw);
		} else {
			this.model.GetPasswords().set(this.model.GetPasswords().indexOf(pw), pw);
		}
		view.clearFields();
		view.setDefaultFocus();		
	}
	
	private void createBackup(Stage stage) {
		Exporter.exportToXmlFile(stage, this.model.GetPasswordArrayForExport());
	}

	private void createCsvFile(Stage stage) {
		Exporter.exportToCsvFile(stage, this.model.GetPasswordArrayForExport());
	}

	private void restoreBackup(Stage stage) {
		Password[] newData = Exporter.importFromXmlFile(stage);
		for (Password pw : newData) {
			this.model.AddPassword(pw);
		}
	}

	private void copyPasswordToClipboard() {
		if (this.model.GetCurrentPw() != null) {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(this.model.GetCurrentPw().getPasswort());
			clipboard.setContent(content);
		}
	}

	private void copyUsernameToClipboard() {
		if (this.model.GetCurrentPw() != null) {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(this.model.GetCurrentPw().getBenutzername());
			clipboard.setContent(content);
		}
	}

	private void openCurrentUrl() {
		if (this.model.GetCurrentPw() != null) {
			try {
				Desktop.getDesktop().browse(
						new URI(this.model.GetCurrentPw().getAdresse()));
			} catch (Exception e) {
				this.serviceLocator.getLogger().warning(
						e.toString() + " - " + e.getStackTrace().toString());
				// DialogHelper.ShowWarningDialog(primaryStage, "Fehler",
				// "Es können nur korrekte Internetadressen geöffnet werden");
			}
		}
	}
	
	private void deletePassword() {
		if (this.model.GetCurrentPw() != null) {
			this.model.GetPasswords().remove(model.GetCurrentPw());
		}
	}
	
	private void editPassword() {
		if (this.model.GetCurrentPw() != null) {
			this.model.SetIsInEdit(true);
			this.view.txtAdress.setText(model.GetCurrentPw().getAdresse());
			this.view.txtUserName.setText(model.GetCurrentPw().getBenutzername());
			this.view.txtPassword.setText(model.GetCurrentPw().getPasswort());
			this.view.txtRemark.setText(model.GetCurrentPw().getBemerkung());
			this.view.setDefaultFocus();
		}
	}

	private void chancelEdit() {
		this.model.SetIsInEdit(false);
		this.view.clearFields();
		this.view.setDefaultFocus();
	}

}
