package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.mapper.UserDTOMapper;
import kg.devcats.internlabs.admin.dto.response.UserDTO;
import kg.devcats.internlabs.core.entity.User;
import kg.devcats.internlabs.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserDTOMapper userDTOMapper;

    @Test
    public void testGetUsers() {
        List<User> users = Arrays.asList( new User(),new User(),new User());
        Page<User> userPage = new PageImpl<>(users);
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);
        Page<User> result = userService.getUsers(0, 10, "id", "asc");
        verify(userRepository, times(1)).findAll(PageRequest.of(0, 10, Sort.by("id").ascending()));
        assertEquals(userPage, result);
    }

    @Test
    public void testFindByFilters() {
        String usernamePrefix = "ak";
        String emailPrefix = "akmal";
        Integer roleId = 1;
        PageRequest pageable = PageRequest.of(0, 10);
        List<User> users = new ArrayList<>();
        users.add(new User("akmal", "Akmal@example.com", "qwert123"));
        users.add(new User("Erkin", "Erkin@example.com", "qwert123"));
        Page<User> usersPage = new PageImpl<>(users);
        when(userRepository.findByFilters(usernamePrefix, emailPrefix, roleId, pageable)).thenReturn(usersPage);
        Page<User> resultPage = userService.findByFilters(usernamePrefix, emailPrefix, roleId, pageable);
        verify(userRepository, times(1)).findByFilters(usernamePrefix, emailPrefix, roleId, pageable);
        assertEquals(users.size(), resultPage.getContent().size());
    }

    @Test
    public void testGetNoExistUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDTO userDTO = userService.getUserById(1L);
        assertNull(userDTO);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testEncodePassword() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        userService.createUser(user);
        assertTrue(user.getPassword().equals("encodedPassword"));
    }

    @Test
    public void testPageWithContent() {
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);
        Page<User> resultPage = userService.getUsers(0, 10, "username", "asc");
        assertNotNull(resultPage);
        assertNotNull(resultPage.getContent());
    }

    @Test
    public void testChangeUserPassword() {
        User user = new User();
        user.setId(1L);
        user.setPassword(passwordEncoder.encode("oldPassword"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("oldPassword"), eq(null))).thenReturn(true);
        userService.changeUserPassword(1L, "oldPassword", "newPassword");
        verify(userRepository).save(user);
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.delete(1L);
        verify(userRepository).delete(user);
    }
}

