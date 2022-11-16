package com.genband.m5.maps.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
   public @interface Static
{
   String name() default "";
   String description() default "";
}

 