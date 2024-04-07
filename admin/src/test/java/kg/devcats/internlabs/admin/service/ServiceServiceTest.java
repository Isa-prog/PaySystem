package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.mapper.ServicesDTOMapper;
import kg.devcats.internlabs.admin.dto.response.ServicesDTO;
import kg.devcats.internlabs.core.entity.Services;
import kg.devcats.internlabs.core.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServicesDTOMapper servicesDTOMapper;

    @InjectMocks
    private ServicesService servicesService;

    @Mock
    private FileService fileService;

    @Test
    public void testGetServices() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        String sortOrder = "asc";
        List<Services> services = new ArrayList<>();
        services.add(new Services("ServiceName", BigDecimal.ONE, new BigDecimal(555), true, "logo.png"));
        services.add(new Services("ServiceName2", BigDecimal.ONE, new BigDecimal(555), true, "logo2.png"));
        Page<Services> servicesPage = new PageImpl<>(services);
        when(serviceRepository.findAll(any(PageRequest.class))).thenReturn(servicesPage);
        Page<ServicesDTO> resultPage = servicesService.getServices(pageNumber, pageSize, sortField, sortOrder);
        verify(serviceRepository, times(1)).findAll(PageRequest.of(pageNumber, pageSize, Sort.by(sortField).ascending()));
        assertEquals(services.size(), resultPage.getContent().size());
    }

    @Test
    public void testGetAllServices() {
        Services service1 = new Services("ServiceName", BigDecimal.ONE, new BigDecimal(555), true, "logo.png");
        Services service2 = new Services("ServiceName2", BigDecimal.ONE, new BigDecimal(555), true, "logo2.png");
        List<Services> servicesList = Arrays.asList(service1, service2);
        when(serviceRepository.findAll()).thenReturn(servicesList);
        ServicesDTO servicesDTO1 = new ServicesDTO(1L,"ServiceName", BigDecimal.ONE, new BigDecimal(555), true, "logo.png");
        ServicesDTO servicesDTO2 = new ServicesDTO(2L,"ServiceName2", BigDecimal.ONE, new BigDecimal(555), true, "logo2.png");
        List<ServicesDTO> expectedServicesDTOList = Arrays.asList(servicesDTO1, servicesDTO2);
        when(servicesDTOMapper.apply(service1)).thenReturn(servicesDTO1);
        when(servicesDTOMapper.apply(service2)).thenReturn(servicesDTO2);
        List<ServicesDTO> result = servicesService.getAllServices();
        assertEquals(expectedServicesDTOList.size(), result.size());
        assertEquals(expectedServicesDTOList, result);
    }

    @Test
    public void testGetServiceById() {
        Long serviceId = 1L;
        Services service = new Services("ServiceName", BigDecimal.ONE, new BigDecimal(555), true, "logo.png");
        ServicesDTO expectedServiceDTO = new ServicesDTO(1L,"ServiceName", BigDecimal.ONE, new BigDecimal(555), true, "logo.png");
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(servicesDTOMapper.apply(service)).thenReturn(expectedServiceDTO);
        Optional<ServicesDTO> result = servicesService.getServiceById(serviceId);
        assertTrue(result.isPresent());
        assertEquals(expectedServiceDTO, result.get());
    }

    @Test
    public void testGetNotFoundServiceById() {
        Long serviceId = 1L;
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());
        Optional<ServicesDTO> result = servicesService.getServiceById(serviceId);
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByFilters() {
        String namePrefix = "Service";
        BigDecimal minPrice = BigDecimal.valueOf(100);
        BigDecimal maxPrice = BigDecimal.valueOf(200);
        Boolean isCancelable = true;
        PageRequest pageable = PageRequest.of(0, 10);
        List<Services> servicesList = new ArrayList<>();
        servicesList.add(new Services("ServiceName", BigDecimal.ONE, new BigDecimal(555), true, "logo.png"));
        servicesList.add(new Services("ServiceName2", BigDecimal.ONE, new BigDecimal(555), true, "logo2.png"));
        Page<Services> servicesPage = new PageImpl<>(servicesList);
        when(serviceRepository.findByFilters(namePrefix, minPrice, maxPrice, isCancelable, pageable)).thenReturn(servicesPage);
        ServicesDTO servicesDTO1 = new ServicesDTO(1L,"ServiceName", BigDecimal.ONE, new BigDecimal(555), true, "logo.png");
        ServicesDTO servicesDTO2 = new ServicesDTO(2L,"ServiceName2", BigDecimal.ONE, new BigDecimal(555), true, "logo2.png");
        List<ServicesDTO> expectedServicesDTOList = Arrays.asList(servicesDTO1, servicesDTO2);
        when(servicesDTOMapper.apply(servicesList.get(0))).thenReturn(servicesDTO1);
        when(servicesDTOMapper.apply(servicesList.get(1))).thenReturn(servicesDTO2);
        Page<ServicesDTO> resultPage = servicesService.findByFilters(namePrefix, minPrice, maxPrice, isCancelable, pageable);
        verify(serviceRepository, times(1)).findByFilters(namePrefix, minPrice, maxPrice, isCancelable, pageable);
        assertEquals(expectedServicesDTOList.size(), resultPage.getContent().size());
    }

    @Test
    public void testDeleteService() {
        Long id = 1L;
        servicesService.deleteService(id);
        verify(serviceRepository, times(1)).deleteById(id);
    }
}
