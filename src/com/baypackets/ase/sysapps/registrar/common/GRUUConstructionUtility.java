package com.baypackets.ase.sysapps.registrar.common;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.registrar.dao.BindingsDAO;
import com.baypackets.ase.sysapps.registrar.dao.DAOFactory;

import sun.misc.BASE64Encoder;
import sun.security.util.BitArray;

/**
 * This class is used to create temp and public GRUU
 */

public class GRUUConstructionUtility
{
	static Logger logger=Logger.getLogger(GRUUConstructionUtility.class);


	private BindingsDAO bindingsDAO = null;
	private DAOFactory daoFactory = null;

	// for keys 
	private  static SecretKeySpec skeySpecHma = null;
	private  static SecretKeySpec skeySpecAes = null;

	// counter 
	private static long I ; 

	private static BASE64Encoder base64Encoder = new BASE64Encoder();

	public void init(){
		logger.debug("Inside Construct gruu init()");
		ArrayList tempGruuLastRow = new ArrayList();
		daoFactory=DAOFactory.getInstance(); 
		try
		{
			bindingsDAO = daoFactory.getBindingsDAO(); 

			tempGruuLastRow = bindingsDAO.getMaxIdRow();

			if(tempGruuLastRow.size() == 0  ){
				TempGruuAlgoritm tempGruuAlgoritm = new TempGruuAlgoritm();
				skeySpecHma = tempGruuAlgoritm.createKey("HMACSHA256");
				skeySpecAes = tempGruuAlgoritm.createKey("AES");
				I = 0L;
			}else{
				I=(Long)tempGruuLastRow.get(0)+1;
				skeySpecHma=(SecretKeySpec)tempGruuLastRow.get(1);
				skeySpecAes=(SecretKeySpec)tempGruuLastRow.get(2);
			}
			logger.debug("Construct gruu init() called successfully ");
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
		}
	}


	/**
	 * This method creates a public GRUU 
	 *
	 * @return value of GRUU ( String )
	 */
	public String createPublicGruu(String AOR,String sipinstance){
		logger.debug("Inside createPublicGruu");
		if (AOR == null || sipinstance == null) {
			logger
					.debug("createPublicGruu: AOR or sipinstance is null so returning null");
			return null;
		}
		return AOR + ";gr=" + sipinstance;
	}

	/**
	 * This method creates a temp  GRUU 
	 *
	 * @return value of GRUU ( TempGruuAlgoritm )
	 */
	public TempGruuAlgoritm createTempGruu() throws Exception {
		TempGruuAlgoritm tempGruuAlgoritm = new TempGruuAlgoritm();
		tempGruuAlgoritm = tempGruuAlgoritm.createGruu();

		return tempGruuAlgoritm;
	}

	public class TempGruuAlgoritm {

		// 		   Algorithm As per RFC 5627 
		//		   1. M = D || I_i
		//		   2. E = AES-ECB-Encrypt(K_e, M)
		//		   3. A = HMAC-SHA256-80(K_a, E)

		private byte[] Ka;
		private byte[] Ke;
		private String tempGruu;
		private long counter;

		private SecretKeySpec createKey(String type) {
			SecretKeySpec skeySpec = null;
			try{
				KeyGenerator kgen = KeyGenerator.getInstance(type);
				kgen.init(128); // 192 and 256 bits may not be available

				// Generate the secret key specs.
				SecretKey skey = kgen.generateKey();
				byte[] raw = skey.getEncoded();
				skeySpec = new SecretKeySpec(raw, type);
			}catch (NoSuchAlgorithmException e) {
				logger.error("No Such Algorithm found for generating keys : " + e );
			}catch (Exception e) {
				logger.error("Error in generating Keys for Gruu : " + e );
			}
			return skeySpec;
		}


