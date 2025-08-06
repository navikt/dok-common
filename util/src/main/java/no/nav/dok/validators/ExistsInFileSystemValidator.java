package no.nav.dok.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExistsInFileSystemValidator implements ConstraintValidator<Exists, String> {

	Exists.ExistsInFileSystemType type;

	@Override
	public void initialize(Exists constraintAnnotation) {
		this.type = constraintAnnotation.type();
	}

	@Override
	public boolean isValid(String path, ConstraintValidatorContext constraintValidatorContext) {
		if (path == null || path.isEmpty()) {
			return true; // delegate null / empty value to existing @NotEmpty-validator
		}

		try {
			Path targetPath = Paths.get(path);
			if (Files.exists(targetPath) && Files.isReadable(targetPath)) {
				if (type == Exists.ExistsInFileSystemType.FILE) {
					return Files.isRegularFile(targetPath);
				} else {
					return Files.isDirectory(targetPath);
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
