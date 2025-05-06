package id.ac.ui.cs.advprog.gatherlove.authentication.repository;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserEntityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByUsername_thenReturnUser() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("password");
        entityManager.persist(userEntity);
        entityManager.flush();

        // When
        Optional<UserEntity> found = userRepository.findByUsername(userEntity.getUsername());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(userEntity.getUsername());
    }

    @Test
    public void whenExistsByUsername_thenReturnTrue() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("password");
        entityManager.persist(userEntity);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByUsername("testuser");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    public void whenExistsByEmail_thenReturnTrue() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("password");
        entityManager.persist(userEntity);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertThat(exists).isTrue();
    }
}
