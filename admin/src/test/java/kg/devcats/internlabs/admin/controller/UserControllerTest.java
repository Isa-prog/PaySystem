package kg.devcats.internlabs.admin.controller;

import kg.devcats.internlabs.admin.dto.response.UserDTO;
import kg.devcats.internlabs.admin.service.UserService;
import kg.devcats.internlabs.core.entity.ERole;
import kg.devcats.internlabs.core.entity.Role;
import kg.devcats.internlabs.core.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @MockBean
    private UserService userService;

    String json;

    @BeforeEach
    public void setup() {
        json = "{\"username\":\"akmal\",\"email\":\"akmal@gmail.com\",\"password\":\"qwert123\",\"roles\":[{\"id\":2,\"name\":\"ROLE_ADMIN\"}]}";
    }

    @Test
    @WithMockUser(authorities = "user.create")
    public void testCreateUser() throws Exception {
        json = "{\"username\":\"akmal\",\"email\":\"akmal@gmail.com\",\"password\":\"qwert123\",\"roles\":[{\"id\":4,\"name\":\"ROLE_MANAGER\"}]}";
        when(userService.createUser(any(User.class))).thenReturn(new User());
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "user.read")
    public void testCreateUserWithoutPermissions() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(new User());
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "user.create")
    public void testCreateSuperAdmin() throws Exception {
        json = "{\"username\":\"akmal\",\"email\":\"akmal@gmail.com\",\"password\":\"qwert123\",\"roles\":[{\"id\":1,\"name\":\"ROLE_SUPER_ADMIN\"}]}";
        when(userService.createUser(any(User.class))).thenReturn(new User());
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "user.create")
    public void testCreateAdminWithoutAdminCreatePermission() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(new User());
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"user.create", "admin.create"})
    public void testCreateAdminWithAdminCreatePermission() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(new User());
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }
}