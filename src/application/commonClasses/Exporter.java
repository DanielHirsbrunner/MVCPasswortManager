package application.commonClasses;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import application.ServiceLocator;
import javafx.stage.FileChooser;
import javafx.stage.Window;
/**
 * Der Exporter ist eine Hilfklasse welche ein CSV export sowie ein XML Import / Export zur verfügung stellt.
 * Der XML Export wird zum erstellen der nicht verschlüsselten Datensicherung verwendet. 
 *  
 * @author Daniel
 *
 */
public class Exporter {
	/**
	 * CSV Export im Format:
	 * | Adresse | Benutzername | Passwort | Bemerkung |
	 * @param stage Stage als Owner für den File Chooser
	 * @param pws zu exportierende Passwörter
	 */
	public static void exportToCsvFile(Window stage, Password[] pws) {
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sicherung speichern unter");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fileChooser.setInitialFileName("Passwoerter"+date+".csv");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
			try (FileWriter writer = new FileWriter(file.getAbsolutePath());)
			{
			    writer.append("Adresse");
			    writer.append(';');
			    writer.append("Benutzername");
			    writer.append(';');
			    writer.append("Passwort");
			    writer.append(';');
			    writer.append("Bemerkung");
			    writer.append(';');
			    writer.append('\n');
				for (Password pw : pws) {
				    writer.append(pw.getAdresse());
				    writer.append(';');
				    writer.append(pw.getBenutzername());
				    writer.append(';');
				    writer.append(pw.getPasswort());
				    writer.append(';');
				    writer.append(pw.getBemerkung());
				    writer.append(';');
				    writer.append('\n');
				}
			    writer.flush();
			    writer.close();
			    Desktop.getDesktop().open(file);
			}
			catch(IOException e)
			{
				sl.getLogger().warning(e.toString() + " - " + e.getStackTrace().toString());
			}
        }
	}
	
	/**
	 *  XML Export zum erstellen der Datensicherung
	 * @param stage aktueller Owner für den FileChooser
	 * @param pws zu Exportierende Passwörter
	 */
	public static void exportToXmlFile(Window stage, Password[] pws) {
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sicherung speichern unter");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML", "*.xml"));
        fileChooser.setInitialFileName("backup"+date+".xml");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
	        try {
				XmlHelper.write(pws, file.getAbsolutePath());
				//DialogHelper.ShowInformationDialog(stage, "Sicherung erstellt", "Die Datensicherung wurde erfolgreich erstellt");
			} catch (Exception e) {
				sl.getLogger().warning(e.toString() + " - " + e.getStackTrace().toString());
				//DialogHelper.ShowWarningDialog(stage, "Fehler aufgetreten", "Die Datensicherung konnte nicht erstellt werden.\nPruefen Sie die schreibberechtigung fuer das gewaehlte Verzeichnis");
			}
        }
	}
	
	/**
	 *  XML Import um Datensicherungen zurück zu lesen
	 *  
	 * @param stage aktueller Owner für den FileChooser
	 * @return Array mit den eingelessenen Passwörter
	 */
	public static Password[] importFromXmlFile(Window stage) {
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		FileChooser fileChooser = new FileChooser();
		
        fileChooser.setTitle(sl.getTranslator().getString(LangText.ExporterChooseBackup));
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
            );            
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML", "*.xml")
            );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
	        try {
				Password[] pws = XmlHelper.read(file.getAbsolutePath());
				//DialogHelper.ShowInformationDialog(stage, "Sicherung wiederherstellen", "Die Datensicherung wurde erfolgreich importiert");
				return pws;
				
			} catch (Exception e) {
				sl.getLogger().warning(e.toString() + " - " + e.getStackTrace().toString());
				//DialogHelper.ShowWarningDialog(stage, "Fehler aufgetreten", "Die Datensicherung konnte nicht zurueckgelesen werden.\nPruefen Sie das Format der XML Datei");
			}
        }
        return new Password[0];
	}

}
