package application.commonClasses;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import application.ServiceLocator;
import application.commonClasses.Configuration.Option;

/**
 * Der XML Helper kümmert sich ums laden und zurückschreiben der Passwörter in die XML Datei.
 * 
 * @author Daniel
 *
 */
public class XmlHelper {

	/**
	 * Exportiert die übergebenen Passwörter in eine unverschlüsselte XML Datei
	 * @param pws Array mit den Passwörtern welche exportiert werden sollen
	 * @param filename Zieldateiname
	 * @throws Exception Bsp. Zugriff nicht erlaubt
	 */
	public static void write(Password[] pws, String filename) throws Exception {
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
		encoder.writeObject(pws);
		encoder.close();
	}

	/**
	 * Importiert Passwörter aus einer nicht verschlüsselten Datei
	 * @param filename zu importierende Datei
	 * @return Array mit den Passwörtern
	 * @throws Exception Bsp. FileNotFound oder Zugriff nicht erlaubt
	 */
	public static Password[] read(String filename) throws Exception {
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
		Password[] o = (Password[]) decoder.readObject();
		decoder.close();
		return o;
	}

	/**
	 * Exportiert die übergebenen Passwörter in eine verschlüsselte XML Datei
	 * 
	 * @param pws Array mit den Passwörtern welche exportiert werden sollen
	 * @param filename Zieldateiname
	 * @throws Exception Bsp. Zugriff nicht erlaubt
	 */
	public static void writeEncrypted(Password[] pws, String filename) throws Exception {
		String password = ServiceLocator.getServiceLocator().getConfiguration().getOption(Option.DBEncryptionKey);
		// XML in ByteArray erstellen lassen
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(stream));
		encoder.writeObject(pws);
		encoder.close();
		// XML verschluesseln
		Cipher cipher = Cipher.getInstance("DES");
		SecretKey key = new SecretKeySpec(password.getBytes(), "DES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		CipherOutputStream out = new CipherOutputStream(new FileOutputStream(filename), cipher);
		out.write(stream.toByteArray());
		out.close();
		stream.close();
	}


	/**
	 * Importiert Passwörter aus einer verschlüsselten Datei
	 * @param filename zu importierende Datei
	 * @return Array mit den Passwörtern
	 * @throws Exception Bsp. FileNotFound oder Zugriff nicht erlaubt
	 */
	public static Password[] readEncrypted(String filename) throws Exception {
		String password = ServiceLocator.getServiceLocator().getConfiguration().getOption(Option.DBEncryptionKey);
		// Verschluesseltes File lesen und in Memorystream laden
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Cipher cipher = Cipher.getInstance("DES");
		SecretKey key = new SecretKeySpec(password.getBytes(), "DES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		CipherInputStream in = new CipherInputStream(new FileInputStream(filename), cipher);
		byte[] byteBuffer = new byte[1024];
		for (int n; (n = in.read(byteBuffer)) != -1; stream.write(byteBuffer, 0, n));
		in.close();
		// Entschluesselter Steam deserialisieren
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(stream.toByteArray())));
		Password[] o = (Password[]) decoder.readObject();
		decoder.close();
		return o;
	}
}