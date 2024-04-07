package kg.devcats.internlabs.admin.controller;

import kg.devcats.internlabs.admin.dto.response.ServicesDTO;
import kg.devcats.internlabs.admin.service.ServicesService;
import kg.devcats.internlabs.core.entity.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/services")
@Validated
public class ServiceController {
    private final ServicesService servicesService;
    public ServiceController(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('services.read')")
    public ResponseEntity<Page<ServicesDTO>> getAllServices(
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    @RequestParam(defaultValue = "name") String sortField,
                                    @RequestParam(defaultValue = "asc") String sortOrder,
                                    @RequestParam(required = false) String namePrefix,
                                    @RequestParam(required = false) BigDecimal minPrice,
                                    @RequestParam(required = false) BigDecimal maxPrice,
                                    @RequestParam(required = false) Boolean isCancelable) {
        Page<ServicesDTO> servicesPage;
        if ((namePrefix != null && !namePrefix.isEmpty()) || minPrice != null || maxPrice != null || isCancelable != null) {
            servicesPage = servicesService.findByFilters(namePrefix, minPrice, maxPrice, isCancelable, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortField));
        } else {
            servicesPage = servicesService.getServices(page, size, sortField, sortOrder);
        }

        if (servicesPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(servicesPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('services.read')")
    public ResponseEntity<ServicesDTO> getServiceById(@PathVariable Long id) {
        return servicesService.getServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('services.create')")
    public ResponseEntity<?> createServices(@RequestParam String name,
                                            @RequestParam BigDecimal min,
                                            @RequestParam BigDecimal max,
                                            @RequestParam boolean isCancelable,
                                            @RequestParam MultipartFile file) throws IOException{
        return ResponseEntity.ok(servicesService.createServices(name, min, max, isCancelable, file));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('services.create')")
    public ResponseEntity<Services> updateService(@PathVariable Long id,
                                                  @RequestParam String name,
                                                  @RequestParam BigDecimal min,
                                                  @RequestParam BigDecimal max,
                                                  @RequestParam boolean isCancelable,
                                                  @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(servicesService.updateService(id, name, min, max, isCancelable, file));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('services.delete')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        servicesService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
