package id.ac.ui.cs.advprog.gatherlove.profile.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProfileResponseTest {

    private ProfileResponse profileResponse;
    private final UUID TEST_ID = UUID.randomUUID();
    private final String TEST_FULL_NAME = "John Doe";
    private final String TEST_PHONE_NUMBER = "1234567890";
    private final String TEST_BIO = "This is my bio";

    @BeforeEach
    void setUp() {
        profileResponse = new ProfileResponse();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(profileResponse);
        assertNull(profileResponse.getId());
        assertNull(profileResponse.getFullName());
        assertNull(profileResponse.getPhoneNumber());
        assertNull(profileResponse.getBio());
    }

    @Test
    void testAllArgsConstructor() {
        profileResponse = new ProfileResponse(TEST_ID, TEST_FULL_NAME, TEST_PHONE_NUMBER, TEST_BIO);
        
        assertEquals(TEST_ID, profileResponse.getId());
        assertEquals(TEST_FULL_NAME, profileResponse.getFullName());
        assertEquals(TEST_PHONE_NUMBER, profileResponse.getPhoneNumber());
        assertEquals(TEST_BIO, profileResponse.getBio());
    }

    @Test
    void testBuilder() {
        profileResponse = ProfileResponse.builder()
                .id(TEST_ID)
                .fullName(TEST_FULL_NAME)
                .phoneNumber(TEST_PHONE_NUMBER)
                .bio(TEST_BIO)
                .build();

        assertEquals(TEST_ID, profileResponse.getId());
        assertEquals(TEST_FULL_NAME, profileResponse.getFullName());
        assertEquals(TEST_PHONE_NUMBER, profileResponse.getPhoneNumber());
        assertEquals(TEST_BIO, profileResponse.getBio());
    }

    @Test
    void testGettersAndSetters() {
        profileResponse.setId(TEST_ID);
        profileResponse.setFullName(TEST_FULL_NAME);
        profileResponse.setPhoneNumber(TEST_PHONE_NUMBER);
        profileResponse.setBio(TEST_BIO);

        assertEquals(TEST_ID, profileResponse.getId());
        assertEquals(TEST_FULL_NAME, profileResponse.getFullName());
        assertEquals(TEST_PHONE_NUMBER, profileResponse.getPhoneNumber());
        assertEquals(TEST_BIO, profileResponse.getBio());
    }

}
