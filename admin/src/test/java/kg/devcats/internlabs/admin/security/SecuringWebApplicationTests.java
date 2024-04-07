package kg.devcats.internlabs.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.devcats.internlabs.admin.payload.request.LoginRequest;
import kg.devcats.internlabs.admin.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecuringWebApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    private String jwt;

    @BeforeEach
    public void setup() {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("super-admin@gmail.com", "super-admin"));
        jwt = jwtUtils.generateJwtToken(authentication);
    }

    @Test
    public void authenticationWithValidData() throws Exception {
        LoginRequest validLoginRequest = new LoginRequest("super-admin@gmail.com", "super-admin");
        ObjectMapper objectMapper = new ObjectMapper();
        String validLoginRequestJson = objectMapper.writeValueAsString(validLoginRequest);
        mockMvc.perform(post("/api/admin/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.username").value("super-admin"))
                .andExpect(jsonPath("$.email").value("super-admin@gmail.com"));
    }

    @Test
    public void authenticationWithInvalidData() throws Exception {
        LoginRequest invalidLoginRequest = new LoginRequest("wrong@wrong.com", "wrong_password");
        ObjectMapper objectMapper = new ObjectMapper();
        String invalidLoginRequestJson = objectMapper.writeValueAsString(invalidLoginRequest);
        mockMvc.perform(post("/api/admin/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginRequestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testForCorrectJWT() throws Exception {
        mockMvc.perform(get("/api/admin/payments")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testForNoCorrectJWT() throws Exception {
        mockMvc.perform(get("/api/admin/payments")
                        .header("Authorization", "Bearer " + jwt + "no_correct_words"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testForSecurityFilterChain() throws Exception {
        mockMvc.perform(get("/api/admin/payments"))
                .andExpect(status().isUnauthorized());
    }
}
