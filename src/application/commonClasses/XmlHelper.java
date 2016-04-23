package application.commonClasses;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import application.ServiceLocator;
import application.commonClasses.Configuration.Option;

public class XmlHelper {

	public static void write(Password[] pws, String filename) throws Exception {
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
		encoder.writeObject(pws);
		encoder.close();
	}

	public static Password[] read(String filename) throws Exception {
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
		Password[] o = (Password[]) decoder.readObject();
		decoder.close();
		return o;
	}

	public static void writeEncrypted(Password[] pws, String filename) throws Exception {
		String password = ServiceLocator.getServiceLocator().getConfiguration().getOption(Option.DBEncryptionKey);
		// XML in ByteArray erstellen lassen
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(stream));
		encoder.writeObject(pws);
		encoder.close();
		// XML verschlüsseln
		CipherOutputStream out;
		Cipher cipher;
		SecretKey key;
		cipher = Cipher.getInstance("DES");
		key = new SecretKeySpec(password.getBytes(), "DES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		out = new CipherOutputStream(new FileOutputStream(filename), cipher);
		out.write(stream.toByteArray());
		out.close();
		stream.close();
	}

	public static Password[] readEncrypted(String filename) throws Exception {
		String password = ServiceLocator.getServiceLocator().getConfiguration().getOption(Option.DBEncryptionKey);
		// Verschlüsseltes File lesen und in Memorystream laden
		CipherInputStream in;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Cipher cipher;
		SecretKey key;
		byte[] byteBuffer;
		cipher = Cipher.getInstance("DES");
		key = new SecretKeySpec(password.getBytes(), "DES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		in = new CipherInputStream(new FileInputStream(filename), cipher);
		byteBuffer = new byte[1024];
		for (int n; (n = in.read(byteBuffer)) != -1; stream.write(byteBuffer, 0, n))
			;
		in.close();
		// Entschlüsselter Steam deserialisieren
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(stream.toByteArray())));
		Password[] o = (Password[]) decoder.readObject();
		decoder.close();
		return o;
	}

	// Methoden zum Verschlüsseln:
	public void encryptFile(String originalFile, String encryptedFile) throws Exception {
		String password = ServiceLocator.getServiceLocator().getConfiguration().getOption(Option.DBEncryptionKey);
		CipherOutputStream out;
		InputStream in;
		Cipher cipher;
		SecretKey key;
		byte[] byteBuffer;
		cipher = Cipher.getInstance("DES");
		key = new SecretKeySpec(password.getBytes(), "DES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		in = new FileInputStream(originalFile);
		out = new CipherOutputStream(new FileOutputStream(encryptedFile), cipher);
		byteBuffer = new byte[1024];
		for (int n; (n = in.read(byteBuffer)) != -1; out.write(byteBuffer, 0, n));
		in.close();
		out.close();
	}

	public void decryptFile(String encryptedFile, String decryptedFile) throws Exception {
		String password = ServiceLocator.getServiceLocator().getConfiguration().getOption(Option.DBEncryptionKey);
		CipherInputStream in;
		OutputStream out;
		Cipher cipher;
		SecretKey key;
		byte[] byteBuffer;
		cipher = Cipher.getInstance("DES");
		key = new SecretKeySpec(password.getBytes(), "DES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		in = new CipherInputStream(new FileInputStream(encryptedFile), cipher);
		out = new FileOutputStream(decryptedFile);
		byteBuffer = new byte[1024];
		for (int n; (n = in.read(byteBuffer)) != -1; out.write(byteBuffer, 0, n));
		in.close();
		out.close();
	}
}