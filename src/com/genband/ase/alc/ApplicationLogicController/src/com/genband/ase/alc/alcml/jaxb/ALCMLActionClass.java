package com.genband.ase.alc.alcml.jaxb;

import java.lang.annotation.*;
import java.lang.reflect.*;

/**
 * Annotations for ALC classes.  These annotations are used by the alcmlc compiler.
 */
public @interface ALCMLActionClass {
		/**
		 * Returns the desired name of service class for presentation in schema definition.
	     * @return desired name of service class for presentation in schema definition.
        */
        String name();
		/**
		 * Returns help that describes the workings and intent of the Service Class.
	     * @return help that describes the workings and intent of the Service Class.
        */
        String help() default ALCMLDefaults.NoHelpAvailable;

		/**
		 * Returns a value that allows for custom types in generated xsd
	     * @return a value that allows for custom types in generated xsd
        */
        String literalXSDDefinition() default ALCMLDefaults.Unimplemented;
}
