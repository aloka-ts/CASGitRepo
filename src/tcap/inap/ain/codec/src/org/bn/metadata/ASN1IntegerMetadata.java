/*
 * Copyright 2006 Abdulla G. Abdurakhmanov (abdulla.abdurakhmanov@gmail.com).
 * 
 * Licensed under the LGPL, Version 2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.gnu.org/copyleft/lgpl.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * With any your questions welcome to my e-mail 
 * or blog at http://abdulla-a.blogspot.com.
 */

package org.bn.metadata;

import java.io.InputStream;
import java.io.OutputStream;

import org.bn.annotations.ASN1Integer;
import org.bn.coders.DecodedObject;
import org.bn.coders.ElementInfo;
import org.bn.coders.IASN1TypesDecoder;
import org.bn.coders.IASN1TypesEncoder;

/**
 * @author jcfinley@users.sourceforge.net
 */
public class ASN1IntegerMetadata extends ASN1FieldMetadata
{
    public ASN1IntegerMetadata() {};
    
    public ASN1IntegerMetadata(String name)
    {
        super(name);
    }
    
    public ASN1IntegerMetadata(ASN1Integer annotation) {
        this(annotation.name());
    }    
    
    public int encode(IASN1TypesEncoder encoder, Object object, OutputStream stream, 
               ElementInfo elementInfo) throws Exception {
    	//NTT Interop: NonAnswer Timer encoding as signed integer
    	//The check is needed to encode only application timer as integer.
    	//encodeInteger API type casts integer to long which is why 
    	//getIntegerLength api of CoderUtil for long is called every time
    	//and this API was changed to get the length only for unsigned integers 
    	//This change was necessitated because of idp extension type encoding
    	//Now as we need to encode the Application Timer value as signed then 
    	//we need to call the getIntegerLength api of CoderUtil for int
    	if (this.name.equalsIgnoreCase("ApplicationTimer")){
    		return encoder.encodeSignedInteger(object, stream, elementInfo);
    	}else{
    		return encoder.encodeInteger(object, stream, elementInfo);
    	}
    	
    }    
    
    public DecodedObject decode(IASN1TypesDecoder decoder, DecodedObject decodedTag, Class objectClass, ElementInfo elementInfo, InputStream stream) throws Exception {
        return decoder.decodeInteger(decodedTag,objectClass,elementInfo,stream);
    }    
}