		private TempGruuAlgoritm createGruu() throws Exception {
			if(I == 140737488355328L){
				I = 0L;
				TempGruuAlgoritm tempGruuAlgoritm = new TempGruuAlgoritm();
				skeySpecHma = tempGruuAlgoritm.createKey("HMACSHA256");
				skeySpecAes = tempGruuAlgoritm.createKey("AES");
			}

			BitArray bitArray_I = new BitArray(48);
			String binaryStr = Long.toBinaryString(I);

			// creating 48 bit array...
			for (int j=0,i=47-binaryStr.length()+1;j<binaryStr.length();j++,i++){
				if (binaryStr.charAt(j)=='0') {
					bitArray_I.set(i, false);
				}else {
					bitArray_I.set(i, true);
				}			
			}
			logger.info("Final 48 Bit counter 'I': "+ bitArray_I.toString());

			//80 Bit Random Number :  D
			BitArray bitArray_D = new BitArray(80);
			
			Date date = new Date();
			Long dateTime = date.getTime();
			String str64 = Long.toBinaryString(dateTime);
			
			//creating 64 bits...
			for(int i =0,j=64-str64.length(); i<str64.length(); i++,j++){
				if (str64.charAt(i)=='0') {
					bitArray_D.set(j, false);
				}else {
					bitArray_D.set(j, true);
				}	
			}
			logger.info("Final 64   Bit D1: "+bitArray_D.toString());
			
			//creating 16  bits...
			SecureRandom rand = new SecureRandom();
			byte [] bs =  new byte [2];
			rand.nextBytes(bs);
			BitArray bitarray16 = new BitArray(16, bs);
			logger.info(" Final 16 Bit D2: "+ bitarray16);
	
			//creating 80 bits...
			for(int i =0,j=64; i<16; i++,j++){
				if(bitarray16.get(i)){
					bitArray_D.set(j, true);
				}else{
					bitArray_D.set(j, false);
				}
			}
			
			logger.info("Final 80 Bit Random 'D' (D1||D2) : "+ bitArray_D.toString());

			// Concatination OR creating 128 Bit :M

			BitArray bitArray_M = new BitArray(128);
			
			//for 48 bits...
			for(int i =0; i<bitArray_I.length() ; i++){
				if(bitArray_I.get(i)){
					bitArray_M.set(i, true);
				}else{
					bitArray_M.set(i, false);
				}
			}
			
			//for 80 bits...
			for(int i =0,j=48; i<80 ; i++,j++){
				if(bitArray_D.get(i)){
					bitArray_M.set(j, true);
				}else{
					bitArray_M.set(j, false);
				}
			}

			logger.info("Final 128 Bit 'M' : "+ bitArray_M.toString());


			// AES-ECB Algo
			byte  message [] =  bitArray_M.toByteArray();

			// Instantiate the cipher
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpecAes);
			byte[] encrypted = cipher.doFinal(message);

			//HMACSHA256 Start 
			Mac mac = Mac.getInstance("HMACSHA256");  
			mac.init(skeySpecHma);  
			byte[] digest = mac.doFinal(encrypted);  

			byte [] A = new byte[10];

			for(int i =0; i<A.length ; i++){
				A[i]=digest[i];
			}

			logger.info("Temp-Gruu : " + "tgruu."+base64Encoder.encode(encrypted)+base64Encoder.encode(A));

			I++;

			setTempGruu("sip:tgruu."+base64Encoder.encode(encrypted)+base64Encoder.encode(A));
			setKa(skeySpecHma.getEncoded());
			setKe(skeySpecAes.getEncoded());
			setCounter(I-1);
			return this;
		}

		public String getTempGruu() {
			return tempGruu;
		}

		public long getCounter() {
			return counter;
		}

		public byte[] getKa() {
			return Ka;
		}

		public byte[] getKe() {
			return Ke;
		}
		public String getKa_String() {
			StringBuffer  stringBuffer = new StringBuffer();
			  int i;
			  for(i =0; i<Ka.length-1 ; i++){
			    stringBuffer.append(Ka[i]+",");			    
			   }			  
			   stringBuffer.append(Ka[i]);			
			   return stringBuffer.toString();
		}

		public String getKe_String() {
			StringBuffer  stringBuffer = new StringBuffer();
			  int i;
			  for(i =0; i<Ke.length-1 ; i++){
			    stringBuffer.append(Ke[i]+",");			    
			   }			  
			   stringBuffer.append(Ke[i]);			
			   return stringBuffer.toString();
		}

		public void setKa(byte[] ka) {
			Ka = ka;
		}


		public void setKe(byte[] ke) {
			Ke = ke;
		}


		public void setCounter(long counter) {
			this.counter = counter;
		}

		public void setTempGruu(String tempGruu) {
			this.tempGruu = tempGruu;
		}

	}
}




