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

import java.io.OutputStream;

import java.lang.reflect.AnnotatedElement;

import org.bn.coders.ElementInfo;
import org.bn.coders.IASN1TypesEncoder;

/**
 * @author jcfinley@users.sourceforge.net
 */
public abstract class ASN1Metadata implements IASN1Metadata
{
    public ASN1Metadata() {
        
    }
    //NTT Interop: NonAnswer Timer encoding as signed integer
    //The modifier has been changed from private to protected,
    //this is needed so that check could be applied on name
    //for application timer
    protected String name;

    public ASN1Metadata(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }    
    
    public void setParentAnnotated(AnnotatedElement parentAnnotated) {}    
}

