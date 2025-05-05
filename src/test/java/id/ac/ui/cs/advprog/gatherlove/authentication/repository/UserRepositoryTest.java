package id.ac.ui.cs.advprog.gatherlove.authentication.repository;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByUsername_thenReturnUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByUsername(user.getUsername());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void whenExistsByUsername_thenReturnTrue() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByUsername("testuser");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    public void whenExistsByEmail_thenReturnTrue() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertThat(exists).isTrue();
    }
}
