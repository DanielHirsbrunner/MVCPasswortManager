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
				addPassword();
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
		// Context Menu eintrag löschen
		view.cmiDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (model.GetCurrentPw() != null) {
					model.GetPasswords().remove(model.GetCurrentPw());
				}
			}
		});
		// Context Menu URL öffnen
		view.cmiOpenAdr.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openCurrentUrl();
			}
		});

		serviceLocator.getLogger().info("Application controller initialized");
	}

	public void addPassword() {
		Password pw = new Password();
		pw.setAdresse(view.txtAdress.getText());
		pw.setBenutzername(view.txtUserName.getText());
		pw.setPasswort(view.txtPassword.getText());
		pw.setBemerkung(view.txtRemark.getText());
		this.model.AddPassword(pw);
		view.txtAdress.clear();
		view.txtUserName.clear();
		view.txtPassword.clear();
		view.txtRemark.clear();
		view.txtAdress.requestFocus();
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
		if (model.GetCurrentPw() != null) {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(model.GetCurrentPw().getPasswort());
			clipboard.setContent(content);
		}
	}

	private void copyUsernameToClipboard() {
		if (model.GetCurrentPw() != null) {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(model.GetCurrentPw().getBenutzername());
			clipboard.setContent(content);
		}
	}

	private void openCurrentUrl() {
		if (model.GetCurrentPw() != null) {
			try {
				Desktop.getDesktop().browse(
						new URI(model.GetCurrentPw().getAdresse()));
			} catch (Exception e) {
				this.serviceLocator.getLogger().warning(
						e.toString() + " - " + e.getStackTrace().toString());
				// DialogHelper.ShowWarningDialog(primaryStage, "Fehler",
				// "Es können nur korrekte Internetadressen geöffnet werden");
			}
		}
	}
}
