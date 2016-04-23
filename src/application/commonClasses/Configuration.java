package application.commonClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import application.ServiceLocator;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * This class provides basic functionality for loading and saving program
 * options. Default options may be delivered with the application; local options
 * (changed by the user) are saved to a file. Program constants can be defined
 * by defining options that the user has no way to change.
 * 
 * @author Brad Richards
 */
public class Configuration {
	
	public static enum Option {
		Language ("Language"),
		LogFileName("LogFileName"),
		DBFileName("DbFileName"),
		DBEncryptionKey("encriptionKey");
		
		String optionName;
		
		Option(String name) {
			this.optionName = name;
		}
		
		public String ToString(){
			return this.optionName;
		}
	}
	
    ServiceLocator sl = ServiceLocator.getServiceLocator();
    Logger logger = sl.getLogger();
	private String appDataDirectory;

    private Properties defaultOptions;
    private Properties localOptions;

    public Configuration() {
    	// Appdata Pfad pruefen und falls noch nicht vorhanden Ordner anlegen (Default Pfad fuer DB)
		Path appDataDirectoryPath = Paths.get(System.getenv("APPDATA"), "EasyPasswortManager");
		appDataDirectory = appDataDirectoryPath.toString();
		File f = new File(appDataDirectory);
		if (!f.exists()) {
			f.mkdir();		
		}
        // Load default properties from wherever the code is
        defaultOptions = new Properties();
        String defaultFilename = sl.getAPP_NAME() + "_defaults.cfg";
        InputStream inStream = sl.getAPP_CLASS().getResourceAsStream(defaultFilename);
        try {
            defaultOptions.load(inStream);
            logger.config("Default configuration file found");
        } catch (Exception e) {
            logger.warning("No default configuration file found: " + defaultFilename);
        } finally {
            try {
                inStream.close();
            } catch (Exception ignore) {
            }
        }

        // Define locally-saved properties; link to the default values
        localOptions = new Properties(defaultOptions);

        // Load the local configuration file, if it exists.
        try {
            inStream = new FileInputStream(sl.getAPP_NAME() + ".cfg");
            localOptions.load(inStream);
        } catch (FileNotFoundException e) { // from opening the properties file
            logger.config("No local configuration file found");
        } catch (IOException e) { // from loading the properties
            logger.warning("Error reading local options file: " + e.toString());
        } finally {
            try {
                inStream.close();
            } catch (Exception ignore) {
            }
        }
        
        for (Enumeration<Object> i = localOptions.keys(); i.hasMoreElements();) {
            String key = (String) i.nextElement();
            logger.config("Option: " + key + " = " + localOptions.getProperty(key));
        }
    }
    
    public void save() {
        FileOutputStream propFile = null;
        try {
            propFile = new FileOutputStream(sl.getAPP_NAME() + ".cfg");
            localOptions.store(propFile, null);
            logger.config("Local configuration file saved");
        } catch (Exception e) {
            logger.warning("Unable to save local options: " + e.toString());
        } finally {
            if (propFile != null) {
                try {
                    propFile.close();
                } catch (Exception e) {
                }
            }
        }
    }
    
    public String getOption(Option opt) {
        return localOptions.getProperty(opt.ToString());
    }
    
    public void setLocalOption(Option opt, String value) {
        localOptions.setProperty(opt.ToString(), value);
    }
}
