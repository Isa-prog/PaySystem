package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.mapper.ServicesDTOMapper;
import kg.devcats.internlabs.admin.dto.response.ServicesDTO;
import kg.devcats.internlabs.core.entity.Services;
import kg.devcats.internlabs.core.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicesService {
    private final ServiceRepository serviceRepository;
    private final ServicesDTOMapper servicesDTOMapper;

    @Autowired
    FileService fileService;

    public ServicesService(ServiceRepository serviceRepository, ServicesDTOMapper servicesDTOMapper) {
        this.serviceRepository = serviceRepository;
        this.servicesDTOMapper = servicesDTOMapper;
    }

    public Page<ServicesDTO> getServices(int pageNumber, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Page<Services> servicesPage = serviceRepository.findAll(pageRequest);
        return servicesPage.map(client -> servicesDTOMapper.apply(client));
    }

    public List<ServicesDTO> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(servicesDTOMapper)
                .collect(Collectors.toList());
    }

    public Page<ServicesDTO> findByFilters(String namePrefix, BigDecimal minPrice, BigDecimal maxPrice, Boolean isCancelable,PageRequest pageable) {
        Page<Services> services = serviceRepository.findByFilters(namePrefix, minPrice, maxPrice, isCancelable, pageable);
        return services.map(service -> servicesDTOMapper.apply(service));
    }

    public Optional<ServicesDTO> getServiceById(Long id) {
        return serviceRepository.findById(id).map(servicesDTOMapper);
    }

    public Services createServices(String name, BigDecimal min, BigDecimal max, boolean isCancelable, MultipartFile file) throws IOException{
        Services services = new Services();
        services.setName(name);
        services.setMin(min);
        services.setMax(max);
        services.setIsCancelable(isCancelable);
        String cloudinaryUrl = fileService.storeFile(file);
        services.setLogo(cloudinaryUrl);

        return serviceRepository.save(services);
    }

    public Services updateService(Long id, String name, BigDecimal min, BigDecimal max, boolean isCancelable, MultipartFile file)throws IOException {
        Optional<Services> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {
            Services services = optionalService.get();
            services.setName(name);
            services.setMin(min);
            services.setMax(max);
            services.setIsCancelable(isCancelable);
            String cloudinaryUrl = fileService.storeFile(file);
            services.setLogo(cloudinaryUrl);
            return serviceRepository.save(services);
        } else {
            return null;
        }
    }


    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
