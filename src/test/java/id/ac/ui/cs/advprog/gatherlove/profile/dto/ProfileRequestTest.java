package id.ac.ui.cs.advprog.gatherlove.profile.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileRequestTest {

    private ProfileRequest profileRequest;
    private final String TEST_FULL_NAME = "John Doe";
    private final String TEST_PHONE_NUMBER = "1234567890";
    private final String TEST_BIO = "This is my bio";

    @BeforeEach
    void setUp() {
        profileRequest = new ProfileRequest();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(profileRequest);
        assertNull(profileRequest.getFullName());
        assertNull(profileRequest.getPhoneNumber());
        assertNull(profileRequest.getBio());
    }

    @Test
    void testAllArgsConstructor() {
        profileRequest = new ProfileRequest(TEST_FULL_NAME, TEST_PHONE_NUMBER, TEST_BIO);
        
        assertEquals(TEST_FULL_NAME, profileRequest.getFullName());
        assertEquals(TEST_PHONE_NUMBER, profileRequest.getPhoneNumber());
        assertEquals(TEST_BIO, profileRequest.getBio());
    }

    @Test
    void testBuilder() {
        profileRequest = ProfileRequest.builder()
                .fullName(TEST_FULL_NAME)
                .phoneNumber(TEST_PHONE_NUMBER)
                .bio(TEST_BIO)
                .build();

        assertEquals(TEST_FULL_NAME, profileRequest.getFullName());
        assertEquals(TEST_PHONE_NUMBER, profileRequest.getPhoneNumber());
        assertEquals(TEST_BIO, profileRequest.getBio());
    }

    @Test
    void testGettersAndSetters() {
        profileRequest.setFullName(TEST_FULL_NAME);
        profileRequest.setPhoneNumber(TEST_PHONE_NUMBER);
        profileRequest.setBio(TEST_BIO);

        assertEquals(TEST_FULL_NAME, profileRequest.getFullName());
        assertEquals(TEST_PHONE_NUMBER, profileRequest.getPhoneNumber());
        assertEquals(TEST_BIO, profileRequest.getBio());
    }
    
    @Test
    void testEqualsAndHashCode() {
        ProfileRequest firstRequest = new ProfileRequest(TEST_FULL_NAME, TEST_PHONE_NUMBER, TEST_BIO);
        ProfileRequest secondRequest = new ProfileRequest(TEST_FULL_NAME, TEST_PHONE_NUMBER, TEST_BIO);
        
        // Test equals
        assertEquals(firstRequest, secondRequest);
        
        // Test hashCode
        assertEquals(firstRequest.hashCode(), secondRequest.hashCode());
    }
    
    @Test
    void testToString() {
        profileRequest = ProfileRequest.builder()
                .fullName(TEST_FULL_NAME)
                .phoneNumber(TEST_PHONE_NUMBER)
                .bio(TEST_BIO)
                .build();
                
        String toString = profileRequest.toString();
        
        assertTrue(toString.contains(TEST_FULL_NAME));
        assertTrue(toString.contains(TEST_PHONE_NUMBER));
        assertTrue(toString.contains(TEST_BIO));
    }
}
