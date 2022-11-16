package com.genband.ase.alc.alcml.jaxb;

import java.lang.annotation.*;
import java.lang.reflect.*;

/**
 * Annotations for ALC method paramters.  These annotations are used by the alcmlc compiler.
 */
public @interface ALCMLMethodParameter {
		/**
		 * Returns the desired name of the attribute/element node for this parameter.
	     * @return the desired name of the attribute/element node for this parameter.
        */
        String name() default ALCMLDefaults.JavaSourceName;
		/**
		 * Returns help that describes the workings and intent of the Service Parameter (element node or attribute).
	     * @return help that describes the workings and intent of the Service Parameter (element node or attribute).
        */
        String help() default ALCMLDefaults.NoHelpAvailable;
		/**
		 * Allows the user to indicate that this parameter should be an attribute of the method element in schema definition.
	     * @return true/false, Allows the user to indicate that this parameter should be an attribute of the method element in schema definition.
        */
        boolean asAttribute() default false;
		/**
		 * Allows the user to indicate that this parameter should be required in a compliant xml instance.
	     * @return true/false, Allows the user to indicate that this parameter should be required in a compliant xml instance.
        */
        boolean required() default false;
		/**
		 * Returns the type for this parameter.
	     * @return the type for this parameter.
        */
        String type() default ALCMLDefaults.ALCMLExpression;
		/**
		 * Returns a default value for this parameter, xml compliant instance would subsequently not have to contain this attribute.
		 * For attribute type parameters only.
	     * @return a default value for this parameter, xml compliant instance would subsequently not have to contain this attribute.
        */
        String defaultValue() default ALCMLDefaults.NoDefaultAvailable;
}

