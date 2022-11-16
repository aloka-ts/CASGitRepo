package com.agnity.map.util;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;

public class NonAsnArg {
	
    private static Logger logger = Logger.getLogger(NonAsnArg.class);	 
	
    public static byte[] tbcdStringEncoder(String digits){
		
        logger.info("tbcdStringEncoder:Enter");
        int len = digits.length() ;
        int size = (len + 1) / 2;
        byte[] out = new byte[size];

        for (int i = 0, j = 0; i < len ; i += 2, j++) {
            byte b1;
            byte b2 = 0xf;
            if(digits.charAt(i)== '*'){
                b1 = 0xa;
            }else if(digits.charAt(i) == '#'){
                b1 = 0xb;
            }else if(digits.charAt(i) == 'a'){
                b1 = 0xc ;
            }else if(digits.charAt(i) == 'b'){
                b1 = 0xd;
            }else if(digits.charAt(i) == 'c'){
                b1 = 0xe ;
            }else {
                b1 = (byte) (digits.charAt(i) - '0');
            }			
			
            if ((i + 1) < len) {
                if(digits.charAt(i+1)== '*'){
                    b2 = 0xa;
                }else if(digits.charAt(i+1) == '#'){
                    b2 = 0xb;
                }else if(digits.charAt(i+1) == 'a'){
                    b2 = 0xc ;
                }else if(digits.charAt(i+1) == 'b'){
                    b2 = 0xd;
                }else if(digits.charAt(i+1) == 'c'){
                    b2 = 0xe ;
                }else {
                    b2 = (byte) (digits.charAt(i + 1) - '0');
                }		
            }

            out[j] = (byte) ((b2 << 4) | b1);
        }
		logger.info("tbcdStringEncoder:Exit");
		return out;
    }
	
    public static final char TbcdCodes[] = { '0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', '*', '#', 'a', 'b', 'c', ' ' };
	
    public static String tbcdStringDecoder(byte data[], int offset) {
        logger.info("TbcdStringDecoder:Enter");
        int len = data.length ;
        char output[] = new char[2 * ( len - offset) ];
        int top = 0;
        
        for (int i = offset; i < len; i++) {
            output[top++] = TbcdCodes[data[i] & 0xf];
            output[top++] = TbcdCodes[(data[i] >> 4) & 0xf];
        }
        String tmpStr = new String(output);
        logger.info("TbcdStringDecoder:Exit");
        return (tmpStr.substring(0, tmpStr.length()).trim());
    }
}
