package no.nav.dok.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static no.nav.dok.validators.Exists.ExistsInFileSystemType.FILE;

/***
 * Validate that the value is a path to a file or directory that can be read by the application
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsInFileSystemValidator.class)
public @interface Exists {

	String message() default "Invalid path! Path must point to a readable file of type={type}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	ExistsInFileSystemType type() default FILE;

	enum ExistsInFileSystemType {
		FILE, DIRECTORY
	}
}
