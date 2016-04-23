package application.commonClasses;

/**
 * Enumeration mit den Sprachresourcen
 * 
 * @author Daniel
 */
public enum LangText {
	Name ("program.name"),
	Title("program.title"),
	MenuFile("program.menu.file"),
	MenuFileBackup("program.menu.file.backup"),
	MenuFileRestore("program.menu.file.restore"),
	MenuFileExport("program.menu.file.export"),
	MenuFileExit("program.menu.file.exit"),
	MenuLanguage("program.menu.language"),
	MenuHelp("program.menu.help"),
	MenuHelpAbout("program.menu.help.about"),
	TableColAdress("program.table.colAdress"),
	TableColUserName("program.table.colUserName"),
	TableColRemark("program.table.colRemark"),
	TableColPassword("program.table.colPassword"),
	ButtonAdd("program.btnAdd"),
	ContextMenuOpenAdr("program.contextmenu.openadr"),
	ContextMenuCopyUser("program.contextmenu.copyuser"),
	ContextMenuCopyPassword("program.contextmenu.copypassword"),
	ContextMenuDeleteEntry("program.contextmenu.delete");
	
	String resourceName;
	
	LangText(String rn) {
		this.resourceName = rn;
	}
	
	public String toString(){
		return this.resourceName;
	}
}