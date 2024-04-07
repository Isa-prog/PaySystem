package kg.devcats.internlabs.admin.controller;

import kg.devcats.internlabs.admin.dto.response.LimitDTO;
import kg.devcats.internlabs.admin.service.LimitService;
import kg.devcats.internlabs.core.entity.Limit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/limits")
@Validated
public class LimitController {

    private final LimitService limitService;

    public LimitController(LimitService limitService) {
        this.limitService = limitService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('limit.read')")
    public List<LimitDTO> getAllLimits() {
        return limitService.getAllLimits();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('limit.read')")
    public ResponseEntity<LimitDTO> getLimitById(@PathVariable Long id) {
        Optional<LimitDTO> limit = limitService.getLimitById(id);
        return limit.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('limit.create')")
    public ResponseEntity<Limit> createLimit(@RequestBody Limit limit) {
        Limit createdLimit = limitService.createLimit(limit);
        return new ResponseEntity<>(createdLimit, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('limit.create')")
    public ResponseEntity<Limit> updateLimit(@PathVariable Long id, @RequestBody Limit updatedLimit) {
        Limit limit = limitService.updateLimit(id, updatedLimit);
        return (limit != null) ? new ResponseEntity<>(limit, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('limit.delete')")
    public ResponseEntity<Void> deleteLimit(@PathVariable Long id) {
        limitService.deleteLimit(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

