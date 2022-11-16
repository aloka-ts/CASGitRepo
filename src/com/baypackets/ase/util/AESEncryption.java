/*****
Copyright (c) 2016 Agnity, Inc. All rights reserved.  

This is proprietary source code of Agnity, Inc.  

Agnity, Inc. retains all intellectual property rights associated  with this source code. Use is subject to license terms.

This source code contains trade secrets owned by Agnity, Inc.

Confidentiality of this computer program must be maintained at all times, unless explicitly authorized by Agnity, Inc.
 *****/

package com.baypackets.ase.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;


public class AESEncryption {

	private static Cipher encCipher = null;
	private static Cipher decCipher = null;
	private static int KEY_OFFSET = 0;
	private static int KEY_LENGTH = 16;
	private static int KEY1_LENGTH = 5;
	private static int KEY3_LENGTH = 9;
	private static int KEY_LIMIT = 70;
	private static Logger _logger = Logger.getLogger(AESEncryption.class);
	private final static String path = Constants.ASE_HOME+"/scripts/encryptor/codeGen "; ;
	private static String[] dictionary1 = {
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
		"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
		"U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d",
		"e", "!", "@", "#", "$", "%", "^", "&", "*", "f",
		"g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
		"q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
		"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
	};
	private static String[] dictionary3 = {
		"9", "8", "7", "6", "5", "4", "3", "2", "1", "0",
		"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
		"k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
		"u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
		"E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
		"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
		"Y", "Z", "!", "@", "#", "$", "%", "^", "&", "*"
	};
	
	private static String sKey = generateKey();
	private static SecretKeySpec sKeySpec = new SecretKeySpec(sKey.getBytes(), KEY_OFFSET, KEY_LENGTH, "AES");

	/**
	 * Private c'tor for singleton implementation
	 */
	private AESEncryption() {}

	/**
	 * API to get key based on dictionary.
	 * @return
	 */
	private static String genKey1() {
		String key = "";
		int cnt = KEY1_LENGTH;
		int index = 1;

		while(cnt > 0){
			int val = (((cnt*index++)*(KEY1_LENGTH - cnt))%KEY_LIMIT);
			key += dictionary1[val];
			cnt --;
		}
		
		return key;
	}

	/**
	 * API to get key from external utility.
	 * @return
	 */
	private static String genKey2() {
		String key = "";
		ProcessBuilder pb = null;
		Process process = null;

		try {
			String cmd = path + genKey1();
			pb = new ProcessBuilder("bash", "-c", cmd);
			pb.redirectErrorStream(true);
			process = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			key = br.readLine();
			process.waitFor();
		} catch (Exception e) {
			throw new RuntimeException("Security breach!!!");
		} finally {
			if (process != null) {
				process.destroy();
			}
		}

		return key;
	}

	/**
	 * API to get key based on system information.
	 * @return
	 */
	private static String genKey3() {
		String key = "";
		int cnt = KEY3_LENGTH;
		int index = 1;

		while(cnt > 0){
			int val = (((cnt*index++)*(KEY3_LENGTH - cnt))%KEY_LIMIT);
			key += dictionary3[val];
			cnt --;
		}

		return key;
	}

	/**
	 * API to generate key based on dictionary, system info and fixed values.
	 * @return
	 */
	public static String generateKey() {
		return (genKey1() + genKey2() + genKey3());
	}

	/**
	 * API to get secret key.
	 * @return
	 */
	public static String getKey() {
		return sKey;
	}

	/**
	 * Method to set key for encryption/decryption
	 * @param key 128 bit secret key
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void initialize(String key) throws Exception {

		if (key == null || key.length() < KEY_LENGTH) {
			throw new RuntimeException("Minimum 128 bit key is required");
		}

		byte[] secretKey = key.getBytes();
		sKeySpec = new SecretKeySpec(secretKey, KEY_OFFSET, KEY_LENGTH, "AES");

		encCipher = Cipher.getInstance("AES");
		encCipher.init(Cipher.ENCRYPT_MODE, sKeySpec);

		decCipher = Cipher.getInstance("AES");
		decCipher.init(Cipher.DECRYPT_MODE, sKeySpec);

		sKey = key;

		// System.out.println("Cipher initialized successfully");
	}

	/**
	 * Method to encrypt input string using AES encryption
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String unEncrypted) {
		String encrypted = "";
		String key = AESEncryption.generateKey();
		if (key == null || key.length() < KEY_LENGTH) {
			throw new RuntimeException("Minimum 128 bit key is required");
		}
		
		try {
			encCipher = Cipher.getInstance("AES");
			encCipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
			byte[] enecryptedKey = encCipher.doFinal(unEncrypted.getBytes());
			encrypted = asHex(enecryptedKey).toUpperCase();
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

		return encrypted;
	}

	/**
	 * Method to decrypt input string using AES encryption
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encrypted) {
		String decrypted = "";
		try {
			
			decCipher = Cipher.getInstance("AES");
			decCipher.init(Cipher.DECRYPT_MODE, sKeySpec);
			byte[] decryptedKey = decCipher.doFinal(hexStringToByteArray(encrypted));
			decrypted = new String(decryptedKey);
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

		return decrypted;
	}

	/**
	 * Helper method to convert byte array as Hex string
	 * @param buf
	 * @return
	 */
	private static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	/**
	 * Helper method to convert Hex string to byte array
	 * @param s
	 * @return
	 */
	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte)v;
		}
		return b;
	}

	/**
	 * Main method to get encrypted key.
	 * @param args
	 */
	public static void main(String args[]) {
		if (args.length != 2) {
			System.out.println("Invalid arguments.");
			System.exit(1);
		}

		int choice = -1;
		try {
			choice = Integer.parseInt(args[0]);
		} catch (Exception e) {
			choice = -1;
		}

		if (!(choice == 0 || choice == 1 || choice == 2)) {
			System.out.println("Invalid arguments.");
			System.exit(2);
		}

		try {
			AESEncryption.initialize(AESEncryption.generateKey());

			String result = "";
			switch(choice) {
			case 0: {
				result = AESEncryption.getKey();
				break;
			}
			case 1: {
				result = AESEncryption.encrypt(args[1]);
				break;
			}
			case 2: {
				result = AESEncryption.decrypt(args[1]);
				break;
			}
			}

			System.out.println(result);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}


} // end-of-class