package dk.meem.swing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

public class Serialiser {

    public void storeData(char[] password, String serialisedFilename, TableData data) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException {
    	Cipher cipher = getCipher(password, Cipher.ENCRYPT_MODE);
		SealedObject sobj = new SealedObject(data, cipher);

		FileOutputStream os = new FileOutputStream(serialisedFilename);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(sobj);
		oos.close();
    }
    
    public TableData restoreData(char[] password, String serialisedFilename) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		if (Files.isReadable(Paths.get(serialisedFilename))) {
			Cipher cipher = getCipher(password, Cipher.DECRYPT_MODE);

			FileInputStream is = new FileInputStream(serialisedFilename);
			ObjectInputStream ois = new ObjectInputStream(is);
			SealedObject sobj = (SealedObject) ois.readObject();
			ois.close();

			return (TableData)sobj.getObject(cipher);
		} else {
			JOptionPane.showMessageDialog(null, "Password file (" + serialisedFilename + ") not found.");
			return null;
		}
    }


	/* This is my own suggestion.
	 * I'm not sure how to handle salt in this case.
	 * Also, how do I handle IV - is that handled by SealedObject?
	 */
	private Cipher getCipher(char[] password, int mode) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		int iterations = 655536;
		int keylength = 256;
		byte salt[] = {-12, -14, 5, 117, -110, 68, 106, 84, -69, 101, 63, -50, 84, 93, 67, 33, 85, 47, -83, -62, -10, -32, -38, -29, 38, 51, 105, 101, -109, -116, -71, 124};

		/*
		byte salt2[] = new byte[32];
		SecureRandom random = new SecureRandom();
	    random.nextBytes(salt2);
    
	    for (byte b : salt2) { System.out.print(b + ", "); }
	    System.out.println();
	    */
	    
	    /* Derive the key, given password and salt. */
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password, salt, iterations, keylength);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");

		Cipher ciph = Cipher.getInstance("AES");
		ciph.init(mode, key);
		
		return ciph;
	}

    /* This is the first suggestion, using Blowfish. 
     * Not used.
     */
	private Cipher getCipher_v1(int mode) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		Cipher ciph = Cipher.getInstance("Blowfish");
		SecretKey key = new SecretKeySpec( new byte[] { 0x09, 0x04, 0x06, 0x02, 0x03, 0x08, 0x06, 0x07 }, "Blowfish" );
		ciph.init(mode, key);
		
		return ciph;
	}


}
