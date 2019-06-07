package com.laces.core.form.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class FormAnnotations {

	public enum FormType {
	    INPUT, OUTPUT, RULE, GENERAL, REGISTER
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Form {
		String formatType() default "";
		FormType settingsType() default FormType.RULE;
		boolean isPublic() default false;
	}
}
