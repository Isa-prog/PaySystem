package kg.devcats.internlabs.admin.repository;

import kg.devcats.internlabs.core.entity.Services;
import kg.devcats.internlabs.core.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ServiceRepositoryTest {
    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    public void testGetClients() {
        List<Services> servicesList = serviceRepository.findAll();
        assertThat(servicesList).isNotNull();
    }
}
