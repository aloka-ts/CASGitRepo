package com.genband.ase.alc.alcml.jaxb;
import java.sql.Date;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import java.io.Serializable;
/**
 * This class encapsulates an ALCMLExpression.
 * An ALCMLExpression is one that's source is an <xmlschema>:string from an ALCML compliant XML Instance.
 * It can contain embeded (and recursive) ALC context variables in the form of:
 *		${<variableName>}
 * The possible resolution of this expression can be in three forms:
 * 		- as a string, wherein all variable will be taken from context and resolved (via a toString) to
 *        a string resultant.
 *      - as an object, wherein all variables will be taken from context and an attempt will be made to
 *		  resolve embeded objects to strings until an outer object is reached.
 *				Example:
 *					${LoopCounter}    <!-- if loop counter is an Integer, then the resultant will be an Integer -->
 *				but consider the following ALCMLExpression,
 *					${Level${Resource}}
 *						where,
 *							Resource - is a string literal of value DS0, DS1 ...
 *							LevelDS0 - is a complex type.
 *							LevelDS1 - is a complex type.
 *				The embedded ${Resource} will attempt to resolve to a string and processing will continue.
 *              The resultant will be the complex type.
 * 		- as an integer, wherein all variable will be taken from context and resolved (via a toString) to
 *        a string resultant and an integer will be generated from the string result.
 */
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class ALCMLExpression  implements Serializable 
{
	public ALCMLExpression(ServiceContext sc, String s)
	{
		this.sc = sc;
		/* When the value is string "null", it means that we need to make the variable null
		 * and not put value "null" in it. Hence the following piece of code has been
		 * modified.
		 * Reshu Chaudhary Bug ID 6753
		 */
		if(s.equals("null")){
			this.s = null ;
		}else{
			this.s = s;
		}
	}

	public static String toString(ServiceContext sc, String s)
	{
		if (s == null)
			return null;
		return (String)sc._ReplaceContextVariables(s, true);
	}

	public static Object toObject(ServiceContext sc, String s)
	{
		if (s == null)
			return null;
		return sc._ReplaceContextVariables(s, false);
	}

	public static Integer toInteger(ServiceContext sc, String s)
	{
		if (s == null)
			return null;
		return  new Integer((String)sc._ReplaceContextVariables(s, true));
	}

    public static double  toDouble(ServiceContext sc, String s)
    {
        if (s == null)
           return 0;
        return  (new Double((String)sc._ReplaceContextVariables(s, true))).doubleValue();
    }


    public static boolean toBoolean(ServiceContext sc, String s)
    {
        if (s == null)
           return false;
        return  (new Boolean((String)sc._ReplaceContextVariables(s, true))).booleanValue();
    }
  

    public static Date toDateTime(ServiceContext sc, String s)
    {
        if (s == null)
            return null;
        return  java.sql.Date.valueOf((String)sc._ReplaceContextVariables(s, true));
    }
       
    public static java.util.Date toJavaDateTime(ServiceContext sc, String s)
    {
        if (s == null)
           return null;
        return  new java.util.Date((String)sc._ReplaceContextVariables(s, true));
    }
      
    public Object toObject()
	{
		return sc._ReplaceContextVariables(s, false);
	}

	public String toString()
	{
		return (String)sc._ReplaceContextVariables(s, true);
	}

	public Integer toInteger()
	{
		return new Integer(this.toString());
	}

	public String toALCMLExpression()
	{
		return s;
	}
	
	public boolean toBoolean()
	{
		return  (new Boolean((String)sc._ReplaceContextVariables(s, true))).booleanValue();
	}

	private ALCMLExpression() {}

	private ServiceContext sc;
	private String s;
}
