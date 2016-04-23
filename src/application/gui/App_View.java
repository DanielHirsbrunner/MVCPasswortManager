package application.gui;

import java.util.Locale;
import java.util.logging.Logger;

import application.abstractClasses.View;
import application.commonClasses.LangText;
import application.commonClasses.Password;
import application.commonClasses.Translator;
import application.ServiceLocator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Daniel Hirsbrunner
 */
public class App_View extends View<App_Model> {
	Menu menuFile;
	Menu menuLanguage;
	Menu menuHelp;
	ContextMenu contextMenu;

	MenuItem menuFileBackup;
	MenuItem menuFileRestore;
	MenuItem menuFileExport;
	MenuItem menuFileExit;

	MenuItem cmiOpenAdr;
	MenuItem cmiCopyUser;
	MenuItem cmiCopyPW;
	MenuItem cmiDelete;
	MenuItem cmiEdit;

	Label lblTitel;

	TableView<Password> table = new TableView<Password>();
	TableColumn<Password, String> colAdress;
	TableColumn<Password, String> colUserName;
	TableColumn<Password, String> colRemark;
	TableColumn<Password, String> colPassword;
	TextField txtAdress;
	TextField txtUserName;
	TextField txtRemark;
	TextField txtPassword;
	Button btnAdd;

	ServiceLocator sl;
	Logger logger;
	Translator translator;

	public App_View(Stage stage, App_Model model) {
		// setzt Stage, das Model und ruft initView und createGUI auf
		super(stage, model);

		this.logger.info("Application view initialized");
	}

	@Override
	protected void initView() {
		// Instanzvariablen Initialisieren
		this.sl = ServiceLocator.getServiceLocator();
		this.logger = sl.getLogger();
		this.translator = sl.getTranslator();
	}

