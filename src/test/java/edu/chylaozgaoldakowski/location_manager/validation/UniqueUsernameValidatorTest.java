package edu.chylaozgaoldakowski.location_manager.validation;

import edu.chylaozgaoldakowski.location_manager.user.UserRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UniqueUsernameValidator Unit Tests")
class UniqueUsernameValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    private UniqueUsernameValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UniqueUsernameValidator(userRepository);
    }

    @Test
    @DisplayName("isValid should return true when username does not exist")
    void testIsValidUsernameDoesNotExist() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        // Act
        boolean result = validator.isValid("newuser", constraintValidatorContext);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByUsername("newuser");
    }

    @Test
    @DisplayName("isValid should return false when username already exists")
    void testIsValidUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act
        boolean result = validator.isValid("existinguser", constraintValidatorContext);

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByUsername("existinguser");
    }

    @Test
    @DisplayName("isValid should return true when username is null")
    void testIsValidNullUsername() {
        // Act
        boolean result = validator.isValid(null, constraintValidatorContext);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository, never()).existsByUsername(any());
    }

    @Test
    @DisplayName("isValid should return true when username is blank")
    void testIsValidBlankUsername() {
        // Act
        boolean result = validator.isValid("", constraintValidatorContext);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository, never()).existsByUsername(any());
    }

    @Test
    @DisplayName("isValid should return true when username contains only whitespace")
    void testIsValidWhitespaceUsername() {
        // Act
        boolean result = validator.isValid("   ", constraintValidatorContext);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository, never()).existsByUsername(any());
    }

    @Test
    @DisplayName("isValid should handle case-sensitive usernames correctly")
    void testIsValidCaseSensitive() {
        // Arrange
        when(userRepository.existsByUsername("TestUser")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean resultUpperCase = validator.isValid("TestUser", constraintValidatorContext);
        boolean resultLowerCase = validator.isValid("testuser", constraintValidatorContext);

        // Assert
        assertThat(resultUpperCase).isTrue();
        assertThat(resultLowerCase).isFalse();
        verify(userRepository).existsByUsername("TestUser");
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    @DisplayName("isValid should handle usernames with special characters")
    void testIsValidSpecialCharacters() {
        // Arrange
        when(userRepository.existsByUsername("user@123")).thenReturn(false);
        when(userRepository.existsByUsername("user.name")).thenReturn(true);

        // Act
        boolean resultNotExists = validator.isValid("user@123", constraintValidatorContext);
        boolean resultExists = validator.isValid("user.name", constraintValidatorContext);

        // Assert
        assertThat(resultNotExists).isTrue();
        assertThat(resultExists).isFalse();
    }

    @Test
    @DisplayName("isValid should handle very long usernames")
    void testIsValidLongUsername() {
        // Arrange
        String longUsername = "a".repeat(100);
        when(userRepository.existsByUsername(longUsername)).thenReturn(false);

        // Act
        boolean result = validator.isValid(longUsername, constraintValidatorContext);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByUsername(longUsername);
    }

    @Test
    @DisplayName("isValid should trim and validate usernames correctly")
    void testIsValidWithLeadingTrailingSpaces() {
        // Note: The validator checks isBlank(), not isEmpty(), so strings with only spaces return true
        // However, strings with non-blank content surrounded by spaces are checked as-is
        // Arrange
        when(userRepository.existsByUsername(" username ")).thenReturn(false);

        // Act
        boolean result = validator.isValid(" username ", constraintValidatorContext);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByUsername(" username ");
    }
}
