package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CompleteProfileControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private CompleteProfileController controller;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void testGetCompleteProfilePage() {
        // Act
        String viewName = controller.getCompleteProfilePage(userId, model);
        
        // Assert
        assertEquals("profile/complete-profile", viewName);
        verify(model).addAttribute(eq("id"), eq(userId));
    }
}
