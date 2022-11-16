/*
 * Created on Jan 27, 2011
 *
 */
package com.baypackets.ase.util;

import java.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * An utility class for Encrypting and Decrypting the data.
 * 
 * @author Puneet
 */
public class DataEncryption {

	//private static Logger logger = Logger.getLogger(DataEncryption.class);
	private static Cipher cipher = null;
	private static String KEY_FILE = "passwordEncryptionKey";
	private static byte[] raw = getDESKey();
	private static final String DES = "DES";
	private static SecretKeySpec sks = new SecretKeySpec(raw, DES);
	private static BASE64Encoder base64Encoder = new BASE64Encoder();
	private static BASE64Decoder base64Decoder = new BASE64Decoder();
	/*
	 * Algo
	 */
	
	static {
		try {
			cipher = Cipher.getInstance(DES);
		} catch (NoSuchPaddingException e) {
			System.out.println(e.getMessage()+ e);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage()+ e);
		}
	}

	public static String encrypt(String data) {
		// Generates the KeySpec object
		byte[] encrypted = null;
		String encryptStr = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());
			encrypted = cipher.doFinal(data.getBytes());
			encryptStr = base64Encoder.encode(encrypted);
		}catch (InvalidKeyException e) {
			System.out.println(e.getMessage()+ e);
		} catch (BadPaddingException e) {
			System.out.println(e.getMessage()+ e);
		} catch (IllegalBlockSizeException e) {
			System.out.println(e.getMessage()+ e);
		} 
		return encryptStr;
	}
	
	private static String fixMessage(String data){
		System.out.println("Inside fixMessage with "+data);
		String newData = null;
		if(data.length() < 12 && !data.endsWith("=")){
			newData=data+"=";	
		}else{
			newData=data;
		}
		return newData;
	}

	public static String decrypt(String data) {
		// Generates the KeySpec object
		byte[] decrypted = null;
		String decryptStr=null;
		try {
			data=fixMessage(data);	
			System.out.println("Fixed data is "+data);
			byte[] EncryptByte = base64Decoder.decodeBuffer(data);
			cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec());
			decrypted = cipher.doFinal(EncryptByte);
			decryptStr = new String(decrypted);	
		} catch (InvalidKeyException e) {
			System.out.println(e.getMessage()+ e);
		} catch (BadPaddingException e) {
			System.out.println(e.getMessage()+ e);
		} catch (IllegalBlockSizeException e) {
			System.out.println(e.getMessage()+ e);
		}catch (IOException e) {
			System.out.println(e.getMessage()+ e);
		}
		return decryptStr;
	}

	private byte[] getDESDynamicKey() {

		KeyGenerator kgen = null;

		try {
			// Get the KeyGenerator
			kgen = KeyGenerator.getInstance(DES);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage()+ e);
		}

		kgen.init(128); // 192 and 256 bits may not be available

		// Generate the secret key specs.
		SecretKey key = kgen.generateKey();

		byte[] raw = null;

		if (key != null) {
			raw = key.getEncoded();
		}
		return raw;
	}

	private static void writeDESKey() {
		KeyGenerator kgen = null;

		try {
			// Get the KeyGenerator
			kgen = KeyGenerator.getInstance(DES);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage()+ e);
		}

		kgen.init(56); // 192 and 256 bits may not be available

		// Generate the secret key specs.
		SecretKey key = kgen.generateKey();

		Object object = key;
		// Serialize to a file

		try {
			//File file = new File(DataEncryption.class.getClassLoader().getResource(KEY_FILE).getFile());
			File file = new File("/vob/Sipservlet/src/com/baypackets/ase/util/passwordEncryptionKey");
			ObjectOutput out = new ObjectOutputStream(
					new FileOutputStream(file));
			out.writeObject(object);
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage()+ e);
		}
	}

	private static byte[] getDESKey() {
		SecretKey key = null;
		try {
			ObjectInputStream in = new ObjectInputStream(DataEncryption.class.getResourceAsStream(KEY_FILE)); // Deserialize
			key = (SecretKey) in.readObject();
			in.close();
		} catch (IOException e) {
			System.out.println(e.getMessage()+ e);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage()+ e);
		}
		byte[] raw = null;

		if (key != null) {
			raw = key.getEncoded();
		}
		return raw;
	}

	private static SecretKeySpec getSecretKeySpec() {
		return sks;
	}

	public static void main(String[] args) {
		//writeDESKey();
		String data = DataEncryption.encrypt("abc123");
		data = "telnet.root.password=" + data ;
		System.out.println("Encrypted Data "+data);
		//String newData = DataEncryption.decrypt(data);
		//System.out.println("Decrypted password " + newData);
		//System.out.println("Decrypted password  in byte form " + newData.getBytes());
		try {
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/vob/Sipservlet/src/setup/conf/ase.properties",true)));
	out.write(data,0,data.length());
	out.flush();
			//File file = new File("/vob/Sipservlet/src/binary.txt");
			//ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file));
			//out.writeObject(data.getBytes());
			out.close();
			decode();		
		} catch (IOException e) {
			System.out.println(e.getMessage()+ e);
		}
	}
	
	private static void decode(){
		try {
			String str=null;
			StringBuffer strContent = new StringBuffer("");
			File file = new File("/vob/Sipservlet/src/binary.txt");
			BufferedReader br = new BufferedReader(new FileReader("/vob/Sipservlet/src/binary.txt"));
			FileInputStream fin = new FileInputStream(file);
			str = br.readLine();
			System.out.println("raed data "+str);
		String newData = DataEncryption.decrypt(str.substring(6));
		System.out.println("Decrypted password " + newData);
		System.out.println("Decrypted password  in byte form " + newData.getBytes());
		} catch (IOException e) {
			System.out.println(e.getMessage()+ e);
		}
	}
}
