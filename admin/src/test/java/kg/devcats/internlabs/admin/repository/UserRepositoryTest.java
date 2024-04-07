package kg.devcats.internlabs.admin.repository;

import kg.devcats.internlabs.core.entity.User;
import kg.devcats.internlabs.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetSuperAdmin() {
        Optional<User> userSuperAdmin = userRepository.findById(1L);
        assertTrue(userSuperAdmin.isPresent());
        assertEquals(userSuperAdmin.get().getUsername(),"super-admin");
    }

    @Test
    public void testSaveUser() {
        User user = new User("Akmal", "akmal@gmail.com", "qwert123");
        userRepository.save(user);
        assertThat(user).isNotNull();
        userRepository.deleteById(user.getId());
    }

    @Test
    public void testNotEmptyUsers() {
        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty());
    }
}
