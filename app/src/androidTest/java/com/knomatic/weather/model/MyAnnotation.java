package com.knomatic.weather.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

/**
 *
 * @author T. Carlos Manuel Patiño Machado.
 *
 * @since Septiembre 2014
 *
 *	Funcional para extraer las anotaciones de las clases.
 */
public @interface MyAnnotation {

    String FieldName();

}
