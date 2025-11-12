package edu.chylaozgaoldakowski.location_manager.validation;

import edu.chylaozgaoldakowski.location_manager.annotation.UniqueUsername;
import edu.chylaozgaoldakowski.location_manager.user.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final UserRepository userRepository;

    public UniqueUsernameValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (username == null || username.isBlank()) {
            return true; // handled by @NotBlank
        }
        return !userRepository.existsByUsername(username);
    }
}
