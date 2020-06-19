package com.laces.core.form.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class FormAnnotations {

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FormData {
		String[] groups() default {};
		boolean isPublic() default false;
		String name() default "";
		boolean isDynamic() default false;
	}
}