	@Override
	protected Scene createGUI() {
		// ------------------------------------------------------
		// ----------- Titel und Logo setzen --------------------
		// ------------------------------------------------------
		this.stage.setTitle(this.translator.getString(LangText.Name));
		this.stage.getIcons().add(new Image("img/password.png"));
		// ------------------------------------------------------
		// ----------- Menubar und Titel ------------------------
		// ------------------------------------------------------
		MenuBar menuBar = new MenuBar();
		this.menuFile = new Menu(this.translator.getString(LangText.MenuFile));
		this.menuFileBackup = new MenuItem(this.translator.getString(LangText.MenuFileBackup));
		this.menuFileBackup.setGraphic(new ImageView(new Image("img/backup.png")));
		this.menuFileRestore = new MenuItem(this.translator.getString(LangText.MenuFileRestore));
		this.menuFileRestore.setGraphic(new ImageView(new Image("img/restore.png")));
		this.menuFileExport = new MenuItem(this.translator.getString(LangText.MenuFileExport));
		this.menuFileExport.setGraphic(new ImageView(new Image("img/export.png")));
		this.menuFileExit = new MenuItem(this.translator.getString(LangText.MenuFileExit));
		this.menuFileExit.setGraphic(new ImageView(new Image("img/close.png")));

		this.menuFile.getItems().addAll(menuFileBackup, menuFileRestore, menuFileExport, menuFileExit);

		this.menuLanguage = new Menu(this.translator.getString(LangText.MenuLanguage));

		for (Locale locale : sl.getLocales()) {
			MenuItem language = new MenuItem(locale.getLanguage());
			this.menuLanguage.getItems().add(language);
			language.setOnAction(event -> {
				sl.setTranslator(new Translator(locale.getLanguage()));
				translator = sl.getTranslator();
				updateTexts();
			});
		}

		this.menuHelp = new Menu(this.translator.getString(LangText.MenuHelp));
		menuBar.getMenus().addAll(menuFile, menuLanguage, menuHelp);

		BorderPane root = new BorderPane();
		VBox top = new VBox();

		this.lblTitel = new Label(this.translator.getString(LangText.Title));
		this.lblTitel.getStyleClass().add("titelLabel");
		this.lblTitel.setMinHeight(40);

		top.getChildren().addAll(menuBar, lblTitel);

		// --------------------------------------------
		// ----------- Tabelle ------------------------
		// --------------------------------------------
		// Referenz: http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
		this.table = new TableView<Password>();
		this.table.setEditable(true);

		this.colAdress = new TableColumn<Password, String>(this.translator.getString(LangText.TableColAdress));
		this.colAdress.setCellValueFactory(new PropertyValueFactory<Password, String>("adresse"));
		this.colAdress.prefWidthProperty().bind(table.widthProperty().divide(5));

		this.colUserName = new TableColumn<Password, String>(
				this.translator.getString(LangText.TableColUserName));
		this.colUserName.setCellValueFactory(new PropertyValueFactory<Password, String>("benutzername"));
		this.colUserName.prefWidthProperty().bind(table.widthProperty().divide(6));

		this.colPassword = new TableColumn<Password, String>(
				this.translator.getString(LangText.TableColPassword));
		this.colPassword.setCellValueFactory(new PropertyValueFactory<Password, String>("passwortFake"));
		this.colPassword.prefWidthProperty().bind(table.widthProperty().divide(6));

		this.colRemark = new TableColumn<Password, String>(this.translator.getString(LangText.TableColRemark));
		this.colRemark.setCellValueFactory(new PropertyValueFactory<Password, String>("bemerkung"));
		this.colRemark.prefWidthProperty().bind(table.widthProperty().divide(3));

		// --------------------------------------------------------------------------------------------------------------------------------------
		// ----------------- Sollten im Controller sein, aber dann funktionierts nicht, wiso weiss nur der java Gott ----------------------------
		// --------------------------------------------------------------------------------------------------------------------------------------
		// Falls eine andere Row ausgewaehlt wurde dem Model mitteilen
		this.table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Password>() {
			@Override
			public void changed(ObservableValue<? extends Password> ov, Password oldPassword, Password newPassword) {
				model.SetCurrentPw(newPassword);
				// chancelEdit Methode
				clearFields();
			}
		});
		// Doppelklick auf Auflistung -> Eintrag editieren
		this.table.setRowFactory( tv -> {
		    TableRow<Password> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	
		        	// editPassword Methode
		        		if (this.model.GetCurrentPw() != null) {
		                    this.model.SetIsInEdit(true);
		        			this.txtAdress.setText(model.GetCurrentPw().getAdresse());
		        			this.txtUserName.setText(model.GetCurrentPw().getBenutzername());
		        			this.txtPassword.setText(model.GetCurrentPw().getPasswort());
		        			this.txtRemark.setText(model.GetCurrentPw().getBemerkung());
		        			this.setDefaultFocus();
		        		}
		        }
		    });
		    return row ;
		});
		// ---------------------------------------------------------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------------------
		// ---------------------------------------------------------------------------------------------------------------------------------------
		
		this.table.getColumns().addAll(this.colAdress, this.colUserName, this.colPassword, this.colRemark);

		// --------------------------------------------
		// ----------- Context menu -------------------
		// --------------------------------------------
		contextMenu = new ContextMenu();
		// Adresse oeffnen
		cmiOpenAdr = new MenuItem(this.translator.getString(LangText.ContextMenuOpenAdr));
		cmiOpenAdr.setGraphic(new ImageView(new Image("img/openAdress.png")));
		// Benutzername kopieren
		cmiCopyUser = new MenuItem(this.translator.getString(LangText.ContextMenuCopyUser));
		cmiCopyUser.setGraphic(new ImageView(new Image("img/copy.png")));
		// Passwort kopieren
		cmiCopyPW = new MenuItem(this.translator.getString(LangText.ContextMenuCopyPassword));
		cmiCopyPW.setGraphic(new ImageView(new Image("img/copy.png")));
		// Eintrag Loeschen
		cmiDelete = new MenuItem(this.translator.getString(LangText.ContextMenuDeleteEntry));
		cmiDelete.setGraphic(new ImageView(new Image("img/delete.png")));
		// Eintrag Bearbeiten
		cmiEdit = new MenuItem(this.translator.getString(LangText.ContextMenuEditEntry));
		cmiEdit.setGraphic(new ImageView(new Image("img/edit.png")));
		contextMenu.getItems().addAll(cmiOpenAdr, cmiCopyUser, cmiCopyPW, cmiEdit, cmiDelete);
		table.setContextMenu(contextMenu);

		table.setItems(this.model.GetPasswords());

		// ---------------------------------------------------
		// ----------- Eingabe Felder ------------------------
		// ---------------------------------------------------
		this.txtAdress = new TextField();
		this.txtUserName = new TextField();
		this.txtPassword = new TextField();
		this.txtRemark = new TextField();
		HBox hb = new HBox();
		this.txtAdress.setPromptText(this.translator.getString(LangText.TableColAdress));
		this.txtUserName.setPromptText(this.translator.getString(LangText.TableColUserName));
		this.txtPassword.setPromptText(this.translator.getString(LangText.TableColPassword));
		this.txtRemark.setPromptText(this.translator.getString(LangText.TableColRemark));

		this.txtAdress.prefWidthProperty().bind(this.colAdress.widthProperty());
		this.txtUserName.prefWidthProperty().bind(this.colUserName.widthProperty());
		this.txtPassword.prefWidthProperty().bind(this.colPassword.widthProperty());
		this.txtRemark.prefWidthProperty().bind(this.colRemark.widthProperty());

		this.btnAdd = new Button(this.translator.getString(LangText.ButtonAdd));

		// ---------------------------------------------------
		// ----------- Container anordnen ------------------------
		// ---------------------------------------------------
		hb.getChildren().addAll(this.txtAdress, this.txtUserName, this.txtPassword, this.txtRemark, btnAdd);

		root.setTop(top);
		root.setCenter(table);
		root.setBottom(hb);
		Scene scene = new Scene(root, 600, 600);
		scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
		return scene;
	}

	/**
	 * Aktualisiert die Sprachtexte auf allen GUI elementen
	 */
	protected void updateTexts() {
		// Titles
		this.stage.setTitle(this.translator.getString(LangText.Name));
		this.lblTitel.setText(this.translator.getString(LangText.Title));

		// menu entries
		this.menuFile.setText(this.translator.getString(LangText.MenuFile));
		this.menuLanguage.setText(this.translator.getString(LangText.MenuLanguage));
		this.menuHelp.setText(this.translator.getString(LangText.MenuHelp));

		this.menuFileBackup.setText(this.translator.getString(LangText.MenuFileBackup));
		this.menuFileRestore.setText(this.translator.getString(LangText.MenuFileRestore));
		this.menuFileExport.setText(this.translator.getString(LangText.MenuFileExport));
		this.menuFileExit.setText(this.translator.getString(LangText.MenuFileExit));

		// Context Menu
		cmiOpenAdr.setText(this.translator.getString(LangText.ContextMenuOpenAdr));
		cmiCopyUser.setText(this.translator.getString(LangText.ContextMenuCopyUser));
		cmiCopyPW.setText(this.translator.getString(LangText.ContextMenuCopyPassword));
		cmiDelete.setText(this.translator.getString(LangText.ContextMenuDeleteEntry));

		// Table
		this.colAdress.setText(this.translator.getString(LangText.TableColAdress));
		this.colUserName.setText(this.translator.getString(LangText.TableColUserName));
		this.colRemark.setText(this.translator.getString(LangText.TableColRemark));
		this.colPassword.setText(this.translator.getString(LangText.TableColPassword));

		// Input Fields
		this.txtAdress.setPromptText(this.translator.getString(LangText.TableColAdress));
		this.txtUserName.setPromptText(this.translator.getString(LangText.TableColUserName));
		this.txtPassword.setPromptText(this.translator.getString(LangText.TableColPassword));
		this.txtRemark.setPromptText(this.translator.getString(LangText.TableColRemark));
		this.btnAdd.setText(this.translator.getString(LangText.ButtonAdd));
	}
	
	protected void clearFields() {
		this.txtAdress.clear();
		this.txtUserName.clear();
		this.txtPassword.clear();
		this.txtRemark.clear();
	}
	
	protected void setDefaultFocus() {
		this.txtAdress.requestFocus();
	}
}