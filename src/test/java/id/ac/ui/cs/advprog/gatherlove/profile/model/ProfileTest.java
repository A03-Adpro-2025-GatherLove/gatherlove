package id.ac.ui.cs.advprog.gatherlove.profile.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validProfile_shouldPassValidation() {
        Profile profile = new Profile();
        profile.setName("Alice");
        profile.setEmail("alice@example.com");
        profile.setPhoneNumber("081234567890");
        profile.setBio("Hi!");

        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_shouldFailValidation() {
        Profile profile = new Profile();
        profile.setName("   "); // blank
        profile.setEmail("alice@example.com");
        profile.setPhoneNumber("081234567890");

        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void invalidEmail_shouldFailValidation() {
        Profile profile = new Profile();
        profile.setName("Bob");
        profile.setEmail("not-an-email");
        profile.setPhoneNumber("08987654321");

        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void blankPhoneNumber_shouldFailValidation() {
        Profile profile = new Profile();
        profile.setName("Charlie");
        profile.setEmail("charlie@example.com");
        profile.setPhoneNumber(" "); // blank

        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber"));
    }

    @Test
    void nullBio_shouldPassValidation() {
        Profile profile = new Profile();
        profile.setName("Dina");
        profile.setEmail("dina@example.com");
        profile.setPhoneNumber("081234567890");
        profile.setBio(null);

        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);

        assertThat(violations).isEmpty();
    }
}
