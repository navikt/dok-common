package no.nav.dok.validators;


import jakarta.validation.Payload;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExistsInFileSystemValidatorTest {

	@Test
	void shouldValidateExistingFilePathAndType() {
		var fileValidator = createAndInitializeValidator(Exists.ExistsInFileSystemType.FILE);
		assertTrue(fileValidator.isValid("src/test/resources/test-file-empty.txt", null));

		var directoryValidator = createAndInitializeValidator(Exists.ExistsInFileSystemType.DIRECTORY);
		assertTrue(directoryValidator.isValid("src/test/resources/", null));
	}

	@Test
	void shouldComplainExistingPathWrongType() {
		var fileValidator = createAndInitializeValidator(Exists.ExistsInFileSystemType.FILE);
		assertFalse(fileValidator.isValid("src/test/resources/", null));

		var directoryValidator = createAndInitializeValidator(Exists.ExistsInFileSystemType.DIRECTORY);
		assertFalse(directoryValidator.isValid("src/test/resources/test-file-empty.txt", null));

	}

	@Test
	void shouldComplainNotExistingFilePath() {
		var fileValidator = createAndInitializeValidator(Exists.ExistsInFileSystemType.FILE);
		assertFalse(fileValidator.isValid("src/test/resources/test-file-no-exists.txt", null));

		var directoryValidator = createAndInitializeValidator(Exists.ExistsInFileSystemType.DIRECTORY);
		assertFalse(directoryValidator.isValid("src/test/resources/smesources/", null));

	}

	private static ExistsInFileSystemValidator createAndInitializeValidator(Exists.ExistsInFileSystemType type) {
		var validator = new ExistsInFileSystemValidator();
		validator.initialize(new Exists() {
								 @Override
								 public Class<? extends Annotation> annotationType() {
									 return null;
								 }

								 @Override
								 public String message() {
									 return "";
								 }

								 @Override
								 public Class<?>[] groups() {
									 return new Class[0];
								 }

								 @Override
								 public Class<? extends Payload>[] payload() {
									 return new Class[0];
								 }

								 @Override
								 public ExistsInFileSystemType type() {
									 return type;
								 }
							 }
		);
		return validator;
	}
}