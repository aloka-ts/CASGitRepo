package com.genband.ase.alc.alcml.jaxb;

import java.lang.annotation.*;
import java.lang.reflect.*;

/**
 * Annotations for ALC methods.  These annotations are used by the alcmlc compiler.
 */
public @interface ALCMLActionMethod {
		/**
		 * Returns the desired name of the element node for this action.
	     * @return desired name of the element node for this action.
        */
        String name() default ALCMLDefaults.JavaSourceName;
		/**
		 * Returns help that describes the workings and intent of the Service Method (element node).
	     * @return help that describes the workings and intent of the Service Method (element node).
        */
        String help() default ALCMLDefaults.NoHelpAvailable;
		/**
		 * Allows the user to indicate that this method can be called as a static on this class.
	     * @return true/false, Allows the user to indicate that this method can be called as a static on this class.
        */
        boolean asStatic() default false;
		/**
		 * Allows the user to indicate that this method could not possibly have results for the ALC.
	     * @return true/false, Allows the user to indicate that this method could not possibly have results for the ALC.
        */
        boolean canHaveResults() default true;
		/**
		 * Allows the user to indicate that this method is perfoming an atomic act.  And thus, the ActionCompleted/ActionFailed
		 * method(s) on context will be called within the functional completion of this method.
	     * @return true/false, Allows the user to indicate an atomic act.
        */
        boolean isAtomic() default false;
}

